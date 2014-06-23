package com.thinkfirst.icashbook.model;
/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Operation{
    private int command;
    private Object o;
    private long[]dates;
    public Operation(int command,Object o){
        this.command=command;
        this.o=o;
    }
    public int getCommand(){
        return command;
    }
    public Object getObject(){
        return o;
    }
}
