package org.openinstitute.finance;

import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.WebPageRequest;

public class FinanceModule extends BaseMediaModule
{

	public void loadDateRange(WebPageRequest inReq)
	{
		DateRange range = null;
		String year = null;
		String clearfilters = inReq.getRequestParameter("clearfilters");
		if (!Boolean.parseBoolean(clearfilters)) {
			range = (DateRange)inReq.getSessionValue("daterange");
			year = inReq.getRequestParameter("year");
		}
		if( year != null)
		{
			range = new DateRange();
			int intyear = Integer.parseInt(year);
			if( intyear == -1)
			{
				range.setAllTime(true);
				range.setYearPicked(-1);
			}
			else
			{
				String month = inReq.getRequestParameter("month");
				if( month != null && !month.equals("0"))
				{
					range.setYearAndMonth(intyear, Integer.parseInt(month));
				}
				else
				{
					range.setYearToDate(intyear);
				}		
			}
		}
		if( range == null)
		{
			range  = new DateRange();			
			range.setYearToDate(0); //YTD
			range.setAllTime(true);
		}
		inReq.putSessionValue("daterange", range);
		
	}
	public FinanceManager loadFinanceManager(WebPageRequest inReq)
	{
		String catalogid = inReq.findValue("catalogid");
		FinanceManager manager = (FinanceManager)getModuleManager().getBean(catalogid, "financeManager");
		inReq.putPageValue("financeManager",manager);
		return manager;
	}
}
