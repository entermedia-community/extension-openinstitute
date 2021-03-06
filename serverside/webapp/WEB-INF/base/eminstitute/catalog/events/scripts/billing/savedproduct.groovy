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
	if(Boolean.parseBoolean(isrecurring))
	{
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
		productSearcher.saveData(product);
	}
}

init();