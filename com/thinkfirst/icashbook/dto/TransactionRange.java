package com.thinkfirst.icashbook.dto;

import com.thinkfirst.icashbook.dto.ATransaction;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.1
 * @since 1.0
 */
//-------------USEFUL CLASS----------DON'T DELETE
public class TransactionRange implements java.lang.Iterable<ATransaction>{
    private java.util.ArrayList<ATransaction>list;
    /**
     * Create a <code>TransactionRange</code> object with the specified initial capacity
     * @param size the initial capacity
     */
    public TransactionRange(int size){
        list=new java.util.ArrayList<ATransaction>(size);
    }
    public void add(ATransaction transaction){
        list.add(transaction);
    }
//    //returns the list of Transaction iterables.
//    public java.util.ArrayList<ATransaction>getSegments(){
//        return list;
//    }
    public int getSize(){
        return list.size();
    }
    public java.util.Iterator<ATransaction>iterator(){
        return list.iterator();
    }
}
