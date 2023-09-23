import org.entermediadb.asset.importer.BaseImporter
import org.entermediadb.asset.util.Row
import org.openedit.data.*
import org.openedit.*
import org.openedit.data.*

class CsvImporter extends BaseImporter
{
	public String collectionid;
	
	public Object scrubValueIfNeeded(PropertyDetail inDetail, Object inValue)
	{
		if( inDetail.getId().equals("collectionid"))
		{
			return collectionid;
		}
		return inValue;
	}

}

String collectionid = context.getRequestParameter("collectionid");

CsvImporter csvimporter = new CsvImporter();
csvimporter.setModuleManager(moduleManager);
csvimporter.setContext(context);
csvimporter.setLog(log);
csvimporter.setMakeId(false);

csvimporter.addDbLookUp("currencytype");
csvimporter.addDbLookUp("expensetype");
csvimporter.addDbLookUp("paidfromaccount");

//csvimporter.addDbLookUp("collectiveproject");
csvimporter.collectionid = collectionid;
csvimporter.importData();
