package billing;

import org.entermedia.stripe.StripePaymentProcessor
import org.entermediadb.asset.MediaArchive
import org.entermediadb.email.WebEmail
import org.openedit.*
import org.openedit.data.Searcher
import org.openedit.users.User
import org.openedit.util.URLUtilities

public void init() {
	MediaArchive mediaArchive = context.getPageValue("mediaarchive");
	Searcher productSearcher = mediaArchive .getSearcher("collectiveproduct");
	Searcher invoiceSearcher = mediaArchive .getSearcher("collectiveinvoice");

	generateInvoice(mediaArchive, productSearcher);  //status=active
	
	

}

private void generateInvoice(MediaArchive mediaArchive, Searcher productSearcher) {
	int daysToExpire = 7; // invoice will expire in (days)
	Calendar today = Calendar.getInstance();
	Calendar due = Calendar.getInstance();
	due.add(Calendar.DAY_OF_YEAR, -5); // make invoice 5 days before next bill date

	
	
	String productid = context.getRequestParameter("id");
	String invoiced = context.getRequestParameter("invoiced");
	
	if (productid == null) {
		productid = context.getPageValue("id");
	}
	MultiValued product = productSearcher.searchById(productid);
	//if(product != null && product.getValue("billingstatus") == "active" && invoiced != "true")
	if(product != null)
	{
		
		Date lastbilldate = product.getValue("lastgeneratedinvoicedate");
//		if (lastbilldate < nextBillOn) { // otherwise assume it's already created
			Searcher invoiceSearcher = mediaArchive.getSearcher("collectiveinvoice");
			Data invoice = invoiceSearcher.createNewData();

			Calendar invoiceDue = Calendar.getInstance();
			invoiceDue.add(Calendar.DAY_OF_YEAR, daysToExpire);

			//Move this to aux-table?
			HashMap<String,Object> productItem = new HashMap<String,Object>();
				productItem.put("productid", product.getValue("id"));
				productItem.put("productname", product.getValue("name"));
				productItem.put("productdescription", product.getValue("productdescription"));
				productItem.put("productquantity", 1 );
				productItem.put("productprice", product.getValue("productprice"));
			Collection items = new ArrayList();
			items.add(productItem);
			//--
			
			invoice.setValue("productlist", items);
			invoice.setValue("paymentstatus", "pending");
			invoice.setValue("isautopaid", product.getValue("isautopaid"));
			invoice.setValue("collectionid", product.getValue("collectionid"));
			invoice.setValue("owner", product.getValue("owner"));
			invoice.setValue("totalprice", product.getValue("productprice"));
			invoice.setValue("duedate", invoiceDue.getTime());
			invoice.setValue("invoicedescription", product.getValue("productdescription"));
			invoice.setValue("notificationsent", "false");
			invoice.setValue("createdon", today.getTime());
			
			//fix currencyprice if not defined deault to USD
			String productcurrency = product.getValue("currencytype");
			if (productcurrency == null) {
				productcurrency = '1';
			}
			invoice.setValue("currencytype",  productcurrency);
			
			
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
			
			
			int recurrentCount = product.getValue("recurringperiod")
			if(recurrentCount != null) 
			{
				Date nextBillOn = today.getTime();
				int currentMonth = nextBillOn.getMonth();
				nextBillOn.setMonth(currentMonth + recurrentCount);
				product.setValue("nextbillon", nextBillOn);
				
				invoice.setValue("isrecurring", "true");
				invoice.setValue("billdate", today.getTime()); //Original Bill Date
			}
			
			invoiceSearcher.saveData(invoice);
			
			product.setValue("lastgeneratedinvoicedate", today.getTime());
			productSearcher.saveData(product);
			
			
			
			context.putPageValue("invoiceid", invoice.getId())
			
			log.info("New Invoice generated - " + invoice.getValue("invoicenumber"));
		}
		
//	}
	
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
