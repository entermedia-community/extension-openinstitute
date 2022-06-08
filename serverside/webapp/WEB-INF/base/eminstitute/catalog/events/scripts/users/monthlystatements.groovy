package users;

import org.entermediadb.asset.MediaArchive
import org.entermediadb.asset.Asset
import org.entermediadb.asset.Category
import org.openedit.Data
import org.openedit.data.QueryBuilder
import org.openedit.data.Searcher
import org.openedit.hittracker.HitTracker

public void init() {
	MediaArchive archive = context.getPageValue("mediaarchive");

	//Search for all tasks with updated dates?
	GregorianCalendar cal = new GregorianCalendar();
	String year = context.getRequestParameter("year");
	String month = context.getRequestParameter("month");

	Integer yearInt = Integer.parseInt(year);
	Integer monthInt = Integer.parseInt(month);

	monthInt = monthInt -1;//Zero base

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

	Date onemonth = cal.getTime();
	log.info("to "+ onemonth);

	Searcher tasksearcher = archive.getSearcher("goaltask");
	QueryBuilder q = tasksearcher.query();
	HitTracker alltasks = q.match("completedby", "*").between("completedon", start, onemonth).sort("completedby").search();
	log.info("Query: " + alltasks.getSearchQuery());


	Map<String, Map<String, List>> usersupdated = new HashMap();

	for (Iterator iterator = alltasks.iterator(); iterator.hasNext();)
	{
		Data  task = (Data) iterator.next();
		String userid = task.getValue("completedby");

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
		currentuser.put("total",usertotal);
		currentuser.put("month",monthInt);
		currentuser.put("year",yearInt);
		usersupdated.put(userid, currentuser);
	}
	
	//Update by user
	Searcher statementsearcher = archive.getSearcher("pointstatementmonthly");
	Calendar today = Calendar.getInstance();
	for (String userid in usersupdated.keySet())
	{
		Map<String, List> currentuser = usersupdated.get(userid);
		//search user+month+year
		Data rowexists = (Data) statementsearcher.query().exact("user", userid)
				.exact("year", (String) currentuser.get("year"))
				.exact("month", (String) currentuser.get("month"))
				.searchOne(); 
		if (rowexists != null) {
			rowexists.setValue("total", currentuser.get("total"));
			rowexists.setValue("lastmodified", today.getTime());
			statementsearcher.saveData(rowexists);
		}
		else {
			Data row = statementsearcher.createNewData();
			row.setValue("total", currentuser.get("total"));
			row.setValue("user", userid);
			row.setValue("month", currentuser.get("month"));
			row.setValue("year", currentuser.get("year"));
			row.setValue("creationdate", today.getTime());
			row.setValue("lastmodified", today.getTime());
			statementsearcher.saveData(row);
		}
		
	}
	
	log.info(usersupdated);
}


init();