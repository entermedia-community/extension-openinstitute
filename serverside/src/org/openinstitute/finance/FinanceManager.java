package org.openinstitute.finance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermedia.transactions.TransactionManager;
import org.entermediadb.asset.MediaArchive;
import org.entermediadb.elasticsearch.SearchHitData;
import org.openedit.CatalogEnabled;
import org.openedit.ModuleManager;
import org.openedit.MultiValued;
import org.openedit.data.BaseData;
import org.openedit.data.QueryBuilder;
import org.openedit.data.Searcher;
import org.openedit.hittracker.HitTracker;
import org.openedit.hittracker.ListHitTracker;
import org.openedit.hittracker.SearchQuery;

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
	
	public Map  getTotalIncomesByDateRange(String inCollectionId, DateRange inDateRange, String topicid)
	{
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		//Regular Income
		Searcher incomesSearcher = getMediaArchive().getSearcher("collectiveincome");
		QueryBuilder query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		if( topicid != null)
		{
			query.exact("collectiveproject",topicid);
		}
		
		addDateRange(query,"date",inDateRange);
		HitTracker hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
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
		if( topicid != null)
		{
			query.exact("collectiveproject",topicid);
		}
		addDateRange(query,"paymentdate",inDateRange);
		hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
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

		
		//Paid Invoices
		incomesSearcher = getMediaArchive().getSearcher("collectiveinvoice");
		query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		if( topicid != null)
		{
			//TODO: Add topics to invoices query.exact("collectiveproject",topicid);
		}
        query.exact("paymentstatus","paid");
		addDateRange(query,"invoicepaidon",inDateRange);
		hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			if( currency == null)
			{
				currency = "1";
			}
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
	
	public Map getIncomeTypesByDateRange(String inCollectionId, DateRange inDateRange, String topicid)
	{
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		//Regular Income
		Searcher incomesSearcher = getMediaArchive().getSearcher("collectiveincome");
		QueryBuilder query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		if( topicid != null)
		{
			query.exact("collectiveproject",topicid);
		}

		addDateRange(query,"date",inDateRange);

		HitTracker hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
	
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
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
		if( topicid != null)
		{
			query.exact("collectiveproject",topicid);
		}

		addDateRange(query,"paymentdate",inDateRange);

		hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
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

		
		//Paid Invoices
		incomesSearcher = getMediaArchive().getSearcher("collectiveinvoice");
		query = incomesSearcher.query();
		query.exact("collectionid", inCollectionId);
		if( topicid != null)
		{
			//TODO: Add topics to invoices query.exact("collectiveproject",topicid);
		}
        query.exact("paymentstatus","paid");
		addDateRange(query,"invoicepaidon",inDateRange);
		hits = incomesSearcher.search(query.getQuery());
		hits.setHitsPerPage(1000);
		
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency =  "1";//(String) data.getValue("currencytype");  //TODO: Support currencies
			
			Collection values = (Collection) bycurrency.get(currency);
			if( values == null)
			{
				values = new ListHitTracker();
				//values.add(data);
				bycurrency.put(currency, values);
			}
			values.add(data);
			
		}

		
		
		//summarize by Currency
		String currentcurrency = "";
		for (Map.Entry<String, Object> set :bycurrency.entrySet()) {
			Collection data = (Collection) set.getValue();
			HashMap<String, Object> currenttype = new HashMap<String, Object>();
			String currenttypeid = "";
			for (Iterator iterator = data.iterator(); iterator.hasNext();) {
				SearchHitData row = (SearchHitData) iterator.next();
				String incometype = (String) row.getValue("incometype");
				if( incometype == null)
				{
					incometype = "6"; //Invoices
				}
				BaseData values = (BaseData)currenttype.get(incometype);
				if (values == null) {
					values = new BaseData();
					values.setValue("total", 0.0);
					currenttype.put(incometype, values);
				}
				Double 	currenttotal = (Double) row.getValue("totalprice");
				if(  currenttotal == null )
				{
					currenttotal = (Double) row.getValue("total");
				}
				Double subtotal = (Double)values.getValue("total");
				if( subtotal == null)
				{
					subtotal = 0D;
				}
				if( currenttotal == null)
				{
					currenttotal = 0D;
				}
				values.setValue("total",  subtotal + currenttotal);
				currenttypeid = (String) row.getValue("currencytype");
				if( currenttypeid == null )
				{
					currenttypeid = "1"; //USD
				}
				values.setValue("currencytype", currenttypeid);
				values.setValue("incometype", incometype);

			}
			bycurrency.replace(set.getKey(), currenttype);
			
		}
		
		return bycurrency;
	}
	
	
	public Map getExpenseTypesByDateRange(String inCollectionId, DateRange inDateRange, String topicid) 
	{
		Searcher expensesSearcher = getMediaArchive().getSearcher("collectiveexpense");
		QueryBuilder query = expensesSearcher.query();
		query.exact("collectionid", inCollectionId);
		if( topicid != null)
		{
			query.exact("collectiveproject",topicid);
		}
		addDateRange(query,"date",inDateRange);

		
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
		rebuildByExpenseType(bycurrency);
			
		return bycurrency;
	}


	protected void rebuildByExpenseType(HashMap<String, Object> bycurrency)
	{
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
				
				values.setValue("total", addUp((Double) values.getValue("total") , (Double) row.getValue("total")));
				values.setValue("currencytype", (String) row.getValue("currencytype"));
				values.setValue("expensetype", currenttypeid);

			}
			bycurrency.replace(set.getKey(), currenttype);
			
		}
	}
	
		
	private Double addUp(Double inValue, Double inValue2)
	{
		if(inValue == null)
		{
			inValue = 0D;
		}
		if(inValue2 == null)
		{
			inValue2 = 0D;
		}
		
		return inValue + inValue2;
	}


	public ArrayList<Map<String, Object>>   getTotalExpensesByDateRange(String inCollectionId, DateRange inDateRange)
	{
		Searcher expenseSearcher = getMediaArchive().getSearcher("collectiveexpense");
		
		//TODO: Group by currency and return array/Collection?
		
		return null;
	}
	
	
	public Map  getTotalExpenseByCurrency(String inCollectionId, DateRange inDateRange, String topicid)
	{
		Searcher expensesSearcher = getMediaArchive().getSearcher("collectiveexpense");
		QueryBuilder query = expensesSearcher.query();
		query.exact("collectionid", inCollectionId);
		if( topicid != null)
		{
			query.exact("collectiveproject",topicid);
		}
		addDateRange(query,"date",inDateRange);

		HitTracker hits = expensesSearcher.search(query.getQuery());
		hits.enableBulkOperations();
		
		HashMap<String, Object> bycurrency = new HashMap<String, Object>();
		
		for (Iterator iterator = hits.iterator(); iterator.hasNext();) {
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			if(currency == null)
			{
				currency = "1";
			}
			Double currencytotal = (Double) bycurrency.get(currency);
			if( currencytotal == null)
			{
				currencytotal = 0.0;
				bycurrency.put(currency, currencytotal);
	
			}
			Double total = (Double)data.getValue("total");
			currencytotal = addUp(currencytotal , total);
			bycurrency.put(currency, currencytotal);
		}
		
		return bycurrency;
		
	}
	
	
	public QueryBuilder addDateRange(QueryBuilder inQuery, String inField ,DateRange inDateRange)
	{
		if( inDateRange == null || inDateRange.isAllTime())
		{
			return inQuery;
		}
		inQuery.between(inField, inDateRange.getStartDate(), inDateRange.getEndDate());
		return inQuery;
	}


	public Map  getNetIncomeByCurrency(String inCollectionId, DateRange inDateRange, String topicid)
	{
		Map incomebycurrency = getTotalIncomesByDateRange(inCollectionId, inDateRange, topicid);
		Map expensesbycurrency = getTotalExpenseByCurrency(inCollectionId, inDateRange, topicid);
		
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
	
	public List<BankTransaction> getAllTransactionByBank(String inBankId, DateRange inDateRange)
	{
		List<BankTransaction> transactions = new ArrayList();


		HitTracker tracker = null;
		Searcher incomesSearcher = null;
		if( "1".equals(inBankId) )
		{
			incomesSearcher = getMediaArchive().getSearcher("transaction");
			tracker = addDateRange(incomesSearcher.query(),"paymentdate",inDateRange).search();
			addAll(incomesSearcher.getSearchType(),tracker,transactions);

			incomesSearcher = getMediaArchive().getSearcher("collectiveinvoice");
			QueryBuilder query = addDateRange(incomesSearcher.query(),"invoicepaidon",inDateRange);
			tracker = query.exact("paymentstatus","paid").search();
			addAll(incomesSearcher.getSearchType(),tracker,transactions);

		}
		incomesSearcher = getMediaArchive().getSearcher("collectiveincome");
		QueryBuilder query = addDateRange(incomesSearcher.query(),"date",inDateRange);
		tracker = query.exact("paidfromaccount",inBankId).search();
		addAll(incomesSearcher.getSearchType(),tracker,transactions);


		incomesSearcher = getMediaArchive().getSearcher("collectiveexpense");
		query = addDateRange(incomesSearcher.query(),"date",inDateRange);
		tracker = query.exact("ispaid","true").exact("paidfromaccount",inBankId).search();
		addAll(incomesSearcher.getSearchType(),tracker,transactions);

		//sort
		Collections.sort(transactions, new Comparator<BankTransaction>()
		{
			public int compare(BankTransaction arg0, BankTransaction arg1) 
			{
				int i = arg1.getDate().compareTo(arg0.getDate());
				return i;
			};
		});
		return transactions;
	}


	protected void addAll(String inSearchType, HitTracker inTracker, List<BankTransaction> inTransactions)
	{
		for (Iterator iterator = inTracker.iterator(); iterator.hasNext();)
		{
			MultiValued hit = (MultiValued) iterator.next();
			BankTransaction transaction = new BankTransaction(inSearchType,hit);
			inTransactions.add(transaction);
			
		}
		
	}

	public HashMap<String, Object>   getTransfersByCurrencyForEntity(String inEntityId, DateRange inDateRange)
	{
		HitTracker hits = getAllTransfersForEntity(inEntityId, inDateRange);

		HashMap<String, Object> bycurrency = new HashMap<String, Object>();

		for (Iterator iterator = hits.iterator(); iterator.hasNext();) 
		{
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Double currencytotal = (Double) bycurrency.get(currency);
			if( currencytotal == null || currencytotal == 0.0)
			{
				currencytotal = 0.0;
				bycurrency.put(currency, currencytotal);
			}
			
			if( currency.equals("2") &&  "2".equals(data.get("currencytransferstatus") ) ) //Points are paid so dont add em
			{
				//Dont add to the total
			}
			else
			{
				String source = data.get("paymententitysource");
				if( !currency.equals("2") && source.equals(inEntityId))
				{
					currencytotal = currencytotal - (Double)data.getValue("total");
				}
				else
				{
					currencytotal = currencytotal + (Double)data.getValue("total");
				}
			}
			bycurrency.replace(currency, currencytotal);
		}
		
		return bycurrency;
	}

	public Map  getTransfersByExpensesForEntity(String inEntityId, DateRange inDateRange)
	{
		HashMap<String, Object>  bycurrency = getTransfersByCurrencyForEntity(inEntityId,inDateRange);
		rebuildByExpenseType(bycurrency);
		return bycurrency;
	}
	public HitTracker getPendingTransfersForEntity(String inEntityId, DateRange inDateRange)
	{
		HitTracker tracker = getTransfersForEntity(inEntityId, null, "1", inDateRange);
		return tracker;

	}
	public HitTracker getAllTransfersForEntity(String inEntityId, DateRange inDateRange)
	{
		HitTracker tracker = getTransfersForEntity(inEntityId, null, null, inDateRange);
		return tracker;
	}
	public HitTracker getTransfersForEntity(String inEntityId, String currencytype, String currencytransferstatus, DateRange inDateRange)
	{
		Searcher incomesSearcher = getMediaArchive().getSearcher("currencytransfer");
		QueryBuilder query = incomesSearcher.query();
		addDateRange(query,"date",inDateRange);
		
		SearchQuery finalq = query.getQuery();

		QueryBuilder query2 = incomesSearcher.query();
		query2.exact("paymententitysource",inEntityId);
		query2.exact("paymententitydest",inEntityId);
		query2.or();

		finalq.addChildQuery(query2.getQuery());

		if( currencytype != null)
		{
			finalq.addExact("currencytype",currencytype);
		}

		if( currencytransferstatus != null)
		{
			finalq.addExact("currencytransferstatus",currencytransferstatus);
		}
		
		finalq.addSortBy("dateDown");
		HitTracker hits = incomesSearcher.search(finalq);
		hits.setHitsPerPage(1000);
		return hits;
	}
	
	public HashMap<String, Object>   getPointsForEntity(String inEntityId, DateRange inDateRange)
	{
		HitTracker hits = getAllTransfersForEntity(inEntityId, inDateRange);

		HashMap<String, Object> bycurrency = new HashMap<String, Object>();

		for (Iterator iterator = hits.iterator(); iterator.hasNext();) 
		{
			SearchHitData data = (SearchHitData) iterator.next();
			String currency = (String) data.getValue("currencytype");
			
			Double currencytotal = (Double) bycurrency.get(currency);
			if( currencytotal == null || currencytotal == 0.0)
			{
				currencytotal = 0.0;
				bycurrency.put(currency, currencytotal);
			}
			
			if( currency.equals("2") &&  "2".equals(data.get("currencytransferstatus") ) ) //Points are paid so dont add em
			{
				//Dont add to the total
			}
			else
			{
				String source = data.get("paymententitysource");
				if( !currency.equals("2") && source.equals(inEntityId))
				{
					currencytotal = currencytotal - (Double)data.getValue("total");
				}
				else
				{
					currencytotal = currencytotal + (Double)data.getValue("total");
				}
			}
			bycurrency.replace(currency, currencytotal);
		}
		
		return bycurrency;
	}

}
