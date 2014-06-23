package com.thinkfirst.icashbook.persistence;

import com.thinkfirst.icashbook.CashBookConstants;
import com.thinkfirst.icashbook.model.Operation;
import com.thinkfirst.icashbook.YearManager;
import com.thinkfirst.icashbook.model.Date;
import com.thinkfirst.icashbook.model.Date;

/**
 * Class that manages the data in the application.
 * It also manages the current date being viewed by the class.
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Model extends java.util.Observable{
    private java.util.Calendar calendarInUse;//the calendar that is currently in use by the application.
    private Date dateInUse;
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
    
    private void createReadMe(){
        java.io.PrintStream ps=null;
        try{
            ps=new java.io.PrintStream(new java.io.File(dir,"Read Me.txt"));
            ps.println("--------------------WARNING!--------------------");
            ps.println("\"db.ctft\" and its counterpart, \"db_backup.ctft\" are private files used by icashbook application.");
            ps.println("Please do not open these files as it may lead to corruption of its contents.");
            ps.println("More importantly, DO NOT MODIFY the contents of this file.");
            ps.println("Failure to adhere to these instructions may lead to loss of all existing data.");
            ps.println();
            ps.println("Thanks for using icashbook.");
            ps.close();
        }catch(java.io.FileNotFoundException e){}
        catch(java.io.IOException e){}
    }
    //Safely exit
    private void exit(int errorCode){
//        if(fileLock!=null)try{fileLock.release();}catch(java.io.IOException e){}
        if(raf!=null)try{raf.close();}catch(java.io.IOException e){if(intoLog!=null)updateLog(e);}//this closes the associated channel, which also releases the fileLock
        if(oos!=null)try{oos.close();}catch(java.io.IOException e){if(intoLog!=null)updateLog(e);}
        if(ois!=null)try{ois.close();}catch(java.io.IOException e){if(intoLog!=null)updateLog(e);}
        if(intoLog!=null)intoLog.close();
        System.exit(errorCode);
    }
    /**
     * Intended to be called just before the application window is closed.
     * This method releases locks and close streams.
     * Then it copies contents of the main file to the backup.
     * If there was any error along the line, the user is notified.
     * Finally, the application is exited by calling System.exit.
     */
    public void saveToBackupAndExit(java.awt.Component parent){
        //Close streams
//        if(fileLock!=null)try{fileLock.release();}catch(java.io.IOException e){}
        if(raf!=null)try{raf.close();}catch(java.io.IOException e){updateLog(e);}
        if(oos!=null)try{oos.close();}catch(java.io.IOException e){updateLog(e);}
        if(ois!=null)try{ois.close();}catch(java.io.IOException e){updateLog(e);}
        try{
        if(!atLeast1saveOperation)
            //The application was opened but no save operation was performed.
            //The db file has been corrupted because some previous yearManager information has been cut off for
            //a new one to be saved but nothing was saved.
            //Hence, copy from db_backup back to db.
            copy(new java.io.FileOutputStream(db),new java.io.FileInputStream(db_backup),true);
        else if(!copy(new java.io.FileOutputStream(db_backup),new java.io.FileInputStream(db),true))
                javax.swing.JOptionPane.showMessageDialog(parent,"Error occurred while saving transactions.","ERROR",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
        }catch(java.io.IOException e){updateLog(e);}
        exit(0);//finally exit
    }
    
    private void lockFile(java.io.File file){
        try{
            raf=new java.io.RandomAccessFile(file,"rw");
        }catch(java.io.FileNotFoundException e){//assert(this exception should never be thrown)
            errorMessage("Error occurred during initialization.",e);
            exit(1);
        }
        java.nio.channels.FileChannel fileChannel=raf.getChannel();
        try{
            fileChannel.tryLock();//try obtaining a lock on this file.
            //no need for an handle to the fileLock object since closing the raf automatically releases the lock on the fileLock object
        }catch(java.io.IOException e){
            //No need to log this error
            errorMessage("Program cannot start.\nAnother instance of this program may be running.",null);
            exit(1);
        }
    }
    
    public java.util.Calendar getStartCalendar(){
        return yearManager.getStartCalendar();
    }
    /**
     * @param out The output stream.
     * @param in The input stream.
     * @param closeInputStream if true, the input stream is closed after the copy operation.
     * The output stream will always be closed after the copy operation.
     * @return true if no error occurred while trying to copy contents of backupFile to mainFile.
     */
    private boolean copy(java.io.OutputStream out,java.io.InputStream in,boolean closeInputStream){
        byte[]buf=new byte[1<<10];
        try{
            int bytesRead=in.read(buf);
            while(bytesRead!=-1){
                out.write(buf,0,bytesRead);
                bytesRead=in.read(buf);
            }
        }catch(java.io.IOException e){updateLog(e);return false;}
        try{
            out.close();
            if(closeInputStream)in.close();
        }catch(java.io.IOException e){updateLog(e);return false;}
        return true;
    }
    //This method is called to delete this directory and its contents so that
    //when the application is launched again, it will be seen as the first launch.
    private void deleteDirectoryAndContents(java.io.File directory){
        for(java.io.File file:directory.listFiles())
            file.delete();
        directory.delete();
    }
    private void readData(){
        if(!dir.exists()){
            //create directory
            if(!dir.mkdir()){
                //directory couldn't be created
                errorMessage("Error occurred while creating application files.",null);
                exit(1);
            }
            //create files
            try{
                if(!db.createNewFile()||!db_backup.createNewFile()){//files couldn't be created
                    errorMessage("Error occurred while creating application files.",null);
                    deleteDirectoryAndContents(dir);
                    exit(1);
                }
            }catch(java.io.IOException e){
                    errorMessage("Error occurred while creating application files.",null);//this is made null because the error log file should have been created yet
                    deleteDirectoryAndContents(dir);
                    exit(1);
            }
            //Create a new instance of year manager to serve as the "empty string" that I likened this scenario with :)
            YearManager ym=YearManager.getInstance();
            java.io.ObjectOutputStream oos=null;//------------------------
            try{//create output stream to db and write the "empty YearManager" to it.
            //try{fileLock.release();}catch(java.io.IOException e){System.err.println("Exception occured ::: "+e);}
                oos=new java.io.ObjectOutputStream(new java.io.FileOutputStream(db_backup));
                oos.writeObject(ym);
            }catch(java.io.IOException e){
                errorMessage("Error occurred while initializing store.",e);
                if(oos!=null)try{oos.close();}catch(java.io.IOException f){/*Do nothing*/}//close this outputstream
                deleteDirectoryAndContents(dir);
                exit(1);
            }finally{
                //This exception is bound to be thrown if the code in catch{} was executed
                //because the stream would have been closed there, hence it will throw an exception here.
                //And without ensuring that the stream is closed, further invoke of deleteDirectoryAndContents()
                //will not delete the db_backup file because the stream is still busy with it.
                if(oos!=null)try{oos.close();}catch(java.io.IOException e){/*Yes, do nothing*/}
            }
            
            
            //Let's copy here
            try{
                if(!copy(new java.io.FileOutputStream(db),new java.io.FileInputStream(db_backup),true)){//error copying from db_backup to db
                    errorMessage("Error occurred while initializing store.",null);
                    deleteDirectoryAndContents(dir);
                    exit(1);
                }
            }catch(java.io.IOException e){//Exception thrown if the file input stream or file output stream couldn't be created
            }
            //Wow! Sweet! All went well till this point! :)
            createReadMe();//create the READ_ME file
        }
        
        try{
            intoLog=new java.io.PrintWriter(new java.io.BufferedOutputStream(new java.io.FileOutputStream(new java.io.File(dir,logFile),true)));
        }catch(java.io.IOException e){
            javax.swing.JOptionPane.showMessageDialog(null,"Error occurred during initialization.\n"
                    + "Please press OK to exit the application.","ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
            exit(1);
        }
        
        
        //Lock the file first
        lockFile(db);//locks file and sets raf
        //read the inputstream
        try{
            ois=new java.io.ObjectInputStream(fis=new java.io.FileInputStream(raf.getFD()));
        }catch(java.io.IOException e){
            updateLog(e);
            if(javax.swing.JOptionPane.showConfirmDialog(null,"Error occurred while reading data from store.\n"
                    +"Do you want the application to recover from its backup store?","CONFIRMATION",
                    javax.swing.JOptionPane.YES_NO_OPTION,javax.swing.JOptionPane.QUESTION_MESSAGE)!=javax.swing.JOptionPane.YES_OPTION)
                exit(1);//if no, exit the program
            //User has chosen to recover from backup file.
            recoverFromBackup();
        }
        
        //Now the db and its backup should exist and have contents, all things being equal.
        //Now work as normal.
        try{
            yearManager=(YearManager)ois.readObject();
            //ok, successfully read. db is fine. transfer it's content to db_backup
            raf.seek(0);//reset the file for this copy operation
            try{copy(new java.io.FileOutputStream(db_backup),fis,false);}catch(java.io.IOException e){updateLog(e);}
        }catch(java.lang.Exception e){//if there's error reading the file
            errorMessage("Error occurred while reading data from store.\n"
                    + "Please press OK to exit the application.",e);
            exit(1);
        }
        
        //Now, initialize the outputstream
        try{
            raf.setLength(0);//reset the file for new storage
            oos=new java.io.ObjectOutputStream(new java.io.FileOutputStream(raf.getFD()));//Writes control information to the file.
            this.filePointer=(int)raf.getFilePointer();//set the filePointer position.            
        }catch(java.io.IOException e){
            errorMessage("Error occurred during initialization.",e);
            exit(1);
        }
        
        dateInUse=yearManager.getDate(calendarInUse);
    }
    /**
     * This method creates another input stream from the backup file.
     * and sets up all identifiers.
     */
    private void recoverFromBackup(){
        String errorMessage="Error occurred while retrieving data from backup store.\n"
                +"Please press OK to exit the application.";
        //close previously open streams
        try{
            if(raf!=null)raf.close();
            if(oos!=null)oos.close();
            if(ois!=null)ois.close();
        }catch(java.io.IOException f){
            errorMessage(errorMessage,f);
            exit(1);
        }
        //db.ctft is corrupt
        //delete the db.ctft corrupt file.
        if(!db.delete()){
            errorMessage(errorMessage,null);
            exit(1);
        }
        //create new empty db.ctft
        try{
            if(!db.createNewFile()){
                errorMessage(errorMessage,null);
                exit(1);
            }
        }catch(java.io.IOException f){
            errorMessage(errorMessage,f);
            exit(1);
        }
        //copy db_backup.ctft to db.ctft
        try{
            if(!copy(new java.io.FileOutputStream(db),new java.io.FileInputStream(db_backup),true)){
                errorMessage(errorMessage,null);
                exit(1);
            }
        }catch(java.io.IOException e){updateLog(e);}
        //all went fine

        //lock the file
        lockFile(db);//initializes raf            
        //initialize the object input stream
        try{
            ois=new java.io.ObjectInputStream(fis=new java.io.FileInputStream(raf.getFD()));
        }catch(java.io.IOException f){
            //complain
            errorMessage(errorMessage,f);
            exit(1);
        }
    }
    /**
     * Saves the current state of the program data.
     * @param parent The component that serves as the parent of the dialog box used if there are any errors along the line.
     */
    public void saveData(java.awt.Component parent){
        try{
            raf.setLength(this.filePointer);//chop off file contents first
            oos.reset();//The edge breaking command
            oos.writeObject(yearManager);
        }catch(java.lang.Exception e){
            updateLog(e);
            javax.swing.JOptionPane.showMessageDialog(parent,"Error occurred while saving transactions.\nPress OK to exit the application.","ERROR",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            exit(1);
        }
        atLeast1saveOperation=true;
    }
    public static void errorMessage(String message,java.lang.Exception e){
        javax.swing.JOptionPane.showMessageDialog(null,message,"ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
        if(e!=null)updateLog(e);
    }
    private static void updateLog(java.lang.Exception e){
        intoLog.println("-----------------------"+java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(new java.util.Date())+"-----------------------");
        e.printStackTrace(intoLog);
    }
    public Model(){
        this.calendarInUse=new java.util.GregorianCalendar();
        readData();
    }
    /**
     * Add a transaction to the date in this calendar
     * @param particular
     * @param mode
     * @param amount
     * @param date 
     */
    public void add(String particular,int amount,int mode,java.util.Date date){
        Date.Transaction t=dateInUse.addTransaction(particular,amount,mode,date);
        setChanged();
        notifyObservers(new Operation(CashBookConstants.OP_ADD,t));
    }
    /**
     * Edit the transaction with this index to these values. The changes are made to the date in the current calendar
     * @param serialNumber
     * @param particular
     * @param mode
     * @param amount
     * @param date 
     */
    public void edit(int serialNumber,String particular,int mode,int amount,java.util.Date date){
        Date.Transaction t=dateInUse.editTransaction(serialNumber,particular,amount,mode,date);
        setChanged();
        notifyObservers(new Operation(CashBookConstants.OP_EDIT,t));
    }
    /**
     * Deletes the transactions with the given indices.
     * @param serialNumbers 
     */
    public void deleteIndices(int[] serialNumbers){
        dateInUse.deleteTransactions(serialNumbers);
        setChanged();
        notifyObservers(new Operation(CashBookConstants.OP_DELETE,serialNumbers));
    }
    /***
     * Sets this calendar and Date to be the new calendar to be used by the application.
     * @param calendar the new calendar to be used by the application.
     */
    public void setDate(java.util.Calendar calendar){
        this.calendarInUse=(java.util.Calendar)calendar.clone();
        this.dateInUse=yearManager.getDate(this.calendarInUse);
    }
    /**
     * @return the current calendar in use by the application.
     */
    public java.util.Calendar getCalendar(){
        return calendarInUse;
    }
    /**
     * 
     * @return the Date that is currently set to be used by the application
     */
    public Date getDate(){
        return dateInUse;
    }
    public java.util.ArrayList<Date.Transaction> getTransactions(){
        return dateInUse.getTransactions();
    }
    public long getAmount(){
        return dateInUse.getNetAmount();
    }
    /**
     * @return the number of transactions that are currently in this Date
     */
    public int getNumberOfTransactions(){
        return dateInUse.getNumberOfTransactions();
    }
}
