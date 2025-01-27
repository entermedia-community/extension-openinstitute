import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.data.Searcher
import org.openedit.util.DateStorageUtil



public void init()
{
MediaArchive archive = context.getPageValue("mediaarchive");
	//Search for slots
	DateStorageUtil util = DateStorageUtil.getStorageUtil();
	Calendar now = util.getCalendar(new Date());
	Calendar today1 = util.getCalendar(util.getThisMonday());
	
	List nextthree = new ArrayList();
	
	int day = 0;
	if(now.get(Calendar.HOUR_OF_DAY) > 9) {
		day = 1; //starts next day
	}
	while(nextthree.size() < 3 && day < 14)
	{
		today1.set(Calendar.DAY_OF_YEAR,now.get(Calendar.DAY_OF_YEAR) + day);
		int hour = 9;
		while( nextthree.size() < 3 && hour <= 11) //Search 3 times
		{
			today1.set(Calendar.HOUR_OF_DAY,hour);
			Data found = archive.query("clientmeeting").exact("time",today1.getTime()).searchOne();
			if( found == null)
			{
				Calendar good = today1.clone();
				nextthree.add(good.getTime());
			}
			hour++;
		}
		if( today1.get(Calendar.DAY_OF_WEEK) == 5 )
		{
			day = day + 2; //Go to monday
		}
		day++;
	}
	context.putPageValue("founddays", nextthree);
}

init();


log.info("request processing...");


