package org.openinstitute.server;

import java.util.Collection;

import org.entermediadb.asset.MediaArchive;
import org.entermediadb.asset.modules.BaseMediaModule;
import org.entermediadb.projects.LibraryCollection;
import org.entermediadb.projects.ProjectManager;
import org.openedit.Data;
import org.openedit.WebPageRequest;
import org.openedit.servlet.SiteData;
import org.openedit.util.PathUtilities;

public class OpenInstituteModule extends BaseMediaModule
{

	public void loadCommunityCategory(WebPageRequest inReq)
	{
		LibraryCollection col = (LibraryCollection)inReq.getPageValue("librarycol");
		if( col != null)
		{
			Collection all = col.getValues("communitytagcategory");
			if( !all.isEmpty())
			{
				String communitytagcategory = (String)all.iterator().next();
				Data data = getMediaArchive(inReq).getCachedData("communitytagcategory", communitytagcategory);
				inReq.putPageValue("communitytagcategory", data);
			}
		}
	}

//	public LibraryCollection loadCollectionFromCommunityTagFolder(WebPageRequest inReq)
//	{
//		MediaArchive archive = getMediaArchive(inReq);
//		String tagid = PathUtilities.extractDirectoryName(inReq.getPath());
//		ProjectManager manager = getProjectManager(inReq);
//		Data tag = archive.getData("communitytag", tagid);
//		if(tag != null) {
//			LibraryCollection col = manager.getLibraryCollection(archive, (String)tag.getValue("collectionid"));
//			if( col != null)
//			{
//				inReq.putPageValue("librarycol", col);
//				inReq.putPageValue("collectionid", col.getId());
//			}			
//			return col;
//		}
//		return null;
//
//	}
	
	
	
	public void loadCommunityTagFolder(WebPageRequest inReq)
	{
		MediaArchive archive = getMediaArchive(inReq);
		String tagid = PathUtilities.extractDirectoryName(inReq.getPath());
		Data tag = archive.getData("communitytagcategory", tagid);
		if(tag != null) {
			inReq.putPageValue("communitytagcategory", tag);
		}
	}

	public void loadCommunityTagCategory(WebPageRequest inReq)
	{
		MediaArchive archive = getMediaArchive(inReq);
		String communitytagcategory = inReq.getRequestParameter("communitytagcategory");
		if( communitytagcategory !=null)
		{
			Data tag = archive.getCachedData("communitytagcategory", communitytagcategory);
			if(tag != null) {
				inReq.putPageValue("communitytagcategory", tag);
				inReq.putPageValue("communitytagcategoryid", tag.getId());
				return;
			}
			
		}
		SiteData data = (SiteData)inReq.getPageValue("sitedata");
		if( data != null)
		{
			String tagid = data.get("domaincommunityid");
			if( tagid != null)
			{
				Data tag = archive.getCachedData("communitytagcategory", tagid);
				if(tag != null) {
					inReq.putPageValue("communitytagcategory", tag);
					inReq.putPageValue("communitytagcategoryid", tag.getId());
					return;
				}
			}
		}
		String tagid = PathUtilities.extractPageName(inReq.getPath());
		if( tagid.equals("index"))
		{
			return;
		}
		Data tag = archive.getCachedData("communitytagcategory", tagid);
		if(tag != null) {
			inReq.putPageValue("communitytagcategory", tag);
			inReq.putPageValue("communitytagcategoryid", tag.getId());
		}
	}
	
}
