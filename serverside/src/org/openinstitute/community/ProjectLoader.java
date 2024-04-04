package org.openinstitute.community;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermediadb.asset.MediaArchive;
import org.openedit.CatalogEnabled;
import org.openedit.Data;
import org.openedit.ModuleManager;
import org.openedit.data.QueryBuilder;
import org.openedit.data.SearcherManager;
import org.openedit.hittracker.HitTracker;
import org.openedit.page.Page;
import org.openedit.page.PageLoader;
import org.openedit.page.manage.PageManager;
import org.openedit.servlet.RightPage;
import org.openedit.servlet.SiteData;
import org.openedit.util.URLUtilities;

public class ProjectLoader implements PageLoader, CatalogEnabled
{
	protected ModuleManager fieldModuleManager;
	protected PageManager fieldPageManager;
	protected String fieldCatalogId;
	private static final Log log = LogFactory.getLog(ProjectLoader.class);
	
	public PageManager getPageManager()
	{
		return fieldPageManager;
	}

	public void setPageManager(PageManager inPageManager)
	{
		fieldPageManager = inPageManager;
	}

	public ModuleManager getModuleManager()
	{
		return fieldModuleManager;
	}

	public void setModuleManager(ModuleManager inModuleManager)
	{
		fieldModuleManager = inModuleManager;
	}

	protected MediaArchive getMediaArchive()
	{
		return (MediaArchive)getModuleManager().getBean(getCatalogId(),"mediaArchive");
	}
	
	@Override
	public RightPage getRightPage( URLUtilities util, SiteData sitedata, Page inPage)
	{
		String path = inPage.getPath();
		String[] url = path.split("/");

		//Check domain?
		String[] domain  = util.domain().split("\\.");

		String communityurlname = null;
		String projecturlname = null;
		String anythingelse = null;

		//Assume everything is higher
		if(domain.length > 2) //Move up everything
		{
			if(url.length > 1 && (url[1].equals("mediadb") || url[1].equals("app")))
			{
				return null;
			}

			communityurlname = domain[0];
			if( url.length > 2)
			{
				projecturlname = url[1];
				if( url.length > 3)
				{
					anythingelse = path.substring(path.indexOf(projecturlname));
				}
			}
		}
		else
		{
			if( url.length < 3)
			{
				return null;
			}
			if(url[2].equals("mediadb") || url[2].equals("app"))
			{
				return null;
			}

			if( url.length == 3)
			{
				communityurlname = url[2];
			}
			if(url.length > 3) //Might be projects or blogs or a virtual project
			{
				communityurlname = url[2];
				projecturlname = url[3];
			}
			if( url.length > 4)
			{
				anythingelse = path.substring(path.indexOf(projecturlname) + projecturlname.length());
			}
		}

		if(projecturlname == null)
		{
			RightPage page = goHome(inPage, communityurlname);
			return page;	
		}
		
		//TODO: Keep a cached list of sub folders where we always load the page from blogs projects etc and assume .../index.html
		
		//Does the page exist or is it a project?
		String siteid = inPage.get("siteid");
		Data communitydata = findCommunity(communityurlname);
		if( communitydata == null)
		{
			return null;
		}
		String communityhome = "/" + siteid + communitydata.get("templatepath");
		String fixedpath =communityhome + "/" + projecturlname;

		Page page = getPageManager().getPage(fixedpath);

		if(  anythingelse != null)
		{
			fixedpath = fixedpath + anythingelse;
			page = getPageManager().getPage(fixedpath);
		}
		
		RightPage right = new RightPage();
		right.putParam("communitytagcategory" ,  communitydata.getId());
		right.putPageValue("communitytagcategory" , communitydata);
		right.putPageValue("communityhome" , communityhome);
		if( page.exists())  //Must be a real page
		{
			right.setRightPage(page);
			return right;
		}

		//Must be a project with something on the end?		
		QueryBuilder query = getMediaArchive().query("librarycollection").exact("urlname", projecturlname).hitsPerPage(1);
		HitTracker hits = getMediaArchive().getCachedSearch(query);
		Data librarycollection = (Data)hits.first();
		if(librarycollection != null)
		{
			String template = null;
			if( anythingelse == null)
			{
				template =communityhome + "/project/projecthome.html";
			}
			else
			{
				template = communityhome + "/project" + anythingelse;
			}

			Page otherpage = getPageManager().getPage(template);
			if( !otherpage.exists())
			{
				log.info("Cant find " + template);
			}
			right.putParam("collectionid" , librarycollection.getId());
			right.putPageValue("librarycol" , librarycollection);
			right.putPageValue("projectlink" , "/" + communityurlname + "/" + librarycollection.get("urlname") );
			right.setRightPage(otherpage);
			return right;
		}
		return null;
	}

	protected RightPage goHome(Page inPage, String communityurlname)
	{
		Data first = findCommunity(communityurlname);
		if(first != null)
		{
			String siteid = inPage.get("siteid");
			String communityhome = "/" + siteid + first.get("templatepath");
			String template =communityhome + "/communityhome.html";  //?communitytagcategoryid=" + first.getId() communities/emedia/home.html
			Page page = getPageManager().getPage(template);
			RightPage right = new RightPage();
			right.putParam("communitytagcategory" ,  first.getId());
			right.putPageValue("communitytagcategory" , first);
			right.putPageValue("communityhome" , communityhome);
			
			right.setRightPage(page);
			return right;

		} return null;
	}

	protected Data findCommunity(String communityurlname)
	{
		QueryBuilder query = getMediaArchive().query("communitytagcategory").match("urlname", communityurlname).hitsPerPage(1);
		HitTracker hits = getMediaArchive().getCachedSearch(query);
		Data first = (Data)hits.first();
		return first;
	}

	@Override
	public void setCatalogId(String inId)
	{
		fieldCatalogId = inId;
		
	}
	protected String getCatalogId()
	{
		return fieldCatalogId;
	}

}
