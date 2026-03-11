
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
		"hitsperpage": "100",
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
	
	  
  //local
	  /*
  baseurl = "http://localhost:8080/finder/mediadb"
  entermediakey = "adminmd5421c0af185908a6c0c40d50fd5e3f16760d5580bc"
  search = '''{
		"page": "1",
		"hitsperpage": "2",
		"query": {
		  "terms": [
			{
			  "field": "name",
			  "operation": "matches",
			  "value": "C*"
			}
		  ]
		}
	  }'''
  */

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
	
	def replacemap = ["AZqnEhttnrYY6bRGwPRw":"AZpgFZ_vQC-UlD66sXgS" ];
	
	//Loop auxiliar tables
	// uploads, assets, goals, tasks, chatterbox, userposts, librarycollectionusers
	
	for (map in results) {
		String collectionid = map.id;
		String newid = replacemap.get(collectionid)
		downloadData(connection, "projectgoal", "collectionid", collectionid, newid)
		downloadData(connection, "goaltask", "collectionid", collectionid, newid)
		downloadData(connection, "chatterbox", "collectionid", collectionid, newid)
		downloadData(connection, "channel", "collectionid", collectionid, newid)
		downloadData(connection, "userpost", "librarycollection", collectionid, newid)
		downloadData(connection, "librarycollectionusers", "collectionid", collectionid, newid)
		
	}
	
	for (map in results) {
		String collectionid = map.id;
		String newid =replacemap.get(collectionid)
		if (newid != null)
		{
			map.put("id", newid)
		}
	}
	
	searcher.saveJson(results)
	
	log.info("saved ${results.size()} Collections");
}

void downloadData(HttpSharedConnection inConnection, String inSearchtype, String inForeignkey, String inForeignid, String inNewcollectionid)
{
	
	def search = '''{
		"page": "1",
		"hitsperpage": "1000",
		"query": {
		  "terms": [
			{
			  "field": "'''+ inForeignkey + '''",
			  "operation": "exact",
			  "value": "'''+ inForeignid + '''"
			}
		  ]
		}
	  }'''
	//log.info("searching ${search}");
	
	JSONParser json_parser = new JSONParser();
	JSONObject searchparams =json_parser.parse(search);
	
	String baseurl = "https://openinstitute.org/site/mediadb";
	String url = baseurl + "/services/lists/export/" + inSearchtype
	
	CloseableHttpResponse response = inConnection.sharedPostWithJson(url, searchparams)
	
	StatusLine filestatus = response.getStatusLine();
	if (filestatus.getStatusCode() != 200)
	{
		//Problem
		log.info( filestatus.getStatusCode() + " URL issue " + " " + url);
		return null;
	}
	
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher searcher = mediaArchive.getSearcher(inSearchtype);
	Map responsemap = inConnection.parseJson(response)
	Collection results = responsemap.get("results");
	
	if (inNewcollectionid != null)
	{
		for (map in results) {
			map.put(inForeignkey, inNewcollectionid)
		}
	}
	
	searcher.saveJson(results)
	
	if (results.size() <= 0)
	{
		return
	}
	
	log.info("saved ${results.size()} ${inSearchtype} for key: ${inForeignid}");
	
	for (map in results) {
		String foreignkeyid = map.id;
		if ("goaltask".equals(inSearchtype))
		{
			//download subtypes: goaltaskuserrole
			downloadData(inConnection, "goaltaskuserrole", "goaltaskid", foreignkeyid, null)
		}
		else if ("chatterbox".equals(inSearchtype))
		{
			//download subtypes: attachments
			downloadData(inConnection, "chatterboxattachment", "messageid", foreignkeyid, null)
		}
	}
}


init();