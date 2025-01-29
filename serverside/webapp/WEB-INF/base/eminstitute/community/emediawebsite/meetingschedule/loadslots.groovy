import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.data.Searcher
import org.openedit.util.DateStorageUtil



public void init()
{
MediaArchive archive = context.getPageValue("mediaarchive");
	//Search for slots
	DateStorageUtil util = DateStorageUtil.getStorageUtil();
	Calendar now = util.getCalendar();
	Calendar today1 = util.getCalendar(util.getThisMonday());
	
	List nextthree = new ArrayList();
	
	int day = 0;
	if(now.get(Calendar.HOUR_OF_DAY) > 9) {
		day = 1; //starts next day
	}
	while(nextthree.size() < 6 && day < 14)
	{
		//log.info("before..." + today1.getTime());
		today1.add(Calendar.DAY_OF_YEAR, day);
		//log.info("after..." + today1.getTime());
		int hour = 9;
		while( nextthree.size() < 6 && hour <= 16) //Search 3 times
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
	log.info("request processing..." + nextthree);
	context.putPageValue("founddays", nextthree);
}

init();





