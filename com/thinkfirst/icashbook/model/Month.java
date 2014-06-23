package com.thinkfirst.icashbook.model;

import com.thinkfirst.icashbook.model.Date;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Month implements java.io.Serializable{
    private java.util.ArrayList<Date> dates=new java.util.ArrayList<Date>(6);
    final int year;//the year that created this month;
    private int month;
    private static String[] monthStrings=java.text.DateFormatSymbols.getInstance().getMonths();
    private int lastAddedCalendarDate=-1;
    public Month(int year,int month){
        this.year=year;
        this.month=month;
    }
    public int getYear(){
        return year;
    }
    public int getValue(){
        return month;
    }
    public java.lang.Iterable<Date>getDates(){
        return dates;
    }
    
    /**
     * 
     * @param calendarDate the calendar value of the date
     * @return the date with the specified calendar value e.g. getDate(1) for the first day of the month
     * <br>If the date specified is not in the list, null is returned.
     */
    public Date getDate(int calendarDate){
        for(Date date:getDates())
            if(date.getValue()==calendarDate)
                return date;
        return null;
    }
    public Date addCalendarDate(int calendarDate){
        for(Date date:dates)
            if(date.getValue()==calendarDate)
                return date;
        Date date;
        ++lastAddedCalendarDate;
        dates.add(date=new Date(year,month,calendarDate));
        return date;
    }
    public Date getLastAddedCalendarDate(){
        return lastAddedCalendarDate==-1?null:dates.get(lastAddedCalendarDate);
    }
    public String toString(){
        return getLastAddedCalendarDate()+", "+monthStrings[month];
    }
}
