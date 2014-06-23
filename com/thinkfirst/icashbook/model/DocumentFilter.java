package com.thinkfirst.icashbook.model;
/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class DocumentFilter extends javax.swing.text.PlainDocument{
    public static final int FILTER_NUM=0;//numbers
    public static final int FILTER_ALL=1;//Allow all characters
    private java.util.regex.Pattern pattern;
    private Must filter;
    public DocumentFilter(int mode){
        super();
        switch(mode){
            case FILTER_NUM://any 1 to 9 digits or 1 followed by 9 digits or 2 followed by 9 zeroes
                filter=new NumberFilter();
                break;
            case FILTER_ALL://Match all
                filter=new AllFilter();
                break;
            default:
                throw new IllegalArgumentException("Unrecognized filter");
        }
    }
    
    public void insertString(int offs,String str,javax.swing.text.AttributeSet attr)
            throws javax.swing.text.BadLocationException{
        if(filter.validate(offs,str))
            super.insertString(offs,str,attr);
        else
            java.awt.Toolkit.getDefaultToolkit().beep();

    }
    public void remove(int offs,int length) throws javax.swing.text.BadLocationException{
        if(filter.remove(offs,length))
            super.remove(offs,length);
        else
            java.awt.Toolkit.getDefaultToolkit().beep();
    }

    interface Must{
        public boolean validate(int offs,String str);
        public boolean remove(int offs,int length);
    }

    private class NumberFilter implements Must{
        private StringBuilder amountBuilder=new StringBuilder(10);//max of 10 characters.
        {
            pattern=java.util.regex.Pattern.compile("(\\d{1,9}|1\\d{9}|20{9})");//edit to 2,000,000,000
        }
        public boolean validate(int offs,String str){
            if(amountBuilder.length()+str.length()>10)
                return false;

            amountBuilder.insert(offs,str);
            if(pattern.matcher(amountBuilder.toString()).matches())
                return true;

            amountBuilder.delete(offs,offs+str.length());
            return false;
        }
        public boolean remove(int offs,int length){
            amountBuilder.delete(offs,offs+length);
            return true;
        }
    }

    private class AllFilter implements Must{
        private int maxChars=1000;
        private int numChars;
        {
            pattern=java.util.regex.Pattern.compile("(.(?<!\t))+");//Match all characters except newlines and tabs. (Note that both \t and \\t would work.
        }
        public boolean validate(int offs,String str){
            //if pattern == null, match all.
            if(!pattern.matcher(str).matches()||numChars+str.length()>maxChars)//doesn't match the pattern or too long
                return false;
            numChars+=str.length();
            return true;
        }
        public boolean remove(int offs,int length){
            return true;
        }
    }
}
