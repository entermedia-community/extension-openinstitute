import org.entermediadb.asset.MediaArchive
import org.openedit.Data
import org.openedit.data.Searcher
import org.openedit.BaseWebPageRequest
import org.openedit.util.DateStorageUtil
import org.openedit.util.RequestUtils
import org.entermediadb.email.PostMail
import org.entermediadb.email.TemplateWebEmail


public void init() {

	//Notify to Email:
	//String notifyemail = "sales@entermediadb.org";  //get it from catalog settings?
	
	String notifyemail = "cristobal@entermediadb.org";  //get it from catalog settings?

	//prevent re-submition
	 String clientform = context.getSessionValue("clientform");
	 if (clientform != null) {
	 	context.putSessionValue("clientform", null);
	 }
	 else {
	 	context.putPageValue("errorcode", "1");
	 	log.info("Form re-submit prevented");
	 	return;
	 }

	MediaArchive archive = context.getPageValue("mediaarchive");
	Boolean valid = true;
	String field1 = context.getRequestParameter("username"); //Fake fields for spamm control
	if(field1 != null) {
		valid = false;
	}
	String field2 = context.getRequestParameter("email");
	if(field2 != null) {
		valid = false;
	}
	if(!valid) {
		log.info("Invalid form request. Possible Spam.");
		return;
	}
	
	
	
	context.putPageValue("subject", "Library Request");
	HashMap htmlfields = new HashMap();
	htmlfields.put("user", context.getRequestParameter("user"));
	htmlfields.put("libraryname", context.getRequestParameter("projectname"));
	htmlfields.put("currentproject", context.getRequestParameter("project"));
	
	context.putPageValue("fields", htmlfields);
	
	//Message requester info
	context.putPageValue("messagetime", new Date() );
	String ipaddress = context.getRequest().getRemoteAddr();
	String senderinfo = "Url: "+context.getPageValue("siteroot");
	if (context.getPageValue("referringPage") != null) {
		senderinfo = senderinfo + " Refering page: "+context.getPageValue("referringPage");
	}
	if (context.getPageValue("page") != null) {
		senderinfo = senderinfo + " Page: "+context.getPageValue("page");
	}
	senderinfo = senderinfo + " Ip: " + ipaddress;
	context.putPageValue("senderinfo",   senderinfo);
	
	String communityhome = context.getPageValue("communityhome");
	String templateSrc = communityhome+"/meetingschedule/notifyemailnewlibrary.html";
	sendEmail(context.getPageMap(), notifyemail, templateSrc);
}






protected void sendEmail(Map pageValues, String email, String templatePage){
	//send e-mail
	//Page template = getPageManager().getPage(templatePage);
	RequestUtils rutil = moduleManager.getBean("requestUtils");
	BaseWebPageRequest newcontext = rutil.createVirtualPageRequest(templatePage, null, null);
	
	newcontext.putPageValues(pageValues);

	PostMail mail = (PostMail)moduleManager.getBean("postMail");
	TemplateWebEmail mailer = mail.getTemplateWebEmail();
	mailer.loadSettings(newcontext);
	mailer.setMailTemplatePath(templatePage);
	mailer.setRecipientsFromCommas(email);
	mailer.send();
	log.info("email sent to ${email}");
}

init();


