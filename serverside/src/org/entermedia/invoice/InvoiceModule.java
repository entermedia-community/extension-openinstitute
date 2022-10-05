package org.entermedia.invoice;

import org.entermediadb.asset.MediaArchive;
import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.MultiValued;
import org.openedit.WebPageRequest;

public class InvoiceModule extends BaseMediaModule
{
	public void loadInvoiceManager(WebPageRequest inReq)
	{
		String catalogid = inReq.findValue("catalogid");
		InvoiceManager manager = (InvoiceManager) getModuleManager().getBean(catalogid, "invoiceManager");
	
		inReq.putPageValue("invoiceManager", manager);
	}
	
	public void saveNewInvoiceForStore(WebPageRequest inReq)
	{
		MediaArchive mediaArchive = getMediaArchive(inReq);
		InvoiceManager manager = (InvoiceManager) mediaArchive.getBean("invoiceManager");
		//Grab data?
		MultiValued invoice = (MultiValued)inReq.getPageValue("data");
		
		String productid = inReq.getRequestParameter("productlist.add");
		if( productid != null)
		{
			MultiValued product = (MultiValued)mediaArchive.getData("collectiveproduct", productid);
			manager.saveNewInvoiceForStore(mediaArchive, invoice, product);
			inReq.putPageValue("data",product); //Render on details  page
		}
		else
		{
			productid = inReq.getRequestParameter("productid");
			MultiValued product = (MultiValued)mediaArchive.getData("collectiveproduct", productid);
			inReq.putPageValue("data",product); //Render on details  page
		}
		//TODO email someone
		
	}


}
