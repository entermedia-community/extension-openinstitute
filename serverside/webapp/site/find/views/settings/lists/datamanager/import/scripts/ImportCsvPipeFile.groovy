import org.entermediadb.asset.importer.BaseImporter
import org.entermediadb.asset.util.*
import org.openedit.Data

class CsvImporter extends BaseImporter
{
	/**
	 * This is an example of making a field lower case
	 */
	protected void addProperties( Row inRow, Data inData)
	{
		super.addProperties( inRow, inData);
		//createLookUp(inSearcher.getCatalogId(),inData,"Division","val_divisions");
	}

	protected void createMetadata(Header inHeader)
	{
		List newnames = new ArrayList();
		for (Iterator iterator = inHeader.getHeaderNames().iterator(); iterator.hasNext();)
		{
			String header = (String) iterator.next();
			
			if(header.contains(".")){ //TODO: What about english and spanish language imports?
				continue;
			}
			if( header == "height")
			{
				header = "pubheight";
			}
			else if( header == "width")
			{
				header = "pubwidth";
			} 
			else if( header == "Pub_Date")
			{
				header = "entity_date";
			}
			else if( header == "Title")
			{
				header = "name";
			}
			else if( header == "Description")
			{
				header = "longcaption";
			}
			else if( header == "Series")
			{
				header = "entityserie";  //entity
			}
			else if( header == "Instrument")
				{
					header = "publicationinstrument";
				}
			else if( header == "Format")
				{
					header = "publicationformat";  
				}
				else if( header == "Genre")
					{
						header = "publicationgenre";  
					}
					else if( header == "Level")
						{
							header = "publicationlevel";
						}
			else if( header == "Pub-Item")
				{
					header = "id";
				}
			
			
			newnames.add(header);
		}
		

		inHeader.getHeaderNames().clear();
		inHeader.setHeaders(newnames);
		
		super.createMetadata(inHeader);
	}
}

CsvImporter csvimporter = new CsvImporter();

csvimporter.setSeparator("|".charAt(0));

csvimporter.setModuleManager(moduleManager);
csvimporter.setContext(context);
csvimporter.setLog(log);


csvimporter.addDbLookUp("publicationinstrument");
csvimporter.addDbLookUp("publicationformat");
csvimporter.addDbLookUp("publicationgenre");
csvimporter.addDbLookUp("publicationlevel");

csvimporter.importData();
