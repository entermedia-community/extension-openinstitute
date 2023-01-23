package store;

import org.entermediadb.asset.MediaArchive
import org.entermediadb.google.GoogleManager
import org.entermediadb.projects.LibraryCollection
import org.openedit.Data
import org.openedit.MultiValued
import org.openedit.event.WebEvent
import org.openedit.users.User
import org.openinstitute.finance.DateRange
 

public void runit()
{
	//Search for dates
	Data product = aproduct;
	log.info("On " + product);

	Collection invoices = mediaarchive.query("collectiveinvoice").exact("productlist.productid",product.getId()).search();
	log.info("Invoices " + invoices.size());

	DateRange range = new DateRange();
	for(Data invoice in invoices)
	{
		Date startDate = invoice.getDate("duedate");
		Date endDate = invoice.getDate("enddate");
		range.addBlockedDateRange(startDate, endDate);
	}
	context.putPageValue("blockeddates",range);
}

runit();