package org.openinstitute.finance;

import java.util.Date;

import org.openedit.MultiValued;

public class BankTransaction
{
	public BankTransaction(String inSearchType, MultiValued inData)
	{
		setSearchType(inSearchType);
		setData(inData);
	}
	public BankTransaction()
	{
	}
	String fieldSearchType;
	public String getSearchType()
	{
		return fieldSearchType;
	}
	public void setSearchType(String inSearchType)
	{
		fieldSearchType = inSearchType;
	}
	MultiValued fieldData;
	
	public MultiValued getData()
	{
		return fieldData;
	}
	public void setData(MultiValued inData)
	{
		fieldData = inData;
	}
	public Date getDate()
	{
		Date date = null;
		if( getSearchType().equals("transaction") )
		{
			date = getData().getDate("paymentdate");
		}
		else if( getSearchType().equals("collectiveinvoice") )
		{
			date = getData().getDate("invoicepaidon");
		}
		else
		{
			date = getData().getDate("date");
		}
		if( date == null)
		{
			return new Date(0);
		}
		return date;
	}
	
	public Double getAmount()
	{
		Double t = 0D;
		if( getSearchType().equals("collectiveexpense") )
		{
			t = 0 - getData().getDouble("total");
		}
		else if( getSearchType().equals("collectiveincome") )
		{
			t = getData().getDouble("total");
		}
		else
		{
			t = getData().getDouble("totalprice");
		}
		return t;
	}

	public String getCollectionId()
	{
		String collectionid = getData().get("collectionid");
		return collectionid;
	}

	public String getCurrencyType()
	{
		String currencytype = getData().get("currencytype");
		if( currencytype == null)
		{
			currencytype = "1";
		}
		return currencytype;
	}
	
	public String getName()
	{
		String name = null;
		if( getSearchType().equals("transaction") )
		{
			name = getData().get("paymentemail");
		}
		else if( getSearchType().equals("collectiveexpense") )
		{
			name = getData().get("expensedescription");
		}
		else if( getSearchType().equals("collectiveincome") )
		{
			name = getData().get("incomedescription");
		}
		else if( getSearchType().equals("collectiveinvoice") )
		{
			name = getData().get("name");
			if( name == null)
			{
				name = getData().get("invoicedescription");
			}
			if( name == null)
			{
				name = "#" + getData().get("invoicenumber");
			}
		}
		return name;
	}
}