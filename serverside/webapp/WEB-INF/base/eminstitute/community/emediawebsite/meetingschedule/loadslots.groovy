import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.util.DateStorageUtil



public void init()
{
MediaArchive archive = context.getPageValue("mediaarchive");
	//Search for slots
	DateStorageUtil util = DateStorageUtil.getStorageUtil();
	
	TimeZone TZ = TimeZone.getTimeZone("America/New_York")
	
	Calendar now = Calendar.getInstance(TZ);
	Calendar startofday = util.createCalendar(TZ);
	List nextthree = new ArrayList();
	
	int day = 0;
	if(now.get(Calendar.HOUR_OF_DAY) > 12) {
		day = 1; //starts next day
	}
	while(nextthree.size() < 12 && day < 14)
	{
		//log.info("before..." + today1.getTime());
		startofday.add(Calendar.DAY_OF_YEAR, day);
		//log.info("after..." + today1.getTime());
		int hour = 9;
		while( nextthree.size() < 12 && hour <= 16) //Search 3 times
		{
			startofday.set(Calendar.HOUR_OF_DAY, hour);
			Data found = archive.query("clientmeeting").exact("time",startofday.getTime()).searchOne();
			if( found == null)
			{
				Calendar good = startofday.clone();
				nextthree.add(good.getTime());
			}
			hour++;
		}
		if( startofday.get(Calendar.DAY_OF_WEEK) == 5 )
		{
			day = day + 2; //Go to monday
		}
		day++;
	}
	log.info("request processing..." + nextthree);
	context.putPageValue("founddays", nextthree);
}

init();





