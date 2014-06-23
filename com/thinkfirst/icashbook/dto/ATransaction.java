/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkfirst.icashbook.dto;
/**
 *
 * @author essiennta
 */
public class ATransaction{
    private String details;
    private int amount;
    private int mode;//transaction mode
    private java.util.Date date;//The time this transaction was created
    private int serialNo;
    private boolean isSaved;//true if this transaction has been saved to the database
    public ATransaction(String details,int amount,int mode,java.util.Date date){//this is the credit transaction
        this.details=details;//details of the transaction
        this.amount=amount;//amount inputed
        this.mode=mode;//credit or debit
        this.date=date;//current time..
    }
    public void modify(String story,int amount,int mode,java.util.Date date){
        this.details=story;
        this.amount=amount;
        this.mode=mode;
        this.date=date;
        isSaved=false;
    }
    public void setSerialNo(int serialNo){
        this.serialNo=serialNo;
    }
    public String getStory(){
        return details;
    }
    public int getAmount(){
        return amount;
    }
    public int getMode(){
        return mode;
    }
    public int getSerialNo(){
        return serialNo;
    }
    public java.util.Date getDate(){
        return date;
    }
    public boolean isSaved(){
        return isSaved;
    }
    public void save(){
        isSaved=true;
    }
}
