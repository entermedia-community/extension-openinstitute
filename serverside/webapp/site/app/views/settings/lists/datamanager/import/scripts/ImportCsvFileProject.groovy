import org.entermediadb.asset.importer.BaseImporter
import org.entermediadb.asset.util.Row
import org.openedit.data.*
import org.openedit.*
import org.openedit.data.*

class CsvImporter extends BaseImporter
{
		protected Data findOrCreateById(String inTable, String id, String value)
		{
			Data data;
			String collid  =  "AX7gTBpbekpQ63X1oUTz";
			Searcher searcher = getSearcherManager().getSearcher(getSearcher().getCatalogId(), inTable);
			data = (Data) searcher.query().exact("parentcollectionid", collid).match("name",value).searchOne();
			if (data == null)
			{
				data = searcher.createNewData();
				data.setId(collid + id);
				data.setName(value);
				data.setValue("parentcollectionid", collid);				
				searcher.saveData(data, null);
			}
			return data;
		}

}

CsvImporter csvimporter = new CsvImporter();
csvimporter.setModuleManager(moduleManager);
csvimporter.setContext(context);
csvimporter.setLog(log);
csvimporter.setMakeId(false);
csvimporter.addDbLookUp("collectiveproject");
csvimporter.importData();
