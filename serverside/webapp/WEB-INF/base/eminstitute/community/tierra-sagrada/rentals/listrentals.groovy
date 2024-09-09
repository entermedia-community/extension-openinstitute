import org.entermediadb.asset.MediaArchive
import org.entermediadb.location.Position
import org.entermediadb.projects.*
import org.openedit.Data

import org.openedit.data.BaseSearcher
import org.openedit.data.*

public void init()
{
	MediaArchive mediaarchive = context.getPageValue("mediaarchive");
	
	//Data communitytagcategory = context.getPageValue("communitytagcategory");
	Collection communities = new ArrayList();
	communities.add("4");
	communities.add("4sm");
	communities.add("4cin");
	communities.add("4sma");
	communities.add("4za");
	
	QueryBuilder query = mediaarchive.query("librarycollection").orgroup("communitytagcategory",communities).named("collections");
	Collection collections = query.search(context);
	if( !collections.isEmpty() )
	{
		query = mediaarchive.query("collectiveproduct").hitsPerPage(20).orgroup("collectionid",collections).exact("producttype","5").named("products").sort("name");
		
		String indate = context.getRequestParameter("checkIn");
		if( indate != null)
		{
			query.not("blockeddates",indate); 
		}
		
		Collection hits = query.search(context);
	    context.putPageValue("hits",hits);
	}
	
}

init();