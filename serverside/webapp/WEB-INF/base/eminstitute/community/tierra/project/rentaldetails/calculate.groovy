package store;

import org.openedit.Data
import org.openedit.util.DateStorageUtil

public void runit()
{
	//Search for dates
	String duedate = context.getRequestParameter("checkIn");
	String checkout = context.getRequestParameter("checkOut");

	DateStorageUtil util = mediaarchive.getBean("dateStorageUtil");
	
    Date startdate = util.parseFromStorage(duedate);
	Date enddate = util.parseFromStorage(checkout);
	
    int count = util.daysBetweenDates(enddate, startdate);

    context.putPageValue("totaldays",count);
	
	
	String id  = context.getRequestParameter("productid");
	Data aproduct = mediaarchive.getData("collectiveproduct",id);

	double subtotal = aproduct.getDouble("productprice");
	//Data period = mediaarchive.getCachedData("productrecurringperiod",$aproduct.recurringperiod)


	String guests = context.getRequestParameter("guests");
	if( guests == null)
	{
		guests = "1";
	}

	if( count < 28)
	{
		subtotal = subtotal * 1.30;
	}
	if( guests != null && !guests.equals("1"))
	{
		subtotal = subtotal * 1.25;
	}
	
	double total = subtotal * count;

	context.putPageValue("guests",Integer.parseInt(guests));

	context.putPageValue("dailyrate",subtotal);
	context.putPageValue("totalcost",total);

	
	
}

runit();
