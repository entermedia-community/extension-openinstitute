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
	public RightPage getRightPage(Page inPage)
	{
		String path = inPage.getPath();
		String[] sections = path.split("/");
		if(sections[2].equals("mediadb") || sections[2].equals("app"))
		{
			return null;
		}
		if(sections.length == 3)
		{
			String communityurl = sections[2];
			QueryBuilder query = getMediaArchive().query("communitytagcategory").exact("urlname", communityurl).hitsPerPage(1);
			HitTracker hits = getMediaArchive().getCachedSearch(query);
			Data first = (Data)hits.first();
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

			}
		}
		if(sections.length > 3)
		{
			String projectname = sections[3];
			QueryBuilder query = getMediaArchive().query("librarycollection").exact("urlname", projectname).hitsPerPage(1);
			HitTracker hits = getMediaArchive().getCachedSearch(query);
			Data librarycollection = (Data)hits.first();
			if(librarycollection != null)
			{
				String categoryid = librarycollection.get("communitytagcategory");  //communities/emedia/home.html
				Data cat = getMediaArchive().getCachedData("communitytagcategory", categoryid);
				String siteid = inPage.get("siteid");
				String communityhome = "/" + siteid + cat.get("templatepath");
				
				String template = null;
				if( sections.length == 4)
				{
					template =communityhome + "/project/projecthome.html";
				}
				else
				{
					template = communityhome + "/project";
					for (int i = 4; i < sections.length; i++)
					{
						template = template + "/" + sections[i];
					}
				}

				Page page = getPageManager().getPage(template);
				if( !page.exists())
				{
					log.info("Cant find " + template);
				}
				RightPage right = new RightPage();
				right.putParam("collectionid" , librarycollection.getId());
				right.putParam("communitytagcategory" , categoryid);
				right.putPageValue("communitytagcategory" , cat);
				right.putPageValue("communityhome" , communityhome);
				right.putPageValue("librarycol" , librarycollection);				
				right.setRightPage(page);
				return right;
			}
		}
		//Look for a community
		return null;
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
