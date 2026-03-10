import org.entermediadb.asset.MediaArchive
import org.entermediadb.projects.*
import org.openedit.*
import org.openedit.hittracker.*
import org.openedit.util.HttpSharedConnection
import org.openedit.data.*

public void init()
{
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");

	HttpSharedConnection connection = new HttpSharedConnection();
	String baseurl = "https://openinstitute.org/site/mediadb";
	connection.getJson(baseurl + "/")
	
	Searcher searcher = mediaArchive.getSearcher("librarycollection");

	searcher.saveAllData(tosave,null);
	log.info("saved ${tosave.size()}");
}

init();