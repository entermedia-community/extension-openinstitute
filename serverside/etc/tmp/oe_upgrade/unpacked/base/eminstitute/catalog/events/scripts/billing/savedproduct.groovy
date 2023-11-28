package billing;

import org.entermediadb.asset.MediaArchive
import org.openedit.*
import org.openedit.data.Searcher

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher productSearcher = mediaArchive.getSearcher("collectiveproduct");
	Data product = context.getPageValue("data");
	
	String isrecurring = product.getValue("recurring");
	String nextbillon = product.getValue("nextbillon.value");
	if(nextbillon != null && Boolean.parseBoolean(isrecurring))
	{
		log.info("nextbillon not set, will be set now.");
		/*
		Calendar today = Calendar.getInstance();
		Calendar nextbillon = Calendar.getInstance();
		int billingday = product.getInt("billingday");
		if (product.getValue("nextbillon") != null) {
			nextbillon.setTime(product.getValue("nextbillon"));
			int currentday = nextbillon.get(Calendar.DAY_OF_MONTH);
			if (currentday == billingday) {
				return;
			}
			nextbillon.set(Calendar.DAY_OF_MONTH, billingday);
		}
		else 
		{
			int todayday = today.get(Calendar.DAY_OF_MONTH);
			if( billingday < todayday)  //Day passed already
			{
				nextbillon.add(Calendar.MONTH,1);
			}
			nextbillon.set(Calendar.DAY_OF_MONTH, billingday);
		}
		product.setValue("nextbillon", nextbillon.getTime());
		product.setValue("billingstatus", "active");
		*/
		Calendar today = Calendar.getInstance();
		Date billingdate = product.getValue("startbillingdate");
		if (billingdate == null) {
			billingdate = today.getTime();
		}
		
		int recurringperiod = product.getInt("recurringperiod");
		if(recurringperiod == null)
		{
			//recurring default to 1 month
			recurringperiod = 1;
			product.setValue("recurringperiod", "1"); //put it back to product
		}
		Calendar nextBillOn = Calendar.getInstance();
		nextBillOn.setTime(billingdate);
		int currentMonth = nextBillOn.get(Calendar.MONTH);
		nextBillOn.set(Calendar.MONTH, currentMonth + recurringperiod);
		product.setValue("nextbillon", nextBillOn.getTime());
		
		//always set product active?
		product.setValue("billingstatus", "active");
		
		productSearcher.saveData(product);
	}
} 

init();


