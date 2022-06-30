package billing;

import org.entermediadb.asset.MediaArchive
import org.openedit.*
import org.openedit.data.Searcher

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher productSearcher = mediaArchive.getSearcher("collectiveproduct");
	Data product = context.getPageValue("data");
	
	//recurring moved to invoice
	if( "true".equals( product.getValue("recurring")) )
	{
		int billingday = product.getInt("billingday");
		Calendar nextbillon = Calendar.getInstance();
		int todayday = today.get(Calendar.DAY_OF_MONTH);
		if( billingday < todayday)  //Day passed already
		{
			nextbillon.add(Calendar.MONTH,1);
		}
		nextbillon.set(Calendar.DAY_OF_MONTH,billingday);
		
		product.setValue("nextbillon", nextbillon);
		productSearcher.saveData(product);
	}
}

init();