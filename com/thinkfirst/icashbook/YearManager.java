package com.thinkfirst.icashbook;

import com.thinkfirst.icashbook.model.Year;
import com.thinkfirst.icashbook.model.Date;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class YearManager implements java.io.Serializable{
    private java.util.ArrayList<Year>years=new java.util.ArrayList<Year>(1);
    private int lastAddedCalendarYear=-1;
    private static YearManager yearManager=new YearManager();    
    private java.util.Calendar startCalendar;//holds the date the database was created and hence, the date when this application began operation.
    
    
    private YearManager(){startCalendar=new java.util.GregorianCalendar();}
    
    public static YearManager getInstance(){
        return yearManager;
    }
    
    /**
     * If the calendar year specified already exists in the list, it is simply returned, else
     * a new one is created and returned.
     * @param calendarYear
     * @return this calendar year specified.
     */
    public Year addCalendarYear(int calendarYear){
        for(Year year:years)
            if(year.getValue()==calendarYear)
                return year;            
        Year year;
        ++lastAddedCalendarYear;
        years.add(year=new Year(calendarYear));
        return year;
    }
    public Year getYear(int calendarYear){
        for(Year year:years)
            if(year.getValue()==calendarYear)
                return year;
        return null;
    }
    public Year getLastCalendarYear(){
        return lastAddedCalendarYear==-1?null:years.get(lastAddedCalendarYear);
    }
    public java.lang.Iterable<Year>getYears(){
        return years;
    }
    public java.util.Calendar getStartCalendar(){
        return (java.util.Calendar)startCalendar.clone();
    }
    /***
     * Retrieves the date for this calendar.<br>
     * Note that this method will never return null.<br>
     * If the year in the calendar is before 2013, a NullPointerException is thrown.<br>
     * If Date specified by this calendar didn't exist before
     * it will be created and returned otherwise if it existed before it will simply be returned.<br>
     * @param calendar the calendar object used to locate the Date
     * @param create if true, a date is created and we're guaranteed that null won't be returned,
     * else if the date does not exist, then null is returned.
     * @return the Date given by the calendar
     */
    public Date getDate(java.util.Calendar calendar){
        int c=calendar.get(java.util.Calendar.YEAR)-2013;
        if(c<0)
            throw new NullPointerException("calendar year: "+c);
        Date theDate=addCalendarYear(c)                                         //get the year
                .addCalendarMonth(calendar.get(java.util.Calendar.MONTH))       //get the month
                .addCalendarDate(calendar.get(java.util.Calendar.DATE));        //get the date
        return theDate;
    }
}
