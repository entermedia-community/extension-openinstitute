package importing;

import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.MultiValued
import org.openedit.data.Searcher


public void init()
{
	MediaArchive archive = context.getPageValue("mediaarchive");

 	Searcher searcher = archive.getSearcher("currencytransfer");
	
	data.setValue("date",new Date());
	data.setValue("user",context.getUserName());
	data.setValue("generatedbyscript","walletsendsave");
	data.setValue("paymententitysourcetype","user"); //Project
	data.setValue("paymententitysource",context.getUserName()); //
	data.setValue("currencytransferstatus","1"); //pending
	searcher.saveData(data);
	
}

init();


