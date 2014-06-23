package com.thinkfirst.icashbook.view;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.CashBookConstants;
import com.thinkfirst.icashbook.component.OnButton;
import com.thinkfirst.icashbook.model.Operation;
import com.thinkfirst.icashbook.component.Transaction;
import com.thinkfirst.icashbook.dto.TransactionRange;
import com.thinkfirst.icashbook.db.DatabaseModel;
import com.thinkfirst.icashbook.window.ChoiceDialog;
import com.thinkfirst.icashbook.window.Calendar;
import com.thinkfirst.icashbook.window.AppWindow;
import com.thinkfirst.icashbook.dto.ATransaction;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class ICashBook extends javax.swing.JComponent implements java.util.Observer{
    private java.awt.Image cashbookImage;
    final java.awt.Color theColor=new java.awt.Color(136,200,200);
    private javax.swing.JLabel save=new javax.swing.JLabel("Save");
    private javax.swing.JLabel convert=new javax.swing.JLabel("Convert");
    private javax.swing.JLabel logout=new javax.swing.JLabel("Log out");
    
    private javax.swing.JLabel amountDisplayPanel=new javax.swing.JLabel(){
        public void setText(String text){
            super.setText(encodeAmount(text));
        }
    };
    private javax.swing.JLabel dateLabel=new javax.swing.JLabel();
    private boolean hasBeenModified;//true whenever user makes any changes and has not saved them.
        
    public static String encodeAmount(String text){        
            String prefix="";
            if(text.startsWith("-")){
                prefix="-";
                text=text.substring(1);
            }
            String newString="";
            int i=text.length();
            while((i-=3)>0)
                newString=","+text.substring(i,i+3)+newString;
            return prefix+text.substring(0,i+3)+newString;
    }
    private void setModified(){
        if(hasBeenModified)
            return;
        hasBeenModified=true;
        save.setForeground(java.awt.Color.PINK);
        save.validate();
    }
    public boolean hasBeenModified(){
        return hasBeenModified;
    }
    private OnButton addTransactionButton=new OnButton("addOn.gif","addOff.gif"){
        {
            setToolTipText("Add a New Transaction");
        }
        public boolean isFocusable(){
            return false;
        }
        public void doAction(){
            if(app.getModel().getNumberOfTransactions()==CashBookConstants.MAX_NUMBER_OF_DAILY_TRANSACTIONS)
                javax.swing.JOptionPane.showMessageDialog(app.getAppWindow(),"Maximum Number of transactions that can be added for the day has been reached!","CANNOT ADD TRANSACTION",javax.swing.JOptionPane.INFORMATION_MESSAGE);
            else
                App.getTrans().setVisible(true);
        }
        public void keyPressed(java.awt.event.KeyEvent e){
        }
        public void keyReleased(java.awt.event.KeyEvent e){
        }
    };
    private OnButton viewTransactionButton;
    private OnButton deleteTransactionButton=new OnButton("deleteOn.gif","deleteOff.gif"){
        {
            setToolTipText("Delete Selected Transactions");
        }
        public boolean isFocusable(){
            return false;
        }
        public void doAction(){            
            if(!App.getLogin().isAdminLogin()){
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }
            if(javax.swing.JOptionPane.showConfirmDialog(app.getAppWindow(),"Are you sure you want to delete "+(Transaction.getSelectionCount()==1
                    ?"this item":"these "+Transaction.getSelectionCount()+" items")+"?",
                    "Confirm Deletion",javax.swing.JOptionPane.YES_NO_OPTION,javax.swing.JOptionPane.QUESTION_MESSAGE)
                    !=javax.swing.JOptionPane.YES_OPTION)
                return;//Leave the items.


            //deletes the selected items from the model
            long[]selectedDates=new long[Transaction.getSelectionCount()];
            int j=0;
            for(Transaction t:transactions)
                if(t.isChecked())
                    selectedDates[j++]=t.getDate().getTime();
            app.getModel().delete(selectedDates);
        }
        public void mouseEntered(java.awt.event.MouseEvent e){
            if(Transaction.getSelectionCount()!=0)
                super.mouseEntered(e);
        }
        public void mouseExited(java.awt.event.MouseEvent e){
            setState(EXITED);
        }
        
        public void mousePressed(java.awt.event.MouseEvent e){
            if(Transaction.getSelectionCount()!=0)
                super.mousePressed(e);
        }
        
        public void mouseClicked(java.awt.event.MouseEvent e){//called after mouseReleased()
            if(Transaction.getSelectionCount()!=0){
                doAction();
                transferFocus();
            }
        }
        public void keyPressed(java.awt.event.KeyEvent e){
        }
        public void keyReleased(java.awt.event.KeyEvent e){
        }
    };
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel panel=new javax.swing.JPanel(null);//Null layout manager
    private App app;
    private Calendar calendar;
    
    /***
     * Cashbook view
     * @param app
     * @param cashbookImage 
     */
    public ICashBook(final App app,String cashbookImage){
        this.cashbookImage=App.getImage(cashbookImage);
        this.app=app;
        calendar=new Calendar(app.getAppWindow(),app.getModel().getStartCalendar());

        setAccelerators();


        dateLabel.setBounds(10,510,150,30);
        dateLabel.setFont(App.getFont("vijayab.ttf").deriveFont(20f));

        viewTransactionButton=new OnButton("viewOn.gif","viewOff.gif"){
            {
                setToolTipText("View Previous Transactions");
            }
            public boolean isFocusable(){
                return false;
            }
            public void doAction(){
                calendar.setVisible(true);
                if(calendar.isPositiveResponse()){//change the view to reflect this
                    updateCalendarView();
                }
            }
            public void keyPressed(java.awt.event.KeyEvent e){
            }
            public void keyReleased(java.awt.event.KeyEvent e){
            }
        };

        addTransactionButton.setSize(28,28);
        addTransactionButton.setLocation(610+128,24+8);
        add(addTransactionButton);

        viewTransactionButton.setSize(29,28);
        viewTransactionButton.setLocation(610+194,24+8);
        add(viewTransactionButton);

        deleteTransactionButton.setSize(25,28);
        deleteTransactionButton.setLocation(610+261,24+8);
        add(deleteTransactionButton);

        scrollPane=new javax.swing.JScrollPane(panel);
        panel.setBackground(new java.awt.Color(231,243,243));
        panel.setPreferredSize(panelDimension);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(960,382);
        scrollPane.setLocation(0,131);

        add(scrollPane);

        amountDisplayPanel.setHorizontalAlignment(javax.swing.JLabel.TRAILING);
        amountDisplayPanel.setFont(CashBookConstants.NUMERIC_FONT.deriveFont(20f));
        amountDisplayPanel.setSize(218,17);
        amountDisplayPanel.setLocation(450,519);
        amountDisplayPanel.setForeground(java.awt.Color.WHITE);
        add(amountDisplayPanel);

        java.awt.Font theFont=App.getFont("BAUHS93.TTF").deriveFont(12f);
        java.awt.Cursor theCursor=new java.awt.Cursor((java.awt.Cursor.HAND_CURSOR));   
        
        convert.setLocation(832,518);
        convert.setFont(theFont);
        convert.setSize(convert.getPreferredSize());
        convert.setForeground(theColor);
        convert.setCursor(theCursor);
        convert.setToolTipText("Save transactions to .tft");
        convert.addMouseListener(new java.awt.event.MouseAdapter(){
            private ChoiceDialog choiceDialog=new ChoiceDialog(app);
            private javax.swing.JFileChooser chooser=new javax.swing.JFileChooser((java.io.File)null);//default directory
            private java.io.File getFile(){
                chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
                chooser.rescanCurrentDirectory();
                chooser.setDialogTitle("Select Folder");
                chooser.setApproveButtonText("OK");
                chooser.showOpenDialog(app.getAppWindow());
                return chooser.getSelectedFile();
            }
            private boolean calendarEqual(java.util.Calendar c1,java.util.Calendar c2){//equal if date,month and year are equal
                return c1.get(java.util.Calendar.YEAR)==c2.get(java.util.Calendar.YEAR)
                        &&c1.get(java.util.Calendar.MONTH)==c2.get(java.util.Calendar.MONTH)
                        &&c1.get(java.util.Calendar.DATE)==c2.get(java.util.Calendar.DATE);
            }
            public void mouseClicked(java.awt.event.MouseEvent e){
                choiceDialog.setVisible(true);

                if(choiceDialog.isPositiveResponse()){
                    java.io.File theFile=getFile();
                    if(theFile==null)
                        return;
                    if(!theFile.isDirectory())//Not a directory
                        javax.swing.JOptionPane.showMessageDialog(app.getAppWindow(),"Invalid directory selection!","ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
                    else{//is a directory
                        //extract the information to file.
                        java.util.Calendar from=choiceDialog.getFrom();
                        java.util.Calendar to=choiceDialog.getTo();
                        String fileName=/*FORMAT: 1-5-2012~2-5-2012.tft*/
                                from.get(java.util.Calendar.DATE)
                                +"-"+from.get(java.util.Calendar.MONTH)
                                +"-"+from.get(java.util.Calendar.YEAR)
                                +"~"
                                +to.get(java.util.Calendar.DATE)
                                +"-"+to.get(java.util.Calendar.MONTH)
                                +"-"+to.get(java.util.Calendar.YEAR)
                                +".tft";

                        java.io.File newFile=new java.io.File(theFile,fileName);
                        if(!newFile.exists()||javax.swing.JOptionPane.showConfirmDialog(app.getAppWindow(),
                                "The file \""+fileName+"\" already exists!\nDo you want to overwrite it?","File Already Exists",
                                javax.swing.JOptionPane.YES_NO_OPTION,javax.swing.JOptionPane.PLAIN_MESSAGE)
                                ==javax.swing.JOptionPane.YES_OPTION){
                            //write the file
                            java.io.ObjectOutputStream oos=null;
                            try{
                                oos=new java.io.ObjectOutputStream(
                                        new java.io.BufferedOutputStream(new java.io.FileOutputStream(newFile)));
                            }catch(java.io.IOException f){
                            }
                            if(oos==null){
                                javax.swing.JOptionPane.showMessageDialog(app.getAppWindow(),"File could not be written.","ERROR",javax.swing.JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            DatabaseModel appModel=app.getModel();
                            java.util.Calendar formerCalendar=appModel.getCalendar();
                            boolean errorAny=false;
                            to.add(java.util.Calendar.DATE,1);//set 'to' to next day as this is used below for boundary test.
                            java.util.ArrayList<TransactionRange>list=new java.util.ArrayList<TransactionRange>();
                            for(java.util.Calendar next=from;!calendarEqual(next,to);next.add(java.util.Calendar.DATE,1)){
                                appModel.setDate(next);
                                list.add(appModel.getTransactions());
                            }
                            try{
                                oos.writeObject(list);
                            }catch(java.io.IOException f){
                                errorAny=true;
                                f.printStackTrace();
                            }
                            appModel.setDate(formerCalendar);//reset the date to what the model was using before
                            //close stream
                            try{
                                oos.close();
                            }catch(java.io.IOException f){
                            }

                            javax.swing.JOptionPane.showMessageDialog(app.getAppWindow(),errorAny?"Some errors occured while saving the transactions."
                                    :"Transactions were successfully saved.","REPORT",errorAny?javax.swing.JOptionPane.ERROR_MESSAGE:javax.swing.JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
            }
        });

        logout.setLocation(900,518);
        logout.setForeground(theColor);
        logout.setFont(theFont);
        logout.setSize(logout.getPreferredSize());
        logout.setCursor(theCursor);
        logout.setToolTipText("Save transactions and log out");
        logout.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                doSave();
                app.getAppWindow().setLoginView(true);
                app.getAppWindow().nextView();
            }
        });
        
        save.setLocation(765+7,518);
        save.setForeground(theColor);
        save.setFont(theFont);
        save.setSize(save.getPreferredSize());
        save.setCursor(theCursor);
        save.setToolTipText("Save recent changes");
        save.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e){
                doSave();
            }
        });
        add(dateLabel);
        add(save);
        add(convert);
        add(logout);
        updateView();
    }
    private void updateCalendarView(){
        app.getModel().setDate(calendar.getCalendar());
        updateView();
    }
    private void doSave(){
        if(!hasBeenModified)
            return;
        app.getModel().saveData(app.getAppWindow());//save the data. This method will not return if data is not successfully saved.
        hasBeenModified=false;//set to false since it hasn't yet been modified after this last save operation.
        save.setForeground(theColor);
        save.validate();
    }
    private void setAccelerators(){
        AppWindow window=app.getAppWindow();
        javax.swing.InputMap inputMap=window.getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,java.awt.event.KeyEvent.CTRL_DOWN_MASK),getClass().getName()+":ADD_COMMAND");
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,java.awt.event.KeyEvent.CTRL_DOWN_MASK),getClass().getName()+":VIEW_COMMAND");
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,java.awt.event.KeyEvent.CTRL_DOWN_MASK),getClass().getName()+":DELETE_COMMAND");
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T,java.awt.event.KeyEvent.CTRL_DOWN_MASK),getClass().getName()+":ABOUT_COMMAND");
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,java.awt.event.KeyEvent.CTRL_DOWN_MASK),getClass().getName()+":SAVE_COMMAND");
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT,java.awt.event.KeyEvent.ALT_DOWN_MASK),getClass().getName()+":LEFT_COMMAND");
        inputMap.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.ALT_DOWN_MASK),getClass().getName()+":RIGHT_COMMAND");

        
        javax.swing.ActionMap actionMap=window.getRootPane().getActionMap();
        actionMap.put(getClass().getName()+":ADD_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(!app.getAppWindow().isLoginView())
                    addTransactionButton.doAction();
            }
        });
        actionMap.put(getClass().getName()+":VIEW_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(!app.getAppWindow().isLoginView())
                    viewTransactionButton.doAction();
            }
        });
        actionMap.put(getClass().getName()+":DELETE_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(!app.getAppWindow().isLoginView()&&Transaction.getSelectionCount()!=0)
                    deleteTransactionButton.doAction();
            }
        });
        actionMap.put(getClass().getName()+":ABOUT_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                    javax.swing.JOptionPane.showMessageDialog(app.getAppWindow(),"Developer: Essiennta Emmanuel\nSupervisor: Akinyele V. Olubodun\nVersion: 1.1\nOrganization: Think First Technology Limited.","About",javax.swing.JOptionPane.PLAIN_MESSAGE);
            }
        });
        actionMap.put(getClass().getName()+":SAVE_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(!app.getAppWindow().isLoginView())
                    doSave();
            }
        });
        actionMap.put(getClass().getName()+":LEFT_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(!app.getAppWindow().isLoginView()){
                    calendar.backward();
                    updateCalendarView();
                }
            }
        });
        actionMap.put(getClass().getName()+":RIGHT_COMMAND",new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(!app.getAppWindow().isLoginView()){
                    calendar.forward();
                    updateCalendarView();
                }
            }
        });
    }
    /***
     * Called to show all the information (Transactions, amount, and date) for the current Calendar in use by the model.
     * Open to further optimization *app.getModel().getTransactions()*
     * @param date 
     */
    private void updateView(){
        //Reset affected fields
        Transaction.resetSelectionCount();
        transactions.clear();
        panelDimension.height=0;
        panel.removeAll();//remove all the previous transactions that were being displayed
        
        TransactionRange tr=app.getModel().getTransactions();
        for(ATransaction t:tr)
            this.transactions.add(new Transaction(t));
        int y=(this.transactions.size()-1)*(27+1);
        int serial=1;
        for(Transaction t:this.transactions){
            t.setSize(960,27);
            t.setLocation(0,y);
            panel.add(t);
            panelDimension.height+=27+1;
            for(ATransaction at:tr)
                if(at.getDate().equals(t.getDate())){
                    at.setSerialNo(serial++);
                    break;
                }
            y-=27+1;
        }
        scrollPane.validate();
        scrollPane.repaint();
        updateAmount();
        dateLabel.setText(java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG).format(app.getModel().getCalendar().getTime()));
    }
    private java.util.TreeSet<Transaction>transactions=new java.util.TreeSet<Transaction>(new java.util.Comparator<Transaction>(){
        //Sort in order of increasing time
        public int compare(Transaction a,Transaction b){
            return a.getDate().before(b.getDate())?-1:1;
        }
    });
    private java.awt.Dimension panelDimension=new java.awt.Dimension(960,0);
    private void addTransactionRow(Transaction transaction){

        //set positions, transactions at the bottom of Transaction[] appear at the top...
        //while those at the top of Transaction[] appear at the bottom.
        
        for(Transaction t:transactions)
            t.setLocation(0,t.getLocation().y+27+1);

//        transaction.setLocation(0,0); ---> No need for this since this is the default location
        transaction.setSize(960,27);
        panelDimension.height+=27+1;
        panel.add(transaction);
        transactions.add(transaction);
    }
    private void deleteSelectedTransactions(java.util.ArrayList<Long>sortedDates){
        
        System.out.println("Set to delete items with the following dates in the given order:\n"+sortedDates);
        int currPointerIndex=0;
        //dates assumed to be sorted
        long pointer=sortedDates.get(currPointerIndex);
        System.out.println("Transactions are:\n"+transactions);
        int id=1;
        java.util.Iterator<Transaction>iter=transactions.iterator();
        while(iter.hasNext()){
            Transaction t=iter.next();
            if(t.getDate().getTime()==pointer){
                iter.remove();//equivalent to transactions.remove(t). Advantageous in that it raises no exception while iteration is ongoing.
                t.removeFromParent();
                System.out.println("Deleted item with date: "+pointer);
                System.out.println("Remaining:\n"+transactions);
                if(++currPointerIndex!=sortedDates.size())
                    pointer=sortedDates.get(currPointerIndex);
            }else{
                t.setSerialNo(id++);
            }
        }
        
        panelDimension.height-=currPointerIndex*(27+1);

        //and update the transaction size.
        //update the panel size and reset locations
        int yCoord=(transactions.size()-1)*28;
        
        //arrange from bottom to top
        for(Transaction t:transactions){
            t.setLocation(0,yCoord);
            yCoord-=27+1;
        }
        
        updateAmount();
        scrollPane.validate();
        scrollPane.repaint();
    }
    /**
     * Called to add a new entry to the display.
     * The display is painted immediately
     * @param t The transaction
     * @param repaint$DisplayAmountImmediately repaints the panel immediately if true is specified.
     */
    public void addEntry(ATransaction t){
        t.setSerialNo(transactions.size()+1);
        addTransactionRow(new Transaction(t));
        updateAmount();
        scrollPane.validate();
        scrollPane.repaint();
    }
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
        g2d.drawImage(cashbookImage,0,0,null);
    }
    public void updateEntry(ATransaction d){
        int nxtPos=(transactions.size()-1)*(27+1);
        int serial=1;
        for(Transaction t:transactions)
            if(t.getSerialNo()==d.getSerialNo()){
                t.setSerialNo(transactions.size());
                t.setFields(d.getStory(),d.getMode(),d.getAmount(),d.getDate());
                t.setLocation(0,0);
            }else{
                t.setSerialNo(serial++);
                t.setLocation(0,nxtPos);
                nxtPos-=27+1;
            }

        scrollPane.validate();
        scrollPane.repaint();
        updateAmount();
    }
    private void updateAmount(){
        long amount=0;
        for(Transaction t:transactions)
            amount+=(t.getTransactionType()==CashBookConstants.CREDIT?+1:-1)*t.getAmount();
        amountDisplayPanel.setForeground(amount<0?java.awt.Color.RED:java.awt.Color.WHITE);
        amountDisplayPanel.setText(""+amount);
    }
    @Override
    public void update(java.util.Observable observable,Object o){
        Operation operation=(Operation)o;
        setModified();
        ATransaction at;
        switch(operation.getCommand()){
            case CashBookConstants.OP_ADD:
                at=(ATransaction)operation.getObject();
                at.save();
                addEntry(at);
                break;
            case CashBookConstants.OP_EDIT://update the index
                at=(ATransaction)operation.getObject();
                at.save();
                updateEntry(at);
                break;
            case CashBookConstants.OP_DELETE://Just redraw the contents of the cashbook
                deleteSelectedTransactions((java.util.ArrayList<Long>)operation.getObject());
                break;
            default:
                assert false;//This place should never be reached.
        }
    }
}
