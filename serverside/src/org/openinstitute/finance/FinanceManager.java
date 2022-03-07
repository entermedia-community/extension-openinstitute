package org.openinstitute.finance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermedia.transactions.TransactionManager;
import org.entermediadb.asset.MediaArchive;
import org.entermediadb.elasticsearch.SearchHitData;
import org.openedit.CatalogEnabled;
import org.openedit.Data;
import org.openedit.ModuleManager;
import org.openedit.data.QueryBuilder;
import org.openedit.data.Searcher;
import org.openedit.hittracker.HitTracker;
import org.openedit.hittracker.ListHitTracker;

public class FinanceManager  implements CatalogEnabled
{
	private static final Log log = LogFactory.getLog(TransactionManager.class);
	protected ModuleManager fieldModuleManager;
	protected MediaArchive fieldMediaArchive;
	protected String fieldCatalogId;
	protected DecimalFormat df = new DecimalFormat("#.00"); 

	public String getCatalogId()
	{
		return fieldCatalogId;
	}


	public void setCatalogId(String inCatalogId)
	{
		fieldCatalogId = inCatalogId;
	}


	public ModuleManager getModuleManager()
	{
		return fieldModuleManager;
	}


	public void setModuleManager(ModuleManager inModuleManager)
	{
		fieldModuleManager = inModuleManager;
	}
	
	public MediaArchive getMediaArchive()
	{
		if (fieldMediaArchive == null)
		{
			fieldMediaArchive = (MediaArchive)getModuleManager().getBean(getCatalogId(), "mediaArchive");
		}
		return fieldMediaArchive;
	}

	public void setMediaArchive(MediaArchive inMediaArchive)
	{
		fieldMediaArchive = inMediaArchive;
	}


	//get expenses from expenses db
	
	public Map  getTotalIncomesByDateRange(String inCollectionId, DateRange inDateRange)
	{
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		//Regular Income
		Searcher incomesSearcher = getMediaArchive().getSearcher("collectiveincome");
		QueryBuilder query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		HitTracker hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		Collection pageOfHits = hits.getPageOfHits();
		pageOfHits = new ArrayList(pageOfHits);
		
		
		for (Iterator iterator = pageOfHits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Double currencytotal = (Double) bycurrency.get(currency);
			if( currencytotal == null || currencytotal == 0.0)
			{
				currencytotal = 0.0;
				bycurrency.put(currency, currencytotal);
	
			}
			currencytotal = currencytotal + (Double)data.getValue("total");
			bycurrency.replace(currency, currencytotal);
		}
		
		//OI Donations (transaction table)
		incomesSearcher = getMediaArchive().getSearcher("transaction");
		query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		pageOfHits = hits.getPageOfHits();
		pageOfHits = new ArrayList(pageOfHits);
		
		
		for (Iterator iterator = pageOfHits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Double currencytotal = (Double) bycurrency.get(currency);
			if( currencytotal == null || currencytotal == 0.0)
			{
				currencytotal = 0.0;
				bycurrency.put(currency, currencytotal);
	
			}
			currencytotal = currencytotal + (Double)data.getValue("totalprice");
			bycurrency.replace(currency, currencytotal);
		}

		
		return bycurrency;
		
	}
	
	public Map getIncomeTypesByDateRange(String inCollectionId, DateRange inDateRange)
	{
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		//Regular Income
		Searcher incomesSearcher = getMediaArchive().getSearcher("collectiveincome");
		QueryBuilder query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		HitTracker hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		Collection pageOfHits = hits.getPageOfHits();
		pageOfHits = new ArrayList(pageOfHits);
		
	
		for (Iterator iterator = pageOfHits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Collection values = (Collection) bycurrency.get(currency);
			if( values == null)
			{
				values = new ListHitTracker();
				//values.add(data);
				bycurrency.put(currency, values);
			}
			values.add(data);
			
		}
		
		//OI Donations (transaction table)
		incomesSearcher = getMediaArchive().getSearcher("transaction");
		query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		pageOfHits = hits.getPageOfHits();
		pageOfHits = new ArrayList(pageOfHits);
		
		for (Iterator iterator = pageOfHits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Collection values = (Collection) bycurrency.get(currency);
			if( values == null)
			{
				values = new ListHitTracker();
				//values.add(data);
				bycurrency.put(currency, values);
			}
			values.add(data);
			
		}
		
		//summarize by IncomeType
		String currentcurrency = "";
		for (Map.Entry<String, Object> set :bycurrency.entrySet()) {
			Collection data = (Collection) set.getValue();
			HashMap<String, Object> currenttype = new HashMap<String, Object>();
			String currenttypeid = "";
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				SearchHitData row = (SearchHitData) iterator.next();
				currenttypeid = (String) row.getValue("incometype");
				
				SearchHitData values = (SearchHitData)currenttype.get(currenttypeid);
				if (values == null) {
					values = new SearchHitData();
					currenttype.put(currenttypeid, values);
					values.setValue("total", 0.0);
				}
				Double currenttotal = 0.0;
				if (row.getValue("incometype").equals("1")) {
					//OI Donation different total field
					currenttotal = (Double) row.getValue("totalprice");
				}
				else {
					currenttotal = (Double) row.getValue("total");
				}
				
				values.setValue("total", (Double) values.getValue("total") + currenttotal);
				values.setValue("currencytype", (String) row.getValue("currencytype"));
				values.setValue("incometype", currenttypeid);

			}
			bycurrency.replace(set.getKey(), currenttype);
			
		}
		
		return bycurrency;
	}
	
	
	public Map getExpenseTypesByDateRange(String inCollectionId, DateRange inDateRange) 
	{
		Searcher expensesSearcher = getMediaArchive().getSearcher("collectiveexpense");
		QueryBuilder query = expensesSearcher.query();
		query.exact("collectionid", inCollectionId);
		HitTracker hits = expensesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		Collection pageOfHits = hits.getPageOfHits();
		pageOfHits = new ArrayList(pageOfHits); 
		
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		for (Iterator iterator = pageOfHits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Collection values = (Collection) bycurrency.get(currency);
			if( values == null)
			{
				values = new ListHitTracker();
				//values.add(data);
				bycurrency.put(currency, values);
			}
			values.add(data);
			
		}
		//summarize by ExpenseType
		String currentcurrency = "";
		for (Map.Entry<String, Object> set :bycurrency.entrySet()) {
			Collection data = (Collection) set.getValue();
			HashMap<String, Object> currenttype = new HashMap<String, Object>();
			String currenttypeid = "";
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				SearchHitData row = (SearchHitData) iterator.next();
				currenttypeid = (String) row.getValue("expensetype");
				
				SearchHitData values = (SearchHitData)currenttype.get(currenttypeid);
				if (values == null) {
					values = new SearchHitData();
					currenttype.put(currenttypeid, values);
					values.setValue("total", 0.0);
				}
				
				values.setValue("total", (Double) values.getValue("total") + (Double) row.getValue("total"));
				values.setValue("currencytype", (String) row.getValue("currencytype"));
				values.setValue("expensetype", currenttypeid);

			}
			bycurrency.replace(set.getKey(), currenttype);
			
		}
			
		return bycurrency;
	}
	
		
	public ArrayList<Map<String, Object>>   getTotalExpensesByDateRange(String inCollectionId, DateRange inDateRange)
	{
		Searcher expenseSearcher = getMediaArchive().getSearcher("collectiveexpense");
		
		//TODO: Group by currency and return array/Collection?
		
		return null;
	}
	
	
	public Map  getTotalExpenseByCurrency(String inCollectionId, DateRange inDateRange)
	{
		Searcher expensesSearcher = getMediaArchive().getSearcher("collectiveexpense");
		QueryBuilder query = expensesSearcher.query();
		query.exact("collectionid", inCollectionId);
		HitTracker hits = expensesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		Collection pageOfHits = hits.getPageOfHits();
		pageOfHits = new ArrayList(pageOfHits);
		
		
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		for (Iterator iterator = pageOfHits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Double currencytotal = (Double) bycurrency.get(currency);
			if( currencytotal == null || currencytotal == 0.0)
			{
				currencytotal = 0.0;
				bycurrency.put(currency, currencytotal);
	
			}
			currencytotal = currencytotal + (Double)data.getValue("total");
			bycurrency.replace(currency, currencytotal);
		}
		
		return bycurrency;
		
	}
	
	
	public Map  getNetIncomeByCurrency(String inCollectionId, DateRange inDateRange)
	{
		Map incomebycurrency = getTotalIncomesByDateRange(inCollectionId, inDateRange);
		Map expensesbycurrency = getTotalExpenseByCurrency(inCollectionId, inDateRange);
		
		HashMap<String, Object> netIncome = new HashMap<String, Object>();
		
		Iterator<Map.Entry<String, Object>> it = incomebycurrency.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String, Object> pair = it.next();
		    netIncome.putIfAbsent(pair.getKey(), pair.getValue());
		}
		
		Iterator<Map.Entry<String, Object>> it2 = expensesbycurrency.entrySet().iterator();
		while (it2.hasNext()) {
		    Map.Entry<String, Object> pair = it2.next();
		    Double current = (Double) netIncome.get(pair.getKey());
		    if (current == null) {
		    	current = 0.0;
		    	netIncome.putIfAbsent(pair.getKey(), current - (Double)pair.getValue());
		    }
		    else {
		    	netIncome.replace(pair.getKey(), current - (Double)pair.getValue());
		    }
		}
		
		return netIncome;
	}
	

	
}
