package billing;

import org.entermedia.stripe.StripePaymentProcessor
import org.entermediadb.asset.MediaArchive
import org.entermediadb.email.WebEmail
import org.openedit.*
import org.openedit.data.Searcher
import org.openedit.users.Group
import org.openedit.users.User
import org.openedit.util.URLUtilities

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	
	Searcher invoiceSearcher = mediaArchive .getSearcher("collectiveinvoice");
	
	
	String invoiceid = context.getRequestParameter("invoiceid");
	if(invoiceid!=null) {
		Data invoice = mediaArchive.getInvoiceById(invoiceid);
		if(invoice != null) {
			invoiceContactIterate(mediaArchive, invoiceSearcher, invoice, "notificationsent");
		}
	}

	// Notifications
	/*sendInvoiceNotifications(mediaArchive, invoiceSearcher);
	sendInvoiceOverdueNotifications(mediaArchive, invoiceSearcher);
	sendInvoicePaidNotifications(mediaArchive, invoiceSearcher);
	*/
}

private void sendInvoiceNotifications(MediaArchive mediaArchive, Searcher invoiceSearcher) {
	Collection pendingNotificationInvoices = invoiceSearcher.query()
			.exact("notificationsent","false")
			.exact("paymentstatus","sendinvoice").search();

	
	if (pendingNotificationInvoices.size()>0)
	{
		log.info("Sending Notification for " + pendingNotificationInvoices.size() + " invoices");
		invoiceContactIterate(mediaArchive, invoiceSearcher, pendingNotificationInvoices, "notificationsent");
	}
}

private void sendInvoiceOverdueNotifications(MediaArchive mediaArchive, Searcher invoiceSearcher) {
	Calendar today = Calendar.getInstance();
	Collection pendingNotificationInvoices = invoiceSearcher.query()
			.before("duedate", today.getTime())
			.exact("notificationoverduesent", "false")
			.exact("paymentstatus","invoiced").search();

	
	if (pendingNotificationInvoices.size()>0)
	{
		log.info("Found " + pendingNotificationInvoices.size() + " overdue invoices");
		invoiceContactIterate(mediaArchive, invoiceSearcher, pendingNotificationInvoices, "notificationoverduesent");
	}
}

private void sendInvoicePaidNotifications(MediaArchive mediaArchive, Searcher invoiceSearcher) {
	Calendar today = Calendar.getInstance();
	Collection pendingNotificationInvoices = invoiceSearcher.query()
			.exact("notificationpaidsent", "false")
			.exact("paymentstatus","paid").search();

	
	if (pendingNotificationInvoices.size()>0)
	{
		log.info("Found " + pendingNotificationInvoices.size() + " paid invoices");
		invoiceContactIterate(mediaArchive, invoiceSearcher, pendingNotificationInvoices, "notificationpaidsent");
	}
}

private void invoiceContactIterate(MediaArchive mediaArchive, Searcher invoiceSearcher, Data invoice, String iteratorType) {

	String appid = mediaArchive.getCatalogSettingValue("events_billing_notify_invoice_appid");
		
	//Data invoice = invoiceSearcher.loadData(invoiceIterator.next());
	String collectionid = invoice.getValue("collectionid");
	Data workspace = mediaArchive.getData("librarycollection", collectionid);
	String emails = invoice.getValue("sentto");
	List<String> emaillist = Arrays.asList(emails.split(","));
	if (emaillist.size()>0) 
	{
		log.info("Sending email to  "+emaillist.size()+" members of: "+workspace+ " ("+collectionid+")");
		for (String email : emaillist)
		 {
			if (email != null) {
				if (email) {
					switch (iteratorType) {
						case "notificationsent":
							String actionUrl = getSiteRoot() + "/" + appid + "/collective/services/paynow.html?invoiceid=" + invoice.getValue("id") + "&collectionid=" + collectionid;
							
							//String key = mediaArchive.getUserManager().getEnterMediaKey(email);
							//actionUrl = actionUrl + "&entermedia.key=" + key;
							
							actionUrl = URLUtilities.urlEscape(actionUrl);
							sendEmail(mediaArchive, email, invoice, "Invoice "+workspace, "send-invoice-event.html", actionUrl);
							break;
						case "notificationoverduesent":
							sendEmail(mediaArchive, email, invoice, "Overdue Invoice "+workspace, "send-overdue-invoice-event.html");
							break;
						case "notificationpaidsent":
							sendEmail(mediaArchive, email, invoice, "Payment Received "+workspace, "send-paid-invoice-event.html");
							break;
					}
				}
			}
		}
		Calendar today = Calendar.getInstance();
		invoice.setValue("sentto", emails);
		invoice.setValue("sentdate", today.getTime());
		invoice.setValue("paymentstatus", "invoiced");
		invoice.setValue(iteratorType, "true");
		invoiceSearcher.saveData(invoice);
		}
		else 
		{
			log.info("No email addresses to send Invoice for: "+workspace+ " ("+collectionid+")");
		}
}



private void sendEmail(MediaArchive mediaArchive, String contact, Data invoice, String subject, String htmlTemplate) {
	sendEmail(mediaArchive, contact, invoice, subject, htmlTemplate, null);
}

private void sendEmail(MediaArchive mediaArchive, String contact, Data invoice, String subject, String htmlTemplate, String actionUrl) {
	String appid = mediaArchive.getCatalogSettingValue("events_billing_notify_invoice_appid");
	String template = "/" + appid + "/theme/emails/" + htmlTemplate;
	
	
	if (actionUrl == null) {
		actionUrl = getSiteRoot() + "/" + appid + "/collective/services/index.html?collectionid=" + invoice.getValue("collectionid");
		
		//String key = mediaArchive.getUserManager().getEnterMediaKey(contact);
		//actionUrl = actionUrl + "&entermedia.key=" + key;
		actionUrl = URLUtilities.urlEscape(actionUrl);

		
	}
	String supportUrl = getSiteRoot() + "/" + appid + "/collective/services/index.html?collectionid=" + invoice.getValue("collectionid");

	WebEmail templateEmail = mediaArchive.createSystemEmail(contact, template);
	templateEmail.setSubject(subject);
	Map objects = new HashMap();
	objects.put("followeruser", contact);
	objects.put("mediaarchive", mediaArchive);
	objects.put("invoice", invoice);
	objects.put("supporturl", supportUrl);
	objects.put("actionurl", actionUrl);
	templateEmail.send(objects);
	log.info("Email sent to: "+contact);
}

private String getSiteRoot() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	String site = mediaArchive.getCatalogSettingValue("siteroot");
	if (!site) {
		site = mediaArchive.getCatalogSettingValue("cdn_prefix");
	}
	return site;
}

init();





