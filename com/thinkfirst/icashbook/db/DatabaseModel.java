/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkfirst.icashbook.db;
import com.thinkfirst.icashbook.CashBookConstants;
import com.thinkfirst.icashbook.YearManager;
import com.thinkfirst.icashbook.dto.ATransaction;
import com.thinkfirst.icashbook.dto.TransactionRange;
import com.thinkfirst.icashbook.model.Operation;
/**
 * @author ESSIENNTA EMMANUEL
 * 
 * META_TABLE -> TABLE_COUNT, 
 * write current number of tables to META_TABLE before exiting app
 * on first launch after installation, create META_TABLE in database
 * Items to store in META_TALBE:
 * 1. startCalendar - The date of first launch
 * 2. numberOfTables - The number of user tables in the database as at when the app was last shutdown.
 * 
 * append ;create=true to database URL
 * ability to retrieve information from the version 1.0 app and move them to this version.
 * @version 1.1
 */
public class DatabaseModel extends java.util.Observable{
    private String databaseURL="jdbc:derby:"+CashBookConstants.APP_DIR.getAbsolutePath();//Place in user's home directory
    private String driver="org.apache.derby.jdbc.EmbeddedDriver";
    private java.sql.Connection connection;
    private java.sql.Statement statement;
    private String tableName;
    
    
    private java.util.Calendar calendarInUse;//the calendar that is currently in use by the application.
//    private Date dateInUse;
    private YearManager yearManager;
    private java.io.ObjectInputStream ois;
    private java.io.ObjectOutputStream oos=null;
    private java.io.RandomAccessFile raf;
    private java.io.File dir=CashBookConstants.APP_DIR;
    private String fileName="db.ctft";//database cashbook thinkfirsttechnology
    private String backup="db_backup.ctft";//backup
    private java.io.File db=new java.io.File(dir,fileName);
    private java.io.File db_backup=new java.io.File(dir,backup);
    private int filePointer;
    private boolean atLeast1saveOperation;
    private java.io.FileInputStream fis=null;
    private static java.io.PrintWriter intoLog;
    private String logFile="log.txt";
    
