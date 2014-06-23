package com.thinkfirst.icashbook;
/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public interface CashBookConstants{
    public static final int CREDIT=1;
    public static final int DEBIT=~CREDIT;//invert. its one's complement
    public static final int OP_ADD=0;//add operation
    public static final int OP_EDIT=1;//edit operation... provided this does not affect CREDIT above
    public static final int OP_DELETE=2;//delete operation
    public static final java.awt.Font NUMERIC_FONT=App.getFont("sylfaen.ttf");
    public static final java.awt.Font OTHER_FONT=App.getFont("tahoma.ttf");//ARIAL FONT
            //5pt font in corel draw synonymous to 20f in java (assert)
    public static final int MAX_NUMBER_OF_DAILY_TRANSACTIONS=1580;
    public static final int MAX_NUM_CHARS_IN_PARTICULAR_FIELD=200;
    public static final java.io.File APP_DIR=new java.io.File(System.getProperty("user.home")+java.io.File.separator+"icashbook_db"+java.io.File.separator);//App directory
    public static final String[]MONTHS={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
}
