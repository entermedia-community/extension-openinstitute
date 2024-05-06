import org.entermediadb.asset.importer.BaseImporter
import org.entermediadb.asset.util.Row
import org.openedit.Data
import org.openedit.data.*;
import org.openedit.hittracker.*;

class CsvImporter extends BaseImporter
	{
		protected void addProperties( Row inRow, Data inData)
		{
			super.addProperties( inRow, inData);
String path = inRow.get("Upload Source Path");

path = path.replace("X:\\TAE_Multimedia\\","").replace("\\","/");

inData.setValue("uploadsourcepath",path);
String keywords = inData.get("keywords");
if( keywords != null )
{
  keywords = keywords.replaceAll(",","|");
  inData.setValue("keywords",keywords);
}
		}
		public boolean skipRow(Row inTrow)
		{
			  String sourcepath = inTrow.get("Upload Source Path");
			  if( fieldLastNonSkipData != null && sourcepath.equals("\\") )
			  {
				  String appendcaption = inTrow.get("Description");
				  if( appendcaption != null)
				  {
					  String oldcaption = fieldLastNonSkipData.get("longcaption");
					  fieldLastNonSkipData.setValue("longcaption", oldcaption + "\n" + appendcaption);
				  }
				  return true;
			  }
			return false;
		}

	protected Data findExistingRecord(Row inRow)
	{
	    String sourcepath = inRow.get("Upload Source Path");
	    Data found = getSearcher().query().exact("uploadsourcepath",sourcepath).searchOne();
		return found;
	}


}



CsvImporter csvimporter = new CsvImporter();
csvimporter.setModuleManager(moduleManager);
csvimporter.setContext(context);
csvimporter.setLog(log);
csvimporter.setMakeId(false);

//TODO: Delete all PMT files
Searcher searcher = csvimporter.loadSearcher(context);
HitTracker hits = searcher.query().startsWith("uploadsourcepath","PMT/").search();
searcher.deleteAll(hits,null);

csvimporter.importData();