    public DatabaseModel(){
        setDate(calendarInUse=new java.util.GregorianCalendar());
        java.io.File logFile=new java.io.File(CashBookConstants.APP_DIR,"derby.log");
        System.setProperty("derby.stream.error.file",logFile.getAbsolutePath());
        try{
            boolean firstTime=false;
            Class.forName(driver).newInstance();
            try{
                connection=java.sql.DriverManager.getConnection(databaseURL);
            }catch(java.sql.SQLException sqle){
                if(sqle.getSQLState().equalsIgnoreCase("XJ004")){//The database didn't exists. Presumably the first launch.
                    firstTime=true;
                    errorMessage(
                            "The database doesn't already exist.\nApplication will now create it.",
                            sqle);
                    try{
                        connection=java.sql.DriverManager.getConnection(databaseURL+";create=true");
                        //After database creation, create META_TABLE
                        //set startCalendar

                    }catch(java.sql.SQLException sqle2){
                        throw sqle2;
                    }
                }
            }
            connection.setAutoCommit(false);
            statement=connection.createStatement();
            
            if(firstTime){
                statement.executeUpdate("CREATE TABLE META_TABLE(name VARCHAR(20) NOT NULL PRIMARY KEY, value BIGINT NOT NULL)");
                java.util.Calendar calendar=new java.util.GregorianCalendar();
                //Reset to mid-night
                calendar.set(java.util.Calendar.HOUR_OF_DAY,0);
                calendar.set(java.util.Calendar.MINUTE,0);
                calendar.set(java.util.Calendar.SECOND,0);
                calendar.set(java.util.Calendar.MILLISECOND,0);
                statement.executeUpdate("INSERT INTO META_TABLE VALUES('startCalendar',"+calendar.getTimeInMillis()+")");
                connection.commit();
            }
            
            /*Close statement object on finalization*/
            
            
        }catch(java.lang.Exception e){
            errorMessage("Error occured during database initialization.\n\n"+e.getMessage()+"\n\nPress OK to exit.",e);
            if(connection!=null)try{connection.close();}catch(Exception ef){}
            System.exit(1);
        }
    }
    /**
     * Saves the current state of the program data.
     * @param parent The component that serves as the parent of the dialog box used if there are any errors along the line.
     */
    public void saveData(java.awt.Component parent){
        System.out.println("commiting data");
        try{connection.commit();}catch(java.sql.SQLException sqle){
            errorMessage("Error occured while saving transactions.",sqle);            
        }
    }
    /**
     * Create a new table
     * @return true if the table didn't exist and was successfully created, false if the table already existed.
     */
    private boolean createTable(){
//        if(containsTable(tableName)){//The table already exists in the database
//            System.err.println("_Table already exists "+tableName);
//            return;
//        }
//        System.out.println("_Creating table "+tableName);
        try{
            statement.executeUpdate("CREATE TABLE "+tableName+" ("
                    + "particular VARCHAR("+CashBookConstants.MAX_NUM_CHARS_IN_PARTICULAR_FIELD+"),"
                    + "amount INT NOT NULL,"
                    + "mode INT,"
                    + "date BIGINT NOT NULL PRIMARY KEY)");
        }catch(java.sql.SQLException sqle){
            if(sqle.getSQLState().equalsIgnoreCase("X0Y32"))//The table already existed. Return
                return false;
            errorMessage(sqle);
        }
        return true;
    }
    /**
     * Drops the table.
     * @return true if the table existed and was successfully dropped, false if the table didn't exist.
     */
    private boolean dropTable(){
        try{
            statement.executeUpdate("DROP TABLE "+tableName);
        }catch(java.sql.SQLException sqle){
            if(sqle.getSQLState().equalsIgnoreCase("42Y55"))//The table didn't exist.
                return false;
            errorMessage(sqle);
        }
        return true;
    }
    private int addImpl(String particular,int amount,int mode,long date)throws java.sql.SQLException{
            int affectedRows=-1;
            affectedRows=statement.executeUpdate("INSERT INTO "+tableName+" "
                + "VALUES ('"+particular+"',"+amount+","+mode+","+date+")");
            setChanged();
            notifyObservers(new Operation(CashBookConstants.OP_ADD,new ATransaction(particular,amount,mode,new java.util.Date(date))));
            return affectedRows;
    }
    /**
     * Add a transaction to the date in this calendar
     * @param particular
     * @param mode
     * @param amount
     * @param date 
     * @return the number of rows that were added, or -1 if an error occurred 
     */
    public int add(String particular,int amount,int mode,long date){
        int affectedRows=-1;
        try{
            System.out.println("add_"+date);
            affectedRows=addImpl(particular,amount,mode,date);
        }catch(java.sql.SQLException sqle){
            if(sqle.getSQLState().equalsIgnoreCase("42X05")){
                try{
                    createTable();
                    affectedRows=addImpl(particular,amount,mode,date);
                }catch(java.sql.SQLException sqle2){//Percolate down to errorMessage() below
                    errorMessage("Error occured while adding transaction.",sqle2);
                }
            }
            else errorMessage("Error occured while adding transaction.",sqle);
        }
        return affectedRows;
    }
    /**
     * Delete the rows with the given dates
     * @param date the primary key value of the row to be deleted
     */
    public void delete(long[]dates){
        java.util.ArrayList<Long>deletedDates=new java.util.ArrayList<Long>(dates.length);
        try{
            for(int i=0;i<dates.length;i++)
                if(statement.executeUpdate("DELETE FROM "+tableName+" WHERE date="+dates[i])==1)
                    deletedDates.add(dates[i]);
            if(getTransactions().getSize()==0){//no more rows
                dropTable();//delete the table
            }
        }catch(java.sql.SQLException sqle){
            errorMessage("Error occured while deleting transaction",sqle);
        }
        setChanged();
        notifyObservers(new Operation(CashBookConstants.OP_DELETE,deletedDates));
    }
    /**
     * The row in the table with the date specified in the newTransaction object is used
     * as primary key to modify the content of that row.
     * @param newTransaction 
     * @return the number of rows affected by this update, or -1 if an error occurred
     */
    public int modify(ATransaction newTransaction){
        int affectedRows=-1;
        try{
            System.out.println("modify_"+newTransaction.getDate().getTime());
            long date=System.currentTimeMillis();
            affectedRows=statement.executeUpdate("UPDATE "+tableName+" SET "
                    +"particular='"+newTransaction.getStory()+"'"
                    +",amount="+newTransaction.getAmount()+""
                    +",mode="+newTransaction.getMode()+""
                    + ",date="+date+" "
                    +"WHERE date="+newTransaction.getDate().getTime());
            newTransaction.modify(newTransaction.getStory(),newTransaction.getAmount(),newTransaction.getMode(),new java.util.Date(date));
            setChanged();
            notifyObservers(new Operation(CashBookConstants.OP_EDIT,newTransaction));
        }catch(java.sql.SQLException sqle){
            //No need to handle non-existence of table since we can only modify a row in a table that already exists.
            errorMessage("Error while modifying transaction",sqle);
        }
        return affectedRows;
    }
    /**
     * . Open to optimization.
     * @return a <code>TransactionRange</code> object containing a range of transactions
     */
    public TransactionRange getTransactions(){
        TransactionRange range=new TransactionRange(20);
        try{
            java.sql.ResultSet rs=statement.executeQuery("SELECT particular,amount,mode,date FROM "+tableName);
            while(rs.next()){
                String particular=rs.getString(1);
                int amount=rs.getInt(2);
                int mode=rs.getInt(3);
                long date=rs.getLong(4);
                System.out.println("get_"+date);
                range.add(new ATransaction(particular,amount,mode,new java.util.Date(date)));
            }
            rs.close();
        }catch(java.sql.SQLException sqle){
            if(sqle.getSQLState().equalsIgnoreCase("42X05")){
                //The table does not exist
                //So that when a non-existent table name is queried for how many rows it has, an empty TransactionRange
                //object is returned gracefully.
                System.out.println("empty table "+tableName+" was queried");
                sqle.printStackTrace(System.out);
            }else
            errorMessage("Error occured while retrieving transactions",sqle);
        }
        return range;
    }
    /***
     * Sets this calendar and Date to be the new calendar to be used by the application.
     * @param calendar the new calendar to be used by the application.
     */
    public void setDate(java.util.Calendar calendar){
//        this.calendarInUse=(java.util.Calendar)calendar.clone();
        this.calendarInUse.set(java.util.Calendar.YEAR,calendar.get(java.util.Calendar.YEAR));
        this.calendarInUse.set(java.util.Calendar.MONTH,calendar.get(java.util.Calendar.MONTH));
        this.calendarInUse.set(java.util.Calendar.DATE,calendar.get(java.util.Calendar.DATE));
        
        this.tableName=CashBookConstants.MONTHS[calendarInUse.get(java.util.Calendar.MONTH)]+"_"+
                calendarInUse.get(java.util.Calendar.DATE)+"_"+
                calendarInUse.get(java.util.Calendar.YEAR);
    }
    /**
     * @return the current calendar in use by the application.
     */
    public java.util.Calendar getCalendar(){
        return calendarInUse;
    }
    
