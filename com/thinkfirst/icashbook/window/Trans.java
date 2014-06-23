package com.thinkfirst.icashbook.window;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.CashBookConstants;
import com.thinkfirst.icashbook.component.Transaction;
import com.thinkfirst.icashbook.component.OnButton;
import com.thinkfirst.icashbook.model.DocumentFilter;
import com.thinkfirst.icashbook.dto.ATransaction;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Trans extends javax.swing.JDialog{
    private TransactionView view;
    protected SpinHandler spinHandler;
    private String particular;
    private int transactionType;
    private int amount;
    private Transaction source;//The Transaction currently being edited in this dialog
    private boolean currentlyUsingAddButton=true;
    private java.awt.Font labelFont=App.getFont("bgothl.ttf").deriveFont(java.awt.Font.BOLD,15.0f);
    public void setTransaction(Transaction source){
        this.source=source;
        currentlyUsingAddButton=false;
    }
    private OnButton addButton=new OnButton("_addOn.gif","_addOff.gif"){
        {setToolTipText("Add entry");}
        public boolean isFocusable(){
            return false;
        }
        public void doAction(){
            particular=descriptionField.getText();
            if(particular==null||particular.isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(Trans.this,"Particular field cannot be empty."
                        +"\nPlease enter some text.","Alert",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                descriptionField.requestFocusInWindow();
                return;//return back to the add transaction input
            }
            String amountText=amountField.getText();
            if(amountText==null||amountText.isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(Trans.this,"Amount field cannot be empty."
                        +"\nPlease enter some amount.","Alert",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                amountField.requestFocusInWindow();
                return;//return back to the add transaction input
            }
            amount=Integer.parseInt(amountText);
            transactionType=spinHandler.getTransactionMode();

            app.getModel().add(particular,amount,transactionType,System.currentTimeMillis());//date at which this transaction is entered

            descriptionField.setText(null);
            amountField.setText(null);
            descriptionField.requestFocusInWindow();
        }
    };
    
    private OnButton updateButton=new OnButton("_editOn.gif","_editOff.gif"){
        {setToolTipText("Edit entry");}
        public boolean isFocusable(){
            return false;
        }
        public void doAction(){
            particular=descriptionField.getText();
            if(particular==null||particular.isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(Trans.this,"Particular field cannot be empty."
                        +"\nPlease enter some text.","Alert",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                descriptionField.requestFocusInWindow();
                return;//return back to the add transaction input
            }
            String amountText=amountField.getText();
            if(amountText==null||amountText.isEmpty()){
                javax.swing.JOptionPane.showMessageDialog(Trans.this,"Amount field cannot be empty."
                        +"\nPlease enter some amount.","Alert",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                amountField.requestFocusInWindow();
                return;//return back to the add transaction input
            }
            amount=Integer.parseInt(amountText);
            transactionType=spinHandler.getTransactionMode();

            //update the field
            ATransaction aTransaction=new ATransaction(particular,amount,transactionType,source.getDate());
            aTransaction.setSerialNo(source.getSerialNo());
            app.getModel().modify(aTransaction);
            
            closeButton.doAction();//exit this dialog
        }
    };
    
    private OnButton closeButton=new OnButton("_closeOn.gif","_closeOff.gif"){
        {setToolTipText("Close");}
        public boolean isFocusable(){
            return false;
        }
        public void doAction(){
            if(currentlyUsingAddButton)
                view.remove(addButton);
            else
                view.remove(updateButton);
            currentlyUsingAddButton=true;
            descriptionField.setText(null);
            spinHandler.setTransactionMode(CashBookConstants.CREDIT);
            amountField.setText(null);
            dispose();//free resources
        }
    };
    private javax.swing.JTextField amountField=new javax.swing.JTextField(){
        public void setBorder(javax.swing.border.Border border){}//do nothing
    };
    private javax.swing.JTextField descriptionField=new javax.swing.JTextField(){
        public void setBorder(javax.swing.border.Border border){}//do nothing
    };
    private App app;
    public Trans(App app){
        super(app.getAppWindow(),true);
        this.app=app;
        
        java.awt.Point parentLocation=app.getAppWindow().getLocation();
        java.awt.Insets insets=app.getAppWindow().getInsets();
        setLocation(insets.left+parentLocation.x+54,insets.top+parentLocation.y+161+7);

        String closingKey=getClass().getName()+":WINDOW_CLOSING";
        javax.swing.JRootPane root=getRootPane();
        root.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),
                closingKey);
        root.getActionMap().put(closingKey,new javax.swing.AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                closeButton.doAction();
            }
        });
        
        
        java.awt.Container contentPane=getContentPane();
        contentPane.setLayout(new java.awt.BorderLayout());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        contentPane.setPreferredSize(new java.awt.Dimension(745,51));
        view=new TransactionView();

        addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent e){
                closeButton.doAction();
            }
        });

        contentPane.add(view);
        setResizable(false);
        pack();
    }
    public void setVisible(boolean b){
        if(b){
            if(currentlyUsingAddButton){
                setTitle("Add Transaction");
                view.add(addButton);
            }else{
                setTitle("Edit Transaction");
                view.add(updateButton);
            }
            view.validate();
            view.repaint();
        }
        super.setVisible(b);
    }
    
    private class TransactionView extends javax.swing.JComponent{
        private java.awt.Image addImage;
        private java.awt.Font font=(new java.awt.Font(java.awt.Font.DIALOG,java.awt.Font.PLAIN,18));
        public TransactionView(){
            addImage=App.getImage("input.gif");

            spinHandler=new SpinHandler("upArrowOn.gif","upArrowOff.gif",
                    "downArrowOn.gif","downArrowOff.gif");

            spinHandler.setSize(44,20);
            spinHandler.setLocation(642+8,13+15);

            addButton.setSize(16,16);
            addButton.setLocation(712,11+15);//20,11
            updateButton.setSize(16,13);
            updateButton.setLocation(712,11+15);

            closeButton.setSize(15,15);
            closeButton.setLocation(20,10+15);//712,10
            

            amountField.setSize(145,12);
            amountField.setLocation(463,13+15);
            descriptionField.setFont(App.getFont("tahoma.ttf").deriveFont(11.0f));
            amountField.setBackground(new java.awt.Color(253,251,251));
            amountField.setDocument(new DocumentFilter(DocumentFilter.FILTER_NUM));
            /**
             * Here, I'll be overriding the default behavior of a text field.
             * This is because I don't want the edit menu to be called again after the enter key has been
             * pressed on this field.
             * So here, I'll make the text field respond only when the enter key is released.
             */
            amountField.addKeyListener(new java.awt.event.KeyAdapter(){
                public void keyReleased(java.awt.event.KeyEvent e){
                    if(e.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER){
                        e.consume();//don't know if the textfield will be making use of this but lemme just consume it to be on the safe side
                        if(currentlyUsingAddButton)
                            addButton.doAction();
                        else
                            updateButton.doAction();
                    }
                }
            });

            descriptionField.setSize(376,12);
            descriptionField.setLocation(53,12+15);
            descriptionField.setFont(App.getFont("tahoma.ttf").deriveFont(11.0f));
            descriptionField.setBackground(new java.awt.Color(253,251,251));
            
            descriptionField.addKeyListener(new java.awt.event.KeyAdapter(){
                public void keyPressed(java.awt.event.KeyEvent e){
                    if(e.getKeyCode()==java.awt.event.KeyEvent.VK_TAB){
                        e.consume();
                        descriptionField.transferFocus();
                    }
                }
            });
            
            add(descriptionField);
            add(amountField);
            add(spinHandler);
            add(closeButton);
        }
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
            g2d.setFont(font);
            g2d.drawImage(addImage,0,0,null);
            g2d.setFont(labelFont);
            int a=10;
            g2d.drawString("Description",41,97-a);
            g2d.drawString("Entry",41,236-a);
            g2d.drawString("Amount",41,322-a);
        }
    }
    public void setParticular(String particular){
        descriptionField.setText(particular);
    }
    public void setMode(int mode){
        spinHandler.setTransactionMode(mode);
    }
    public void setAmount(int amount){
        String text=""+amount;
        amountField.setText(text);
        this.amountField.revalidate();
        this.amountField.repaint();
    }

    protected class SpinHandler extends javax.swing.JLabel{
        private java.awt.Image up_entered;
        private java.awt.Image up_exited;
        private java.awt.Image down_entered;
        private java.awt.Image down_exited;
        private boolean isEntered;
        private void doAction(){
            view.repaint();
        }
        private boolean isFocus;
        private int transactionMode=CashBookConstants.CREDIT;
        public int getTransactionMode(){
            return transactionMode;
        }
        public void setTransactionMode(int mode){
            this.transactionMode=mode;
            repaint();
        }
        private SpinHandler(String up_entered,String up_exited,String down_entered,String down_exited){
            this.up_entered=App.getImage(up_entered);
            this.up_exited=App.getImage(up_exited);
            this.down_entered=App.getImage(down_entered);
            this.down_exited=App.getImage(down_exited);
            addFocusListener(new java.awt.event.FocusAdapter(){
                public void focusGained(java.awt.event.FocusEvent e){
                    isFocus=isEntered=true;
                    repaint();
                }
                public void focusLost(java.awt.event.FocusEvent e){
                    isFocus=isEntered=false;
                    repaint();
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter(){
                public void mouseClicked(java.awt.event.MouseEvent e){
                    doAction();
                    transactionMode=~transactionMode;
                    repaint();
                    requestFocusInWindow();
                }
                public void mouseEntered(java.awt.event.MouseEvent e){
                    if(!isFocus){
                        isEntered=true;
                        repaint();
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent e){
                    if(!isFocus){
                        isEntered=false;
                        repaint();
                    }
                }
            });
            addKeyListener(new java.awt.event.KeyAdapter(){
                public void keyReleased(java.awt.event.KeyEvent e){
                    switch(e.getKeyCode()){
                        case java.awt.event.KeyEvent.VK_UP:
                            if(transactionMode==CashBookConstants.DEBIT){
                                doAction();
                                transactionMode=~transactionMode;
                                repaint();
                            }
                            break;
                        case java.awt.event.KeyEvent.VK_DOWN:
                            if(transactionMode==CashBookConstants.CREDIT){
                                doAction();
                                transactionMode=~transactionMode;
                                repaint();
                            }
                            break;
                    }
                }
            });
        }
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
            g2d.drawImage(transactionMode==CashBookConstants.DEBIT?isEntered?up_entered:up_exited:isEntered?down_entered:down_exited,0,0,null);
        }
        public boolean isFocusable(){
            return true;
        }
    }
}