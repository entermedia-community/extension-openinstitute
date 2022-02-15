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
	
	public ArrayList<Map<String, Object>>   getTotalIncomesByDateRange(String inCollectionId, DateRange inDateRange)
	{
		Searcher incomesSearcher = getMediaArchive().getSearcher("transaction");
		Collection<Data> incomes = incomesSearcher.query().exact("collectionid", inCollectionId).search();
		//inDateRange
		
		ArrayList<Map<String, Object>>  incomesbyCurrency = null;
		
		//TODO: Group by currency and return array/Collection?

		return incomesbyCurrency;
		
	}
	
	public ArrayList<Map<String, Object>>   getIncomeTypesByDateRange(String inCollectionId, DateRange inDateRange)
	{
		Searcher incomesSearcher = getMediaArchive().getSearcher("transaction");
		return null;
	}
	
	public Map   getExpenseTypesByDateRange(String inCollectionId, DateRange inDateRange) 
	{
		Searcher expensesSearcher = getMediaArchive().getSearcher("collectiveexpense");
		QueryBuilder query = expensesSearcher.query();
		query.exact("collectionid", inCollectionId);
		HitTracker hits = expensesSearcher.search(query.getQuery());
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
		Searcher expenseSearcher = getMediaArchive().getSearcher("expense");
		//TODO: Group by currency and return array/Collection?
		
		return null;
	}
	
	public String getTotalExpenseByCurrency(String inCollectionId, String inCurrencyId, DateRange inDateRange) 
	{
		Searcher expenseSearcher = getMediaArchive().getSearcher("expense");
		return "";
	}
	
	

	
}
