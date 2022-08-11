package importing;

import org.entermediadb.asset.Category
import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.data.Searcher


public void init()
{
	MediaArchive archive = context.getPageValue("mediaarchive");

	String sourcepath = context.getRequestParameter("sourcepath");
			
	
	Searcher searcher = archive.getSearcher("userupload");
	Data upload = searcher.createNewData();
	upload.setValue("uploaddate",new Date());
	upload.setValue("owner",context.getUserName());
	upload.setValue("librarycollection",context.getRequestParameter("librarycollection"));
	upload.setValue("usertags",context.getRequestParameters("usertags"));
	upload.setValue("title",context.getRequestParameters("title"));
	upload.setValue("longdescription",context.getRequestParameters("longdescription"));
	upload.setValue("collectiveproject",context.getRequestParameters("collectiveproject"));
	
	log.info("Script running" + sourcepath);
	Category defaultcat = archive.getCategorySearcher().createCategoryPath(sourcepath);
	upload.setValue("uploadcategory",defaultcat);
	searcher.saveData(upload);
		
	context.putPageValue("data",upload);
	context.putPageValue("searcher",searcher);
	
}

init();


