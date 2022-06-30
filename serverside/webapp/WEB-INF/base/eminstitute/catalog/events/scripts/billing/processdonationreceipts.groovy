package billing;

import org.openedit.util.DateStorageUtil

import org.entermedia.stripe.StripePaymentProcessor
import org.entermediadb.asset.MediaArchive
import org.entermediadb.email.WebEmail
import org.openedit.*
import org.openedit.data.Searcher
import org.openedit.users.User
import org.openedit.util.URLUtilities


public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher transactionSearcher = mediaArchive.getSearcher("transaction");
	//Searcher receiptSearcher = mediaArchive .getSearcher("donationreceipt");
	
	sendDonationReceipt(mediaArchive, transactionSearcher);
}

private void sendDonationReceipt(MediaArchive mediaArchive, Searcher transactionSearcher) {
	Collection pendingNotification = transactionSearcher.query()
			.exact("receiptstatus","new").search();

	//log.info("Sending Notification for " + pendingNotification.size() + " Donations");
	sendReceipt(mediaArchive, transactionSearcher, pendingNotification, "notificationsent");
}

private void sendReceipt(MediaArchive mediaArchive, Searcher transactionSearcher, Collection receipts, String iteratorType) {
	for (Iterator receiptIterator = receipts.iterator(); receiptIterator.hasNext();) {
		Data receipt = transactionSearcher.loadData(receiptIterator.next());
		
		String receiptemail = receipt.getValue("paymentemail");
		
		if (receiptemail != null) {
			String emailbody = "";
			String subject = "";
			
			Data collection = mediaArchive.getData("librarycollection", receipt.getValue("collectionid"));
					
			Map objects = new HashMap();
			
			objects.put("mediaarchive", mediaArchive);
			
			objects.put("receipt", receipt);
			
			String dates = DateStorageUtil.getStorageUtil().formatDateObj(receipt.getValue("paymentdate"), "dd-MM-YYYY");
			objects.put("donationdate", dates);
			
			User user = mediaArchive.getUser(receipt.getValue("userid"));
			if(user != null)
			{
				objects.put("donor", (String) user.getScreenName());
			}
			else 
			{
				objects.put("donor", "Sponsor");
			}

			objects.put("amount", "\$" + context.doubleToMoney(receipt.getValue("totalprice"), 2) );
			
			String currencytype = mediaArchive.getCachedData("currencytype", receipt.getValue("currencytype"));
			objects.put("currency", currencytype);
			
			String collection_url = '';
			String collection_url_donation = '';
			
			String appid = context.findValue("sitelink");
			
			appid = 'app';  //Sitelink not working
			
			if (collection != null) {
				objects.put("project", collection);
				
				collection_url = getSiteRoot() + "/" + appid + "/collective/channel/"+collection.getId() +"/"+ URLUtilities.dash(collection.getName()) + ".html";
				objects.put("project_url", collection_url);
				
				collection_url_donation = getSiteRoot() + "/" + appid + "/collective/donate/"+collection.getId()+"/donate.html"
				objects.put("project_url_donation", collection_url_donation);

				//log.info(collection_url_donation);
			}
			
			
			//get emailbody from collection
			if(collection != null) {
				if (collection.getValue("donationemailtemplate")) {
					emailbody = (String) collection.getValue("donationemailtemplate");
				}
				if (collection.getValue("donationemailsubject")) {
					subject = (String) collection.getValue("donationemailsubject");
				}
			}
			//default emailbody+subject
			if (emailbody.equals("")) {
				emailbody = (String) mediaArchive.getCatalogSettingValue("donation_email_body");
			}
			if (emailbody.equals("")) {
				emailbody = "Thank you for your Donation.";
			}
			if (subject.equals("")) {
				subject =  "${project} Donation Receipt";
			}
			
			String body = mediaArchive.getReplacer().replace(emailbody, objects);
			subject = mediaArchive.getReplacer().replace(subject, objects);
		
			WebEmail templateEmail = mediaArchive.createSystemEmailBody(receiptemail);
			templateEmail.setSubject(subject);
			templateEmail.loadSettings(context);
			templateEmail.send(body);
			
			receipt.setValue("receiptstatus", "sent");
			transactionSearcher.saveData(receipt);
			
			log.info("Email sent to: " + receiptemail);
		}
	}
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
