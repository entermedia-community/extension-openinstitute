package org.openinstitute.finance;

import java.util.Calendar;
import java.util.Date;

public class DateRange
{
	Date fieldStartDate;
	
	boolean fieldAllTime;
	int fieldYearPicked;
	public int getYearPicked()
	{
		return fieldYearPicked;
	}
	public void setYearPicked(int inYearPicked)
	{
		fieldYearPicked = inYearPicked;
	}
	public int getMonthPicked()
	{
		return fieldMonthPicked;
	}
	public void setMonthPicked(int inMonthPicked)
	{
		fieldMonthPicked = inMonthPicked;
	}
	int fieldMonthPicked;
	
	public boolean isAllTime()
	{
		return fieldAllTime;
	}
	public void setAllTime(boolean inAllTime)
	{
		fieldAllTime = inAllTime;
	}
	public Date getStartDate()
	{
		return fieldStartDate;
	}
	public void setStartDate(Date inStartDate)
	{
		fieldStartDate = inStartDate;
	}
	public Date getEndDate()
	{
		return fieldEndDate;
	}
	public void setEndDate(Date inEndDate)
	{
		fieldEndDate = inEndDate;
	}
	Date fieldEndDate;
	
	public void setYearAndMonth(int yearsback, int inMonth)
	{
		setYearPicked(yearsback);
		setMonthPicked(inMonth);
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.set(Calendar.YEAR,year - yearsback);
		cal.set(Calendar.MONTH, inMonth - 1);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date start = cal.getTime();
		setStartDate(start);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH,-1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		
		Date onemonth = cal.getTime();
		setEndDate(onemonth);
	}
	public void setYearToDate(int yearsback)
	{
		setYearPicked(yearsback);
		setMonthPicked(0);
		Calendar cal = Calendar.getInstance();
		
		int year = cal.get(Calendar.YEAR);
		cal.set(Calendar.YEAR,year - yearsback);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH,1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Date start = cal.getTime();
		setStartDate(start);
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.YEAR,year - yearsback);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH,days);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		
		Date onemonth = cal.getTime();
		setEndDate(onemonth);
	}

}
