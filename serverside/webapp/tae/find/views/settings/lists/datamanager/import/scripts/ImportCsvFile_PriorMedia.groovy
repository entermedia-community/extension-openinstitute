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
			
			path = path.replace("\\","/");
			
			inData.setValue("uploadsourcepath",path);
			String keywords = inData.get("keywords");
			if( keywords != null )
			{
			  keywords = keywords.replaceAll(",","|");
			  inData.setValue("keywords",keywords);
			}
			
			  //initialize a row of notes
			  String notes = ""//Date\tRes\tShooter\tClip Numbers\tDescription\n";
			  notes = appendTo(notes,inRow,"Date");
			  notes = appendTo(notes,inRow,"Description");
			  notes = appendTo(notes,inRow,"Clip Numbers");
			  notes = appendTo(notes,inRow,"Res");
			  notes = appendTo(notes,inRow,"Shooter");
			  notes = notes + "\n";	 				  

			inData.setValue("notes",notes);
		}
		
		protected String appendTo(String notes,Row inRow,String inColumn)
		{
			String value = inRow.get(inColumn);
			if( value == null)
			{
				value="";
			}
			String newval = notes + value + "\t";
			return newval;
		}
		
		public boolean skipRow(Row inRow)
		{
			if( fieldLastNonSkipData == null)
			{
				//Dont skip first row
				return false;
			}
			
			  String name = inRow.get("Name");

			  if( name==null || name.isEmpty() ) //we will skip these
			  {
				  String appendcaption = inRow.get("Description");
				  if( appendcaption != null)
				  {
					  String oldcaption = fieldLastNonSkipData.get("longcaption");
					  fieldLastNonSkipData.setValue("longcaption", oldcaption + "\n" + appendcaption); //Description is clean
				  }
				  
			      //append a row of notes
				  String notes = fieldLastNonSkipData.get("notes");
				  notes = appendTo(notes,inRow,"Date");
				  notes = appendTo(notes,inRow,"Description");
				  notes = appendTo(notes,inRow,"Clip Numbers");
				  notes = appendTo(notes,inRow,"Res");
				  notes = appendTo(notes,inRow,"Shooter");
				  notes = notes + "\n";	 				  
				  fieldLastNonSkipData.setValue("notes",notes);
				  
				  return true;
			  }
			return false;
		}

	protected Data findExistingRecord(Row inRow)
	{
	    if (inRow.get("Name") == null)
	    {
	    	return null;
	    }
	
	    String sourcepath = inRow.get("Upload Source Path");
	    Data found = getSearcher().query().exact("uploadsourcepath",sourcepath).searchOne();
		return found;
	}
}

CsvImporter csvimporter = new CsvImporter();
csvimporter.setModuleManager(moduleManager);
csvimporter.setContext(context);
csvimporter.setLog(log);

// Delete all PMT files
Searcher searcher = csvimporter.loadSearcher(context);
HitTracker hits = searcher.query().exact("sourcefolder","l").search();
searcher.deleteAll(hits,null);

csvimporter.setMakeId(false);
csvimporter.addDbLookUp("sourcefolder");
csvimporter.addDbLookUp("foldercategory");

csvimporter.importData();
