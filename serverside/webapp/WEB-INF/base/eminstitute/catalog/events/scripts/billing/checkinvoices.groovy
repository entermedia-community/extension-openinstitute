package billing;

import org.entermedia.stripe.StripePaymentProcessor
import org.entermediadb.asset.MediaArchive
import org.openedit.*
import org.openedit.data.Searcher
import org.openedit.users.User
import org.openedit.util.DateStorageUtil

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher productSearcher = mediaArchive .getSearcher("collectiveproduct");
	Searcher invoiceSearcher = mediaArchive .getSearcher("collectiveinvoice");
	log.info("Checking Invoices");
	generateRecurringInvoices(mediaArchive, productSearcher);  //status=active
	//generateNonRecurringInvoices(mediaArchive, productSearcher);  //status=active
	payAutoPaidInvoices(mediaArchive, invoiceSearcher);
	
	
	// Notifications - Handled on sendinvoice.groovy
	mediaArchive.fireMediaEvent("billing","sendinvoices",  null, context.getUser());
	
	//sendInvoiceNotifications(mediaArchive, invoiceSearcher);  //may work same as sendinvoice.
	//sendInvoiceOverdueNotifications(mediaArchive, invoiceSearcher);
	//sendInvoicePaidNotifications(mediaArchive, invoiceSearcher);
	//done in sendinvoice.groovy
}

private void payAutoPaidInvoices(MediaArchive mediaArchive, Searcher invoiceSearcher) {
	StripePaymentProcessor stripe = new StripePaymentProcessor();
	Calendar today = Calendar.getInstance();
	Collection invoices = invoiceSearcher.query()
			.exact("paymentstatus","invoiced")
			.exact("isautopaid","true").search();
    if (invoices.size() > 0) {
		log.info("Auto-Paid pending invoices " + invoices.size() + " found");
		for (Iterator invoiceIterator = invoices.iterator(); invoiceIterator.hasNext();) {
			Data invoice = invoiceSearcher.loadData(invoiceIterator.next());
			Map<String, Object> customer = stripe.getCustomer(mediaArchive,  "billing+" + invoice.getValue("collectionid") + "@entermediadb.com")
			if (customer != null) {
				Map<String, Object> sourcesData = customer.get("sources");
				ArrayList<Map<String, Object>> sources = sourcesData.get("data");
				if (sources.size() > 0) {
					Map<String, Object> source = sources.get(0);
					Searcher payments = mediaArchive.getSearcher("transaction");
					Data payment = payments.createNewData();
					payment.setValue("paymenttype","stripe" );
					payment.setValue("totalprice", invoice.getValue("totalprice"));
					Boolean chargeSuccess = stripe.createCharge(mediaArchive, payment, customer.get("id"));
	
					if (chargeSuccess) {
						invoice.setValue("paymentstatus", "paid");
						invoice.setValue("invoicepaidon", today.getTime());
					} else {
						invoice.setValue("paymentstatus", "autopayfailed");
						invoice.setValue("paymentstatusreason", "CreditCard failed");
					}
				} else {
					invoice.setValue("paymentstatus", "autopayfailed");
					invoice.setValue("paymentstatusreason", "No Credit Cards Stored");
				}
			} else {
				invoice.setValue("paymentstatus", "autopayfailed");
				invoice.setValue("paymentstatusreason", "No Customer");
			}
			invoiceSearcher.saveData(invoice);
		}
    }
}

private void generateRecurringInvoices(MediaArchive mediaArchive, Searcher productSearcher) {
	int daysToExpire = 7; // invoice will expire in (days)
	Calendar today = Calendar.getInstance();
		//to go back
		Calendar since = Calendar.getInstance();
		since.add(Calendar.DAY_OF_YEAR, -15);
		//--
	Calendar due = Calendar.getInstance();
	due.add(Calendar.DAY_OF_YEAR, +15); // make invoice 10 days before next bill date

	Collection pendingProducts = productSearcher.query()
			.exact("recurring","true")
			.exact("billingstatus", "active")
			.between("nextbillon", since.getTime(), due.getTime())
			.search();
    log.info("Searching invoices from ${since.getTime()} to ${due.getTime()}. ${pendingProducts.size()} found.");
	
	if (pendingProducts.size() > 0) 
	{
		log.info("Creating recurring invoices for " + pendingProducts.size() + " products");
		for (Iterator productIterator = pendingProducts.iterator(); productIterator.hasNext();) {
			Data product = productSearcher.loadData(productIterator.next());
	
			Date nextBillOn = product.getValue("nextbillon");
			Date lastbilldate = product.getValue("lastgeneratedinvoicedate");
			if (lastbilldate == null || lastbilldate < nextBillOn) { // otherwise assume it's already created
				Searcher invoiceSearcher = mediaArchive.getSearcher("collectiveinvoice");
				Data invoice = invoiceSearcher.createNewData();
	
				/*Calendar invoiceDue = Calendar.getInstance();
				invoiceDue.setTime(nextBillOn);
				invoiceDue.add(Calendar.DAY_OF_YEAR, daysToExpire);*/
				
				HashMap<String,Object> productItem = new HashMap<String,Object>();
				productItem.put("productid", product.getValue("id"));
				productItem.put("productquantity", 1 );
				productItem.put("productprice", product.getValue("productprice"));
				Collection items = new ArrayList();
				items.add(productItem);
				invoice.setValue("productlist", items);
				invoice.setValue("currencytype",  product.getValue("currencytype"));
				invoice.setValue("paymentstatus", "sendinvoice");
				invoice.setValue("isautopaid", product.getValue("isautopaid"));
				invoice.setValue("collectionid", product.getValue("collectionid"));
				invoice.setValue("owner", product.getValue("owner"));
				invoice.setValue("totalprice", product.getValue("productprice"));
				invoice.setValue("isrecurring", "true");
				invoice.setValue("recurringperiod", product.getValue("recurringperiod"));
				//invoice.setValue("billdate", nextBillOn); //Original Bill Date
				invoice.setValue("duedate", nextBillOn);  
				invoice.setValue("invoicedescription", product.getValue("productdescription"));
				invoice.setValue("notificationsent", "false");
				invoice.setValue("createdon", today.getTime());
				
				//start
				//invoice.setValue("startdate", nextBillOn);  //use duedate
				
				//end
				Integer recurringperiod = product.getValue("recurringperiod");
				Calendar endbilldate = Calendar.getInstance();
				endbilldate.setTime(nextBillOn);
				endbilldate.add(Calendar.MONTH, recurringperiod);
				invoice.setValue("enddate", endbilldate.getTime());

				//name -subject
				String collectionid = product.getValue("collectionid");
				Data collection = mediaArchive.getCachedData("librarycollection", collectionid);
				if(collection != null) {
					String name = "\${project} - \${invoicemonth} Invoice";
					invoice.setValue("name", name);
				}
				
				String contactsstring = "";
				if(product.getValue("sentto") != null) {
					contactsstring = product.getValue("sentto");
				}
				else {
					Collection contacts = mediaArchive.query("librarycollectionusers")
						.exact("collectionid",product.getValue("collectionid"))
						.exact("ontheteam",true)
						.exact("isbillingcontact","true")
						.search();
					if (contacts!= null) {
						for(Data c:contacts)
						{
							User user = mediaArchive.getUser(c.getValue("followeruser"));
							
							if(user!= null && user.getEmail() != null) {
								if (contactsstring != "") {
									contactsstring = contactsstring + ", " + user.getEmail();
								}
								else {
									contactsstring =  user.getEmail();
								}
							}
						}
					}
				}
				invoice.setValue("sentto", contactsstring);
				invoiceSearcher.saveData(invoice);
				log.info("Invoice Created for recurring product");
				
				int recurrentCount = product.getValue("recurringperiod");
				int currentMonth = nextBillOn.getMonth();
				nextBillOn.setMonth(currentMonth + recurrentCount);
				product.setValue("nextbillon", nextBillOn);
				product.setValue("lastgeneratedinvoicedate", today.getTime());
				productSearcher.saveData(product);
			}
			else {
				log.info("Invoice not created, invoice may alreay sent. ${lastbilldate} < ${nextBillOn}")
			} 
				
		}
	}
}

private void generateNonRecurringInvoices(MediaArchive mediaArchive, Searcher productSearcher) {
	int daysToExpire = 7; // invoice will expire in (days)
	Calendar today = Calendar.getInstance();
	Calendar due = Calendar.getInstance();
	due.add(Calendar.DAY_OF_YEAR, -5); // make invoice 5 days before next bill date

	Collection pendingProducts = productSearcher.query()
			.exact("recurring","false")
			.exact("billingstatus", "active")
			.missing("lastgeneratedinvoicedate").search();
			//.exact("producttype","0")
			
			
	//log.info("Creating invoice for " + pendingProducts.size() + " none-recurring Products");
	for (Iterator productIterator = pendingProducts.iterator(); productIterator.hasNext();) {
		Data product = productSearcher.loadData(productIterator.next());

		Date nextBillOn = product.getValue("nextbillon");
		Date lastbilldate = product.getValue("	");
		if (lastbilldate < nextBillOn) { // otherwise assume it's already created
			Searcher invoiceSearcher = mediaArchive.getSearcher("collectiveinvoice");
			Data invoice = invoiceSearcher.createNewData();

			Calendar invoiceDue = Calendar.getInstance();
			invoiceDue.add(Calendar.DAY_OF_YEAR, daysToExpire);
			HashMap<String,Object> productItem = new HashMap<String,Object>();
			productItem.put("productid", product.getValue("id"));
			productItem.put("productquantity", 1 );
			productItem.put("productprice", product.getValue("productprice"));
			Collection items = new ArrayList();
			items.add(productItem);
			invoice.setValue("productlist", items);
			invoice.setValue("paymentstatus", "sendinvoice");
			invoice.setValue("isautopaid", product.getValue("isautopaid"));
			invoice.setValue("collectionid", product.getValue("collectionid"));
			invoice.setValue("owner", product.getValue("owner"));
			invoice.setValue("totalprice", product.getValue("productprice"));
			invoice.setValue("startdate", invoiceDue.getTime());
			invoice.setValue("invoicedescription", product.getValue("productdescription"));
			invoice.setValue("notificationsent", "false");
			invoice.setValue("createdon", today.getTime());
			invoiceSearcher.saveData(invoice);

			product.setValue("lastgeneratedinvoicedate", today.getTime());
			productSearcher.saveData(product);
		}
	}
}

/**
Moved to sendinvoice.groovy


private void sendInvoiceNotifications(MediaArchive mediaArchive, Searcher invoiceSearcher) {
	Collection pendingNotificationInvoices = invoiceSearcher.query()
			.exact("notificationsent","false")
			.orgroup("paymentstatus", "sendinvoice error")
			.search();

	
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

private void invoiceContactIterate(MediaArchive mediaArchive, Searcher invoiceSearcher, Collection invoices, String iteratorType) {
	String appid = mediaArchive.getCatalogSettingValue("events_billing_notify_invoice_appid");
		for (Iterator invoiceIterator = invoices.iterator(); invoiceIterator.hasNext();) 
		{
			Data invoice = invoiceSearcher.loadData(invoiceIterator.next());
			String collectionid = invoice.getValue("collectionid");
			Data workspace = mediaArchive.getData("librarycollection", collectionid);
			String emails = invoice.getValue("sentto");
			Boolean sent = false;
			if (emails == null)
			{
					//If no email defined will search again for them.
					Collection contacts = mediaArchive.query("librarycollectionusers")
					.exact("collectionid",collectionid)
					.exact("ontheteam",true)
					.exact("isbillingcontact","true")
					.search();
					String contactsstring = "";
					if (contacts!= null) {
						for(Data c:contacts)
						{
							User user = mediaArchive.getUser(c.getValue("followeruser"));
							
							if(user!= null && user.getEmail() != null) {
								if (contactsstring != "") {
									contactsstring = contactsstring + ", " + user.getEmail();
								}
								else {
									contactsstring =  user.getEmail();
								}
							}
						}
					}
					invoice.setValue("sentto", contactsstring);
					emails = contactsstring;
			}
			if (emails != null)
			{
				List<String> emaillist = Arrays.asList(emails.split(","));
				if (emaillist.size()>0) 
				{
					log.info("Sending email to  "+emaillist.size()+" members of: "+workspace+ " ("+collectionid+")");
					for (String email : emaillist)
					 {
						if (email != null) {
								switch (iteratorType) {
									case "notificationsent":
										String actionUrl = getSiteRoot() + "/" + appid + "/collective/services/paynow.html?invoiceid=" + invoice.getValue("id") + "&collectionid=" + collectionid;
										actionUrl = actionUrl + "&notifiedemail="+email;
										//String key = mediaArchive.getUserManager().getEnterMediaKey(email);
										//actionUrl = actionUrl + "&entermedia.key=" + key;
										
										actionUrl = URLUtilities.urlEscape(actionUrl);
										sendEmail(mediaArchive, email, invoice, "Invoice "+workspace, "send-invoice-event.html", actionUrl);
										sent = true;
										break;
									case "notificationoverduesent":
										sendEmail(mediaArchive, email, invoice, "Overdue Invoice "+workspace, "send-overdue-invoice-event.html");
										sent = true;
										break;
									case "notificationpaidsent":
										sendEmail(mediaArchive, email, invoice, "Payment Received "+workspace, "send-paid-invoice-event.html");
										sent = true;
										break;
								}
							}
						}
				}
			}
			if (sent) {
				Calendar today = Calendar.getInstance();
				invoice.setValue("sentto", emails);
				invoice.setValue("sentdate", today.getTime());
				invoice.setValue("paymentstatus", "invoiced");
				invoice.setValue(iteratorType, "true");
			}
			else {
				invoice.setValue("paymentstatus", "error");
				invoice.setValue("paymentstatusreason", "No billing address defined in Project.");
				log.info("No email defined to send Invoice for: "+workspace+ " ("+collectionid+")");
			}
			invoiceSearcher.saveData(invoice);
		}
}



/*
 * 
 private void invoiceContactIterate(MediaArchive mediaArchive, Searcher invoiceSearcher, Collection invoices, String iteratorType) {

	String appid = mediaArchive.getCatalogSettingValue("events_billing_notify_invoice_appid");
	
	for (Iterator invoiceIterator = invoices.iterator(); invoiceIterator.hasNext();) {
		Data invoice = invoiceSearcher.loadData(invoiceIterator.next());

		Searcher teamSearcher = mediaArchive .getSearcher("librarycollectionusers");
		String collectionid = invoice.getValue("collectionid");
		if (collectionid != null) {
			Collection invoiceMembers = teamSearcher.query()
					.exact("collectionid", collectionid)
					.exact("isbillingcontact", "true")
					.search();
			Data workspace = mediaArchive.getData("librarycollection", collectionid)
			log.info("Reviewing "+invoiceMembers.size()+" members for: "+workspace+ " ("+collectionid+")");
			for (Iterator teamIterator = invoiceMembers.iterator(); teamIterator.hasNext();) {
				Data member = teamSearcher.loadData(teamIterator.next());
				User contact = mediaArchive.getUser(member.getValue("followeruser"));

				if (contact != null) {
					String email = contact.getValue("email");
					if (email) {
						switch (iteratorType) {
							case "notificationsent":
								String actionUrl = getSiteRoot() + "/" + appid + "/collective/services/paynow.html?invoiceid=" + invoice.getValue("id") + "&collectionid=" + collectionid;
								
								String key = mediaArchive.getUserManager().getEnterMediaKey(contact);
								actionUrl = actionUrl + "&entermedia.key=" + key;
								actionUrl = URLUtilities.urlEscape(actionUrl);
								sendEmail(mediaArchive, contact, invoice, "Invoice "+workspace, "send-invoice-event.html", actionUrl);
								break;
							case "notificationoverduesent":
								sendEmail(mediaArchive, contact, invoice, "Overdue Invoice "+workspace, "send-overdue-invoice-event.html");
								break;
							case "notificationpaidsent":
								sendEmail(mediaArchive, contact, invoice, "Payment Received "+workspace, "send-paid-invoice-event.html");
								break;
						}
					}
				}
			}

			invoice.setValue(iteratorType, "true");
			invoiceSearcher.saveData(invoice);
		}
	}
}
 * */

/*
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
	}
	actionUrl = URLUtilities.urlEscape(actionUrl);
	
	String supportUrl = getSiteRoot() + "/" + appid + "/collective/services/index.html?collectionid=" + invoice.getValue("collectionid");

	WebEmail templateEmail = mediaArchive.createSystemEmail(contact, template);
	templateEmail.setSubject(subject);
	Map objects = new HashMap();
	objects.put("followeruser", contact);
	objects.put("mediaarchive", mediaArchive);
	objects.put("invoice", invoice);
	objects.put("supporturl", supportUrl);
	objects.put("actionurl", actionUrl);
	objects.put("siteroot", getSiteRoot());
	objects.put("applink", appid);
	templateEmail.send(objects);
	log.info("Email sent to: "+contact);
}
*/

private String getSiteRoot() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	String site = mediaArchive.getCatalogSettingValue("siteroot");
	if (!site) {
		site = mediaArchive.getCatalogSettingValue("cdn_prefix");
	}
	return site;
}

init();





