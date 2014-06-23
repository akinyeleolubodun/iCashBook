package com.thinkfirst.icashbook.component;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.CashBookConstants;
import com.thinkfirst.icashbook.window.Trans;
import com.thinkfirst.icashbook.component.OnButton;
import com.thinkfirst.icashbook.view.ICashBook;
import com.thinkfirst.icashbook.dto.ATransaction;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Transaction extends OnButton{
    private static final java.text.DateFormat timeFormatter=java.text.DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM);
    //280-3*3=271
    private static final int MAX_TEXT_WIDTH=395/*max total width*/-3/*width of a dot*/*3/*3 dots (elipsis)*/;
    private java.awt.FontMetrics fontMetrics;
    private int maxAdvance;
    /**
     * 
     * @return true if the check box is checked. false otherwise.
     */
    public boolean isChecked(){
        return isChecked==1;
    }
    private int isChecked=-1;
    private OnButton checkBox=new OnButton("checkOn.gif","checkOff.gif"){//Change zone
        private java.awt.Image checkBoxImage=App.getImage("check.gif");
        public void doAction(){
            isChecked=-isChecked;
            selectionCount+=isChecked;
        }
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            if(isChecked==1){
                java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
                g2d.drawImage(checkBoxImage,3,4,null);
            }
        }
        public void mouseEntered(java.awt.event.MouseEvent e){
            Transaction.this.setState(ENTERED);
            super.mouseEntered(e);
        }
        public void mouseExited(java.awt.event.MouseEvent e){
            super.mouseExited(e);
        }
        public void mouseClicked(java.awt.event.MouseEvent e){
            doAction();
            repaint();
            requestFocusInWindow();
        }
        public void keyReleased(java.awt.event.KeyEvent e){
            if(e.getKeyCode()==java.awt.event.KeyEvent.VK_SPACE){
                doAction();
                repaint();
            }
        }
        public void focusGained(java.awt.event.FocusEvent e){
            super.focusGained(e);
            Transaction.this.setState(ENTERED);
        }
        public void focusLost(java.awt.event.FocusEvent e){
            super.focusLost(e);
            Transaction.this.setState(EXITED);
        }
    };
    private OnButton editMenu=new OnButton("edit_entered.gif","edit_exited.gif"){
        {setToolTipText("Edit this entry");}
        public void doAction(){
            if(!App.getLogin().isAdminLogin()){
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }
            
            //set all fields to their values
            
            Trans trans=App.getTrans();
            trans.setTransaction(Transaction.this);//It's important that this comes before the trans object is set visible
            
            trans.setParticular(aTransaction.getStory( ));
            trans.setMode(aTransaction.getMode( ));
            trans.setAmount(aTransaction.getAmount( ));
            
            //Pop up the edit dialog
            trans.setVisible(true);
        }
        public void mouseEntered(java.awt.event.MouseEvent e){
            Transaction.this.setState(ENTERED);
            super.mouseEntered(e);
        }
        public void focusGained(java.awt.event.FocusEvent e){
            super.focusGained(e);
            Transaction.this.setState(ENTERED);
        }
        public void focusLost(java.awt.event.FocusEvent e){
            super.focusLost(e);
            Transaction.this.setState(EXITED);
        }
    };
    public void mouseExited(java.awt.event.MouseEvent e){
        super.mouseExited(e);
    }
    /**
     * Method to convert a string to its short form.
     * If the string is too long to fit in the maximum width,
     * it is expressed in ellipsis form.
     * @return the string in its short form.
     */
    private String setShortParticular(String particular){
        byte[]bytes=particular.getBytes();        
        maxAdvance=fontMetrics.getMaxAdvance();
        
        int minNumOfChars=MAX_TEXT_WIDTH/maxAdvance;
        if(bytes.length<=minNumOfChars)
            return particular;//Bound to contain this since this is for MAXIMUM
        int currentAdvance=fontMetrics.bytesWidth(bytes,0,minNumOfChars);
        int index=minNumOfChars;//index of next character to be checked.
        boolean isWithin=true;//true if 'particular' can be used without ellipsis
        while(index<bytes.length){
            isWithin=false;
            int charWidth=fontMetrics.charWidth(bytes[index]);
            if(currentAdvance+charWidth>=MAX_TEXT_WIDTH)
                break;
            isWithin=true;
            currentAdvance+=charWidth;
            index++;
        }
        return new String(bytes,0,index)+(isWithin?"":"...");
    }
    private ATransaction aTransaction;
    
    private static int selectionCount;
    
    private String shortParticular;
    public Transaction(ATransaction aTransaction){
        super("transactionRow_entered.gif","transactionRow_exited.gif");
        this.removeFocusListener(this);
        this.aTransaction=aTransaction;
        setToolTipText(java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(aTransaction.getDate()));
        //Calculate the width used by this particular
        formattedDate=timeFormatter.format(aTransaction.getDate());
        checkBox.setSize(15,15);
        checkBox.setLocation(10,6);
        add(checkBox);

        editMenu.setSize(16,11);
        editMenu.setLocation(912,10);
        add(editMenu);
    }
    public void removeFromParent(){
        getParent().remove(this);
        if(isChecked())
            selectionCount--;
    }
    public static int getSelectionCount(){
        return selectionCount;
    }
    /**
     * Reset the count of checked items to zero
     */
    public static void resetSelectionCount(){
        selectionCount=0;
    }
    
    
    private boolean firstTime=true;
    private java.awt.Font amountFont=CashBookConstants.NUMERIC_FONT.deriveFont(15f),
            otherFont=CashBookConstants.OTHER_FONT.deriveFont(12f);
    private int amountWidth, serialWidth;
    private java.awt.FontMetrics amountMetrics;
    private String formattedDate;
    private String comma$Amount;
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
        g2d.drawImage(currentImage,0,0,null);
        
        if(firstTime){
            fontMetrics=g2d.getFontMetrics(otherFont);
            shortParticular=setShortParticular(aTransaction.getStory( ));
            comma$Amount=ICashBook.encodeAmount(""+aTransaction.getAmount( ));
            amountMetrics=g2d.getFontMetrics(amountFont);
            amountWidth=amountMetrics.stringWidth(comma$Amount);//669
            serialWidth=amountMetrics.stringWidth(""+aTransaction.getSerialNo());
            firstTime=false;
        }
        g2d.setFont(otherFont);
        g2d.drawString(shortParticular,100,19);
        g2d.drawString(aTransaction.getMode( )==CashBookConstants.CREDIT?"Cr.":"Dr.",710,19);
        g2d.drawString(formattedDate,795,19);
        
        g2d.setFont(amountFont);
        g2d.drawString(""+aTransaction.getSerialNo(),40+(49-serialWidth)/2,19);
        if(aTransaction.getMode( )==CashBookConstants.DEBIT){
            if(g2d.getColor()!=java.awt.Color.RED)
                g2d.setColor(java.awt.Color.RED);
        }else
            if(g2d.getColor()!=java.awt.Color.BLACK)
                g2d.setColor(java.awt.Color.BLACK);
        g2d.drawString(comma$Amount,651-amountWidth,19);
    }
    public void doAction(){
        //do nothing!
    }
    public boolean isFocusable(){
        return false;
    }
    public int getAmount(){
        return aTransaction.getAmount( );
    }
    public String getParticular(){
        return aTransaction.getStory( );
    }
    public int getSerialNo(){
        return aTransaction.getSerialNo();
    }
    public int getTransactionType(){
        return aTransaction.getMode( );
    }
    public java.util.Date getDate(){
        return aTransaction.getDate();
    }
    public void setSerialNo(int serialNo){
        aTransaction.setSerialNo(serialNo);
        serialWidth=amountMetrics.stringWidth(""+serialNo);
    }
    public void setFields(String particular,int transactionType,int amount,java.util.Date date){
        this.aTransaction.modify(particular,amount,transactionType,date);
        formattedDate=timeFormatter.format(date);
        setToolTipText(java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(date));
        shortParticular=setShortParticular(particular);
        comma$Amount=ICashBook.encodeAmount(""+amount);
        amountWidth=amountMetrics.stringWidth(comma$Amount);//669
        serialWidth=amountMetrics.stringWidth(""+aTransaction.getSerialNo());
//        repaint();
    }
    public String toString(){
        return ""+getDate().getTime();
    }
}
