import org.openedit.Data
import org.openedit.data.Searcher
import org.openedit.data.SearcherManager
import org.entermediadb.asset.MediaArchive

import org.openedit.hittracker.HitTracker


	// this is no longer used: Use BaseCompositeData 

	public void batchSave(){
		MediaArchive archive = context.getPageValue("mediaarchive");
		SearcherManager sm = archive.getSearcherManager();
		String catalogid = context.findValue("catalogid");
		String searchtype = context.findValue("searchtype");
		String sessionid = context.findValue("hitssessionid");
		Searcher searcher = sm.getSearcher(catalogid, searchtype);
		HitTracker targets = context.getSessionValue(sessionid);
		ArrayList toSave = new ArrayList();

		for (Iterator iterator = targets.getSelectedHitracker().iterator(); iterator.hasNext();) {
			Data hit = (Data) iterator.next();
			Object real = searcher.searchById(hit.getId());
			String[] fields = context.getRequestParameters("field");
			//			public Data updateData(WebPageRequest inReq, String[] fields, Data data);
			if(real != null){
				searcher.updateData(context, fields, real);
				toSave.add(real);
			}

		}
		searcher.saveAllData(toSave, context.getUser());

	}


batchSave();