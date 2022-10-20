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
		log.info("Sending Invoice.");
		Data invoice = mediaArchive.getInvoiceById(invoiceid);
		if(invoice != null) {
			if (!invoice.get("paymentstatus").equals("paid")) {
				invoiceContactIterate(mediaArchive, invoiceSearcher, invoice, "notificationsent");
				invoice.setValue("paymentstatus", "invoiced");
				invoice.setValue("notificationsent", "true")
				invoiceSearcher.saveData(invoice);
		
			}
			else if(invoice.get("paymentstatus").equals("paid") && !
				Boolean.valueOf(invoice.get("notificationpaidsent"))) {
				invoiceContactIterate(mediaArchive, invoiceSearcher, invoice, "notificationpaidsent");
				invoice.setValue("notificationpaidsent", "true")
				invoiceSearcher.saveData(invoice);
			}
		}
	}
	
	log.info("Sending Recurring Invoices...");
	// Notifications
	sendInvoiceNotifications(mediaArchive, invoiceSearcher);
	sendInvoiceOverdueNotifications(mediaArchive, invoiceSearcher);
	
	
}

private void sendInvoiceNotifications(MediaArchive mediaArchive, Searcher invoiceSearcher) {
	Collection pendingNotificationInvoices = invoiceSearcher.query()
			.exact("notificationsent","false")
			.orgroup("paymentstatus", "sendinvoice error")
			.search();

	
	if (pendingNotificationInvoices.size()>0)
	{
		log.info("Sending Notification for " + pendingNotificationInvoices.size() + " invoices");
		invoiceIterate(mediaArchive, invoiceSearcher, pendingNotificationInvoices, "notificationsent");
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
		invoiceIterate(mediaArchive, invoiceSearcher, pendingNotificationInvoices, "notificationoverduesent");
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
		invoiceIterate(mediaArchive, invoiceSearcher, pendingNotificationInvoices, "notificationpaidsent");
	}
}


private void invoiceIterate(MediaArchive mediaArchive, Searcher invoiceSearcher, Collection invoices, String iteratorType) {
	String appid = mediaArchive.getCatalogSettingValue("events_billing_notify_invoice_appid");
		for (Iterator invoiceIterator = invoices.iterator(); invoiceIterator.hasNext();)
		{
			Data invoice = invoiceSearcher.loadData(invoiceIterator.next());
			invoiceContactIterate(mediaArchive, invoiceSearcher, invoice, iteratorType);
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
					sendinvoiceEmail(mediaArchive, email, invoice, workspace, iteratorType);
				}
			}
		}
		Calendar today = Calendar.getInstance();
		invoice.setValue("sentto", emails);
		invoice.setValue("sentdate", today.getTime());
		invoice.setValue(iteratorType, "true");
		invoiceSearcher.saveData(invoice);
		}
		else 
		{
			log.info("No email addresses to send Invoice for: "+workspace+ " ("+collectionid+")");
		}
}

/*

private void sendEmail(MediaArchive mediaArchive, String contact, Data invoice, Data workspace, String subject, String htmlTemplate) {
	sendEmail(mediaArchive, contact, invoice, workspace, subject, htmlTemplate, null);
}
*/
private void sendinvoiceEmail(MediaArchive mediaArchive, String contact, Data invoice, Data workspace, String messagetype) {
	String appid = mediaArchive.getCatalogSettingValue("events_billing_notify_invoice_appid");
	String template = "/" + appid + "/theme/emails/";

	String actionUrl = getSiteRoot() + "/" + appid + "/collective/services/index.html?collectionid=" + invoice.getValue("collectionid");
	
	//String key = mediaArchive.getUserManager().getEnterMediaKey(contact);
	//actionUrl = actionUrl + "&entermedia.key=" + key;
	
	
	String subject;
	String invoiceemailheader;
	String invoiceemailfooter;
	
	String collectionid = workspace.getId();
	
	switch(messagetype) {
		case "notificationsent":
			actionUrl = getSiteRoot() + "/" + appid + "/collective/services/paynow.html?invoiceid=" + invoice.getValue("id") + "&collectionid=" + collectionid;
			actionUrl = actionUrl + "&contactemail="+contact;
			
			subject = "Invoice "+workspace;
			template = template + "send-invoice-event.html";
			//Invoice Template from collection
			invoiceemailheader = workspace.get("invoiceemailheader");
			if(invoiceemailheader == null || invoiceemailheader.equals("")) {
				invoiceemailheader =  mediaArchive.getCatalogSettingValue("invoice_email_header");
			}
			
			
			invoiceemailfooter = workspace.get("invoiceemailfooter");
			if(invoiceemailfooter == null || invoiceemailheader.equals("")) {
				invoiceemailfooter =  mediaArchive.getCatalogSettingValue("invoice_email_footer");
			}
		
		break;
	case "notificationoverduesent":
		subject = "Overdue Invoice for "+workspace;
		template = template + "send-overdue-invoice-event.html";
		//Invoice Template from collection
		invoiceemailheader = workspace.get("invoiceemailheader");
		if(invoiceemailheader == null || invoiceemailheader.equals("")) {
			invoiceemailheader =  mediaArchive.getCatalogSettingValue("invoice_email_header");
		}
		
		
		invoiceemailfooter = workspace.get("invoiceemailfooter");
		if(invoiceemailfooter == null || invoiceemailheader.equals("")) {
			invoiceemailfooter =  mediaArchive.getCatalogSettingValue("invoice_email_footer");
		}

		break;
	case "notificationpaidsent":
		subject = "Payment Received "+workspace;
		template = template + "send-paid-invoice-event.html";
		
		invoiceemailheader = workspace.get("invoicepaidemail");
		if(invoiceemailheader == null || invoiceemailheader.equals("")) {
			invoiceemailheader =  mediaArchive.getCatalogSettingValue("invoice_paid_email");
		}
		break;
	}
	actionUrl = URLUtilities.urlEscape(actionUrl);
	
	String supportUrl = getSiteRoot() + "/" + appid + "/collective/services/index.html?collectionid=" + invoice.getValue("collectionid");
	supportUrl = URLUtilities.urlEscape(supportUrl);
	
	//Other Pay options
	String invoicepayoptions = workspace.get("invoicepayoptions");
	if(invoicepayoptions == null) {
		invoicepayoptions =  mediaArchive.getCatalogSettingValue("invoice_pay_options");
	}

	WebEmail templateEmail = mediaArchive.createSystemEmail(contact, template);
	templateEmail.setSubject(subject);
	
	Map objects = new HashMap();
	objects.put("followeruser", contact);
	objects.put("invoiceto", contact); //change to name
	
	objects.put("mediaarchive", mediaArchive);
	objects.put("invoice", invoice);
	objects.put("project", workspace);
	objects.put("supporturl", supportUrl);
	objects.put("actionurl", actionUrl);
	objects.put("siteroot", getSiteRoot());
	objects.put("applink", appid);
	
	if (!invoiceemailheader.equals("")) {
		invoiceemailheader = mediaArchive.getReplacer().replace(invoiceemailheader, objects);
	}
	if (!invoiceemailfooter.equals("")) {
		invoiceemailfooter= mediaArchive.getReplacer().replace(invoiceemailfooter, objects);
	}
	
	objects.put("invoiceheader", invoiceemailheader);
	objects.put("invoicefooter", invoiceemailfooter);
	
	objects.put("invoicepayoptions", invoicepayoptions);
	
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





