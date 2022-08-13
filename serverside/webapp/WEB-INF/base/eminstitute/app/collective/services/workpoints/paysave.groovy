package importing;

import org.entermediadb.asset.MediaArchive
import org.entermediadb.asset.util.MathUtils
import org.openedit.Data
import org.openedit.MultiValued
import org.openedit.data.Searcher


public void init()
{
	MediaArchive archive = context.getPageValue("mediaarchive");

	String id = context.getRequestParameter("id.value");
	
	String collectionid = context.getRequestParameter("collectionid");
	Data librarycol = mediaarchive.getCachedData("librarycollection",collectionid);
	
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
	
	Double dollarsperpoints = librarycol.getDouble("dollarsperpoints");
	if( dollarsperpoints  == null)
	{
		dollarsperpoints = 1.0;
	}
	double dollars = totalpoints * dollarsperpoints;
	
	double exchangerate = currency.getDouble("exchangetousd"); //7.5
	
	double incurrency = dollars * exchangerate;
	tosave.setValue("total",incurrency); 
	searcher.saveData(tosave);

	searcher.saveData(data);
	
}

init();