    /**
     * Shows user the error message and writes it to log.txt
     * @param message
     * @param e 
     */
    public void errorMessage(String message,java.lang.Exception e){
        javax.swing.JOptionPane.showMessageDialog(null,message+"\n"+e.getMessage(),"ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
        errorMessage(e);
//        if(e instanceof java.sql.SQLException)
//            try{connection.rollback();}catch(Exception ef){}
    }
    public void errorMessage(java.lang.Exception e){
        if(e!=null)updateLog(e);
    }
    private static void updateLog(java.lang.Exception e){
        e.printStackTrace();
//        intoLog.println("-----------------------"+java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(new java.util.Date())+"-----------------------");
//        e.printStackTrace(intoLog);
    }    
    /**
     * @return the number of transactions that are currently in this table
     */
    public int getNumberOfTransactions(){
        return getTransactions().getSize();
    }
    public java.util.Calendar getStartCalendar(){
        java.util.Calendar calendar=null;
        try{
            java.sql.ResultSet rs=statement.executeQuery("SELECT value FROM META_TABLE where name='startCalendar'");
            rs.next();
            calendar=new java.util.GregorianCalendar();
            calendar.setTimeInMillis(rs.getLong(1));
        }catch(java.sql.SQLException sqle){
            errorMessage("Error while retrieving startCalendar",sqle);
        }
        return calendar;
    }
    public void shutdownModel(java.awt.Component parent){
        if(statement!=null)
            try{statement.close();}catch(java.sql.SQLException sqle){}
        if(connection!=null)
            try{connection.close();}catch(java.sql.SQLException sqle){}
        System.out.println("shutting down model");
    }
}
