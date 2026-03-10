
import org.apache.http.StatusLine
import org.apache.http.client.methods.CloseableHttpResponse
import org.entermediadb.asset.MediaArchive
import org.json.simple.JSONObject
import org.openedit.data.*
import org.openedit.util.HttpSharedConnection
import org.openedit.util.JSONParser

public void init()
{
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");

	HttpSharedConnection connection = new HttpSharedConnection();
	String baseurl = "https://openinstitute.org/site/mediadb";
	
	String entermediakey = "1106md5420940dd427e0a60de560b00f00de530d2380b93208d";

	String exportsearchtype = "librarycollection";		
	
	def search = '''{
		"page": "1",
		"hitsperpage": "2",
		"query": {
		  "terms": [
			{
			  "field": "communitytagcategory",
			  "operation": "exact",
			  "value": "3"
			}
		  ]
		}
	  }'''

	JSONParser json_parser = new JSONParser();
	JSONObject searchparams =json_parser.parse(search); 
	
	connection.addSharedHeader("X-tokentype", "entermedia")
	connection.addSharedHeader("X-token", entermediakey)
	
	String url = baseurl + "/services/lists/export/" + exportsearchtype
	
	CloseableHttpResponse response = connection.sharedPostWithJson(url, searchparams)
	
	StatusLine filestatus = response.getStatusLine();
	if (filestatus.getStatusCode() != 200)
	{
		//Problem
		log.info( filestatus.getStatusCode() + " URL issue " + " " + url);
		return null;
	}
	
	Searcher searcher = mediaArchive.getSearcher(exportsearchtype);
	Map responsemap = connection.parseJson(response)
	Collection results = responsemap.get("results");
	searcher.saveJson(results)
	
	//Loop auxiliar tables
	// uploads, assets, goals, tasks, chatterbox, userposts, librarycollectionusers 
	
	log.info("saved ${hits.size()}");
}

init();