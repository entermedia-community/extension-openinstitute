package org.entermedia.invoice;

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.entermediadb.asset.MediaArchive;
import org.openedit.CatalogEnabled;
import org.openedit.Data;
import org.openedit.ModuleManager;
import org.openedit.MultiValued;
import org.openedit.data.Searcher;
import org.openedit.users.User;

public class InvoiceManager implements CatalogEnabled
{
	private static final Log log = LogFactory.getLog(InvoiceManager.class);
	protected String fieldCatalogId;
	protected MediaArchive fieldMediaArchive;
	protected ModuleManager fieldModuleManager;
	protected DecimalFormat df = new DecimalFormat("#.00"); 


	public int getUnpaidInvoiceNumber(String inCollectionId)
	{
		int res = 0;
		Collection<Data> clientinvoices = getMediaArchive().query("clientinvoice").match("clientinvoicestatus", InvoiceStatus.UNPAID).exact("librarycollection", inCollectionId).search();

		if (clientinvoices != null)
		{
			res = clientinvoices.size();
		}
		return res;
	}

	public int getPaidInvoiceNumber(String inCollectionId)
	{
		int res = 0;
		Collection<Data> clientinvoices = getMediaArchive().query("clientinvoice").match("status", InvoiceStatus.PAID).exact("librarycollection", inCollectionId).search();

		if (clientinvoices != null)
		{
			res = clientinvoices.size();
		}
		return res;
	}

	public int getCancelledInvoiceNumber(String inCollectionId)
	{
		int res = 0;
		Collection<Data> clientinvoices = getMediaArchive().query("clientinvoice").match("status", InvoiceStatus.CANCELLED).exact("librarycollection", inCollectionId).search();
		
		if (clientinvoices != null)
		{
			res = clientinvoices.size();
		}
		return res;
	}

	public int getRefundedInvoiceNumber(String inCollectionId)
	{
		int res = 0;
		Collection<Data> clientinvoices = getMediaArchive().query("clientinvoice").match("status", InvoiceStatus.REFUNDED).exact("librarycollection", inCollectionId).search();

		if (clientinvoices != null)
		{
			res = clientinvoices.size();
		}
		return res;
	}


	
	public String getAccountBalance(String inCollectionId)
	{
		Searcher invoiceSearcher = getMediaArchive().getSearcher("clientinvoice");
		Collection<Data> clientinvoices = invoiceSearcher.query().match("clientinvoicestatus", InvoiceStatus.UNPAID).exact("librarycollection", inCollectionId).search();
		double amount = 0;

		for (Data it : clientinvoices)
		{
			MultiValued real = (MultiValued) invoiceSearcher.loadData(it);

			try
			{
				amount += (double)real.getValue("amount");
			}
			catch (Exception e)
			{
				log.error("Can't retrieve/parse invoice " + real.get("name") + " amount", e);
			}
		}
		return df.format(amount);
	}

	public String getCatalogId()
	{
		return fieldCatalogId;
	}

	public void setCatalogId(String inCatalogId)
	{
		fieldCatalogId = inCatalogId;
	}
	public MediaArchive getMediaArchive()
	{
		if (fieldMediaArchive == null)
		{
			fieldMediaArchive = (MediaArchive)getModuleManager().getBean(getCatalogId(), "mediaArchive");
		}
		return fieldMediaArchive;
	}

	public void setMediaArchive(MediaArchive inMediaArchive)
	{
		fieldMediaArchive = inMediaArchive;
	}

	public ModuleManager getModuleManager()
	{
		return fieldModuleManager;
	}

	public void setModuleManager(ModuleManager inModuleManager)
	{
		fieldModuleManager = inModuleManager;
	}

	protected void saveNewInvoiceForStore(MediaArchive mediaArchive, MultiValued invoice, MultiValued product)
	{
		double totalcost = calculatePricePerDay(product, invoice);
		
		Map map = new HashMap();
		map.put("productid",product.getId());
		map.put("productquantity","1");
		map.put("productprice", totalcost );
		
		List products = new ArrayList(1);
		products.add(map);
		invoice.setValue("productlist", products);
		invoice.setValue("totalprice",totalcost);
		if( invoice.getValue("currencytype") == null)
		{
			invoice.setValue("currencytype",product.get("currencytype"));
		}
		//Grab email from user who ordered the product
		User sentto = mediaArchive.getUser(invoice.get("forcustomer"));
		User sentto2 = mediaArchive.getUser(invoice.get("owner"));
		StringBuffer to = new StringBuffer();
		if( sentto != null && sentto.getEmail() != null)
		{
			to.append(sentto.getEmail());
		}
		if( sentto != sentto2 && sentto2 != null && sentto2.getEmail() != null)
		{
			if(sentto != null && sentto.getEmail() != null)
			{
				to.append(",");
			}
			to.append(sentto2.getEmail());
		}
		if( to.length() > 0)
		{
			invoice.setValue("sentto",to.toString()); //Collective admin plus the users
		}
		invoice.setValue("paymentstatus","sendinvoice");
		
		mediaArchive.saveData("collectiveinvoice", invoice);
		mediaArchive.fireSharedMediaEvent("billing/sendinvoices");
		
	}

	public double calculatePricePerDay(MultiValued product, MultiValued invoice)
	{
		Date startdate = invoice.getDate("startdate");
		Date enddate = invoice.getDate("enddate");
		long noOfDaysBetween = ChronoUnit.DAYS.between(startdate.toInstant(), enddate.toInstant());
		
		double price = product.getDouble("productprice");
		double totalcost = price * noOfDaysBetween;
		return totalcost;
	}
	
}
