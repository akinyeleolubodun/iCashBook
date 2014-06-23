package com.thinkfirst.icashbook.model;

import com.thinkfirst.icashbook.CashBookConstants;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Date implements java.io.Serializable{
    final private int year;//the year that creted this date
    final private int month;//the month that created this date
    private int dayOfWeek;
    private java.util.ArrayList<Transaction> todaysTransaction=new java.util.ArrayList<Transaction>();
    private long netAmount;//net amount for the day
    private int nextSerialNumber=1;//serial of next item to be inserted
    public Date(int year,int month,int dayOfWeek){
        this.year=year;
        this.month=month;
        this.dayOfWeek=dayOfWeek;
    }
    public int getYear(){
        return year;
    }
    public int getMonth(){
        return month;
    }
    public int getValue(){
        return dayOfWeek;
    }
    /**
     * @return the number of transactions that are currently in this Date
     */
    public int getNumberOfTransactions(){
        return nextSerialNumber-1;
    }
    public Transaction addTransaction(String story,int amount,int mode,java.util.Date date){
        Transaction t;
        todaysTransaction.add(t=new Transaction(nextSerialNumber++,story,amount,mode,date));
        netAmount+=mode==CashBookConstants.CREDIT?amount:-amount;
        return t;
    }
    public Transaction editTransaction(int serialNumber,String story,int amount,int mode,java.util.Date date){
        Transaction t=todaysTransaction.get(serialNumber-1);
        netAmount-=t.mode==CashBookConstants.CREDIT?t.amount:-t.amount;//first undo this transaction
        todaysTransaction.get(serialNumber-1).modify(story,amount,mode,date);
        netAmount+=mode==CashBookConstants.CREDIT?amount:-amount;//then do this one
        return t;
    }
    /**
     * Deletes the Transactions specified with these indices
     * @param serialNumbers the indices of transactions to be removed from this list
     */
    public void deleteTransactions(int[] serialNumbers){
        if(serialNumbers.length==0)
            return;//contains nothing so return
        java.util.Arrays.sort(serialNumbers);
        int currPointerIndex=0;
        int pointer=serialNumbers[currPointerIndex]-1;
        int size=todaysTransaction.size();

        for(int i=0;i<size;i++)//first push the valid transactions to the left in O(N) time
            if(i==pointer){
                netAmount-=todaysTransaction.get(i).mode==CashBookConstants.CREDIT
                        ?todaysTransaction.get(i).amount:-todaysTransaction.get(i).amount;
                if(++currPointerIndex!=serialNumbers.length)//sure currPointerIndex would never be greater than indices.length in this loop. I love such smartness :p
                    pointer=serialNumbers[currPointerIndex]-1;
                nextSerialNumber--;//next serial number reduces by 1
            }else{
                todaysTransaction.set(i-currPointerIndex,todaysTransaction.get(i));//.setIndex(i-currPointerIndex+1);
                todaysTransaction.get(i-currPointerIndex).setIndex(i-currPointerIndex+1);
            }

        //delete the remaining transactions in O(1) time
        for(int i=0;i<serialNumbers.length;i++)
            todaysTransaction.remove(todaysTransaction.size()-1);//remove the tail
    }
    public java.util.ArrayList<Transaction> getTransactions(){
        return todaysTransaction;
    }
    public long getNetAmount(){
        return netAmount;
    }
    public String toString(){
        return ""+dayOfWeek;

    }

    /**
     * A transaction that was done today
     */
    public class Transaction implements java.io.Serializable{
        private String story;
        private int amount;
        private int mode;//transaction mode
        private int index;
        private java.util.Date date;//The time this transaction was created
        private Transaction(int index,String story,int amount,int mode,java.util.Date date){//this is the credit transaction
            this.story=story;//details of the transaction
            this.amount=amount;//amount inputed
            this.mode=mode;//credit or debit
            this.index=index;
            this.date=date;//current time..
        }
        private void modify(String story,int amount,int mode,java.util.Date date){
            this.story=story;
            this.amount=amount;
            this.mode=mode;
            this.date=date;
        }
        private void setIndex(int index){
            this.index=index;
        }
        public String getStory(){
            return story;
        }
        public int getAmount(){
            return amount;
        }
        public int getMode(){
            return mode;
        }
        public java.util.Date getDate(){
            return date;
        }
        public int getSerialNumber(){
            return index;
        }
    }
}
