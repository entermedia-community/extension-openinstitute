import org.entermediadb.asset.MediaArchive
import org.entermediadb.location.Position
import org.entermediadb.projects.*
import org.openedit.Data

import org.openedit.data.BaseSearcher
import org.openedit.data.*

public void init()
{
	MediaArchive mediaarchive = context.getPageValue("mediaarchive");
	
	Data communitytagcategory = context.getPageValue("communitytagcategory");
	
	QueryBuilder query = mediaarchive.query("librarycollection").exact("communitytagcategory",communitytagcategory.getId()).named("collections");
	Collection collections = query.search(context);
	if( !collections.isEmpty() )
	{
		context.putPageValue("collectionhits",collections);
		
		query = mediaarchive.query("collectiveproduct").hitsPerPage(20).orgroup("collectionid",collections).exact("producttype","8").named("products").sort("name");
		Collection hits = query.search(context);
	    context.putPageValue("hits",hits);
	}
	
}

init();