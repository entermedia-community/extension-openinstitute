package users;

import org.entermediadb.asset.MediaArchive
import org.entermediadb.asset.Asset
import org.entermediadb.asset.Category
import org.openedit.Data
import org.openedit.data.QueryBuilder
import org.openedit.data.Searcher
import org.openedit.hittracker.HitTracker

//loop all year


public void init() {

	String year = context.getRequestParameter("year");
	String month = context.getRequestParameter("month");
	if (year== null || month == null) {
		//Generate All
		GregorianCalendar cal = new GregorianCalendar();
		Integer thisyear = cal.get(Calendar.YEAR);
		Integer thismonth = cal.get(Calendar.MONTH);
		thismonth = thismonth + 1;
		for(Integer c = 1; c <= thismonth; c++) {
			bymonth(thisyear, c);
		}
		
	}
	else {
		Integer yearInt = Integer.parseInt(year);
		Integer monthInt = Integer.parseInt(month)
		bymonth(yearInt, monthInt);
	}
}


public void bymonth(Integer yearInt, Integer monthInt) {
	MediaArchive archive = context.getPageValue("mediaarchive");

		monthInt = monthInt -1;//Zero base
	
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(new Date());
	cal.set(Calendar.YEAR, yearInt);
	cal.set(Calendar.MONTH, monthInt);
	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	Date start = cal.getTime();
	//log.info("to "+ start);

	context.putPageValue("since", start);


	int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	cal.set(Calendar.DAY_OF_MONTH, days);
	cal.set(Calendar.MINUTE, 59);
	cal.set(Calendar.HOUR_OF_DAY, 23);
	cal.set(Calendar.SECOND, 59);

	Date onemonth = cal.getTime();
	//log.info("to "+ onemonth);

	Searcher tasksearcher = archive.getSearcher("goaltask");
	QueryBuilder q = tasksearcher.query();
	HitTracker alltasks = q.match("completedby", "*").between("completedon", start, onemonth).sort("completedby").search();
	//log.info("Query: " + alltasks.getSearchQuery());


	Map<String, Map<String, List>> usersupdated = new HashMap();

	for (Iterator iterator = alltasks.iterator(); iterator.hasNext();)
	{
		Data  task = (Data) iterator.next();
		String userid = task.getValue("completedby");
		if (task.getValue("projectdepartment") == null) {
			continue;
		}
		Category cat = archive.getCategory(task.getValue("projectdepartment"));
		Double tasktotal = cat.getValue("goalpoints");
		if (tasktotal == null) {
			tasktotal = 0.0;
		}
		
		Map<String, List> currentuser = usersupdated.get(userid);
		if(currentuser == null) {
			currentuser = new HashMap();
		}
		Double usertotal = (Double) currentuser.get("total");
		if (usertotal == null) {
			usertotal = 0.0;
		}
		
		usertotal = usertotal + tasktotal;
		if(task.getValue("completedurgent")) {
			usertotal = usertotal + 10;
		}
		currentuser.put("total",usertotal);
		currentuser.put("month",monthInt+1);
		currentuser.put("year",yearInt);
		usersupdated.put(userid, currentuser);
	}
	
	//Update by user
	Searcher statementsearcher = archive.getSearcher("currencytransfer");
	Calendar today = Calendar.getInstance();
	if (usersupdated.size()>0) {
		for (String userid in usersupdated.keySet())
		{
			Map<String, List> currentuser = usersupdated.get(userid);
			//search user+month+year
			
			Data rowexists = (Data) statementsearcher.query().exact("paymententitydest", userid)
					.exact("generatedbyscript", "monthlystatements")
					.between("date", start, onemonth)
					.searchOne(); 
			
			if (rowexists != null) {
				rowexists.setValue("total", currentuser.get("total"));
				statementsearcher.saveData(rowexists);
			}
			else {
				Data row = statementsearcher.createNewData();
				row.setValue("total", currentuser.get("total"));
				row.setValue("generatedbyscript", "monthlystatements");
				row.setValue("paymententitysourcetype", "system");
				row.setValue("paymententitysource", "system");
				row.setValue("paymententitydest", userid);
				row.setValue("paymententitydesttype", "user");
				row.setValue("currencytransferstatus", "2");
				row.setValue("date", today.getTime());
				statementsearcher.saveData(row);
			}
			
		}
		
		log.info("Updated " + usersupdated.size() + " transactions");
	}
}


init();