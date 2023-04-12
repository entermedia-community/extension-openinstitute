package org.entermedia.invoice;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.entermediadb.asset.MediaArchive;
import org.entermediadb.asset.modules.BaseMediaModule;
import org.openedit.MultiValued;
import org.openedit.WebPageRequest;
import org.openedit.util.DateStorageUtil;

public class InvoiceModule extends BaseMediaModule
{
	public void loadInvoiceManager(WebPageRequest inReq)
	{
		String catalogid = inReq.findPathValue("catalogid");
		InvoiceManager manager = (InvoiceManager) getModuleManager().getBean(catalogid, "invoiceManager");
	
		inReq.putPageValue("invoiceManager", manager);
	}
	
	public void saveNewInvoiceForStore(WebPageRequest inReq)
	{
		MediaArchive mediaArchive = getMediaArchive(inReq);
		InvoiceManager manager = (InvoiceManager) mediaArchive.getBean("invoiceManager");
		//Grab data?
		MultiValued invoice = (MultiValued)inReq.getPageValue("data");
		
		String range = inReq.getRequestParameter("datarange");
		if( range != null)
		{
			String[] parts = range.split(" ");
			Date start = DateStorageUtil.getStorageUtil().parseFromStorage(parts[0]);
			invoice.setValue("duedate",start);
			Date enddate = DateStorageUtil.getStorageUtil().parseFromStorage(parts[2]);
			Calendar endcal = new GregorianCalendar();
			endcal.setTime(enddate);
			int year = endcal.get(Calendar.YEAR);  // 2012
			if( year < 1000)
			{
				endcal.set(Calendar.YEAR, year + 2000);
				enddate = endcal.getTime();
			}
			
			invoice.setValue("enddate",enddate);
		}
		
		String productid = inReq.getRequestParameter("productlist.add");
		if( productid != null)
		{
			MultiValued product = (MultiValued)mediaArchive.getData("collectiveproduct", productid);
			manager.saveNewInvoiceForStore(mediaArchive, invoice, product);
			inReq.putPageValue("data",product); //Render on details  page
			inReq.setRequestParameter("invoiceid", invoice.getId());
		}
		else
		{
			//Just editing an existing invoice
			productid = inReq.getRequestParameter("productid");
			MultiValued product = (MultiValued)mediaArchive.getData("collectiveproduct", productid);
			inReq.putPageValue("data",product); //Render on details  page
		}
		//TODO email someone
		//Depending on the producttype??
		inReq.putPageValue("status","saved");
		
	}


}
