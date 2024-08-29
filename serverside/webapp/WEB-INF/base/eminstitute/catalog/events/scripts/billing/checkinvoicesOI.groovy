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
   Map customer = stripe.getCustomer(mediaArchive,  "billing+" + invoice.getValue("collectionid") + "@entermediadb.com");

   if (customer != null) {
    Map sourcesData = customer.get("sources");
    ArrayList sources = sourcesData.get("data");
    if (sources.size() > 0) {
     Map source = sources.get(0);
     Searcher payments = mediaArchive.getSearcher("transaction");
     Data payment = payments.createNewData();
     payment.setValue("paymenttype","stripe" );
     payment.setValue("totalprice", invoice.getValue("totalprice"));
     boolean chargeSuccess = stripe.createCharge(mediaArchive, payment, customer.get("id"));
 
     if (chargeSuccess ) {
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
    productItem.put("productname", product.getValue("name"));
    String productname = product.getValue("name");
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
    if(productname != null) {
     invoice.setValue("name", productname);
    }
    else if(collection != null) {
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
    log.info("Invoice Created for recurring product for: " +collection);
    
    String recurrentCount = product.getValue("recurringperiod");
    int currentMonth = nextBillOn.getMonth();
    nextBillOn.setMonth(currentMonth + Integer.parseInt(recurrentCount) );
    product.setValue("nextbillon", nextBillOn);
    product.setValue("lastgeneratedinvoicedate", today.getTime());
    productSearcher.saveData(product);
    log.info("Next Invoice for: "+collection+", will be generated: " +nextBillOn);
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
  Date lastbilldate = product.getValue(" ");
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


private String getSiteRoot() {
 MediaArchive mediaArchive = context.getPageValue("mediaarchive");
 String site = mediaArchive.getCatalogSettingValue("siteroot");
 if (!site) {
  site = mediaArchive.getCatalogSettingValue("cdn_prefix");
 }
 return site;
}

init();





