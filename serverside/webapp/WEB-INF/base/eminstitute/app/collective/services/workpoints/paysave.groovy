package importing;

import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.MultiValued
import org.openedit.data.Searcher


public void init()
{
	MediaArchive archive = context.getPageValue("mediaarchive");

	String id = context.getRequestParameter("id.value");
	
	String collectionid = context.getRequestParameter("collectionid");
	
	Searcher searcher = archive.getSearcher("currencytransfer");
	Data data = searcher.searchById(id);
	data.setValue("currencytransferstatus","2"); //paid
	
	Data tosave = searcher.createNewData();
	tosave.setValue("date",new Date());
	tosave.setValue("user",context.getUserName());
	tosave.setValue("generatedbyscript","paysave");
	tosave.setValue("paymententitysourcetype","librarycollection"); //Project
	tosave.setValue("paymententitysource",collectionid); //
	tosave.setValue("currencytransferstatus","2"); //complete
	tosave.setName(data.getName()); //complete
	
	tosave.setValue("expensetype",data.getValue("expensetype")); 
	String currencytype = context.getRequestParameter("currencytype.value");
	tosave.setValue("currencytype",currencytype); 
		
	tosave.setValue("paymententitydesttype","user"); 
	String topay = data.get("paymententitysource");
	tosave.setValue("paymententitydest",topay); 
	//Multiply
	MultiValued currency = (MultiValued)archive.getCachedData("currencytype",currencytype);
	double totalpoints = data.getValue("total");
	double exchangerate = currency.getDouble("exchangetousd");
	double value = totalpoints * exchangerate;
	tosave.setValue("total",value); //complete
	searcher.saveData(tosave);

	searcher.saveData(data);
	
}

init();


