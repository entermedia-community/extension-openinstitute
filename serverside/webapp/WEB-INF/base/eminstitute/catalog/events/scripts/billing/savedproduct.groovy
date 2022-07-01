package billing;

import org.entermediadb.asset.MediaArchive
import org.openedit.*
import org.openedit.data.Searcher

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher productSearcher = mediaArchive.getSearcher("collectiveproduct");
	Data product = context.getPageValue("data");
	
	//recurring moved to invoice
	String isrecurring = product.getValue("recurring");
	if(Boolean.parseBoolean(isrecurring)  && product.getValue("nextbillon") == null)
	{
		Calendar today = Calendar.getInstance();
		int billingday = product.getInt("billingday");
		
		Calendar nextbillon = Calendar.getInstance();
		int todayday = today.get(Calendar.DAY_OF_MONTH);

		if( billingday < todayday)  //Day passed already
		{
			nextbillon.add(Calendar.MONTH,1);
		}
		
		nextbillon.set(Calendar.DAY_OF_MONTH, billingday);
		
		
		product.setValue("billingstatus", "active");
		
		product.setValue("nextbillon", nextbillon.getTime());
		productSearcher.saveData(product);
	}
}

init();