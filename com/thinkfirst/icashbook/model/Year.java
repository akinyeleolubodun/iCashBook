package com.thinkfirst.icashbook.model;

import com.thinkfirst.icashbook.model.Month;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Year implements java.io.Serializable{
    private int year;
    private java.util.ArrayList<Month> months=new java.util.ArrayList<Month>(1);
    private int lastAddedMonthIndex=-1;//the last month that was added
    
    public Year(int year){
        this.year=year;
    }
    public int getValue(){
        return year;
    }
    /**
     * Allows you to add a new month.
     * This means that you're are not obliqued to add the months in the normal order.<br>
     * For instance, you could add Calendar.FEBRUARY, Calendar.OCTOBER, and Calendar.NOVEMBER<b>
     * This way space is economized by not creating months for the other months you didn't add.
     * @param calendarMonth The index of the month e.g. Calendar.MAY
     * @return the Month specified by this calendarMonth.
     */
    public Month addCalendarMonth(int calendarMonth){
        //ensure that this month does not exist before
        for(Month month:getMonths())
            if(month.getValue()==calendarMonth)
                return month;
        Month month;
        ++lastAddedMonthIndex;
        months.add(month=new Month(year,calendarMonth));//this always returns true but i'm not using it coz i dont wanna take any risks here
        return month;
    }
    public Month getLastAddedMonth(){
        return lastAddedMonthIndex==-1?null:months.get(lastAddedMonthIndex);
    }
    /**
     * 
     * @return an iterable for the months that have been added to this year.
     */
    public java.lang.Iterable<Month>getMonths(){
        return months;
    }
    /**
     * @param calendarMonth the calendar value of the month
     * @return the month with the specified Calendar value. e.g. getMonth(Calendar.JANUARY);
     * if the month specified is not in the list, null is returned.
     */
    public Month getMonth(int calendarMonth){
        for(Month month:getMonths())
            if(month.getValue()==calendarMonth)
                return month;
        return null;
    }
    
    public String toString(){
        return getLastAddedMonth()+", "+year;
    }
    
}