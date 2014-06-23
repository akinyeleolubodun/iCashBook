package com.thinkfirst.icashbook.window;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.component.OnButton;
import com.thinkfirst.icashbook.component.PureButton;
import com.thinkfirst.icashbook.window.AppWindow;

/**
 * The Calendar
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Calendar extends javax.swing.JDialog{
    private java.awt.Image calendarImage;
    private CalendarView view;
    private java.util.GregorianCalendar calendar=new java.util.GregorianCalendar();
    private ItemSlider yearField;
    private ItemSlider monthField;
    private java.util.Calendar startCalendar;
    private PureButton[] ddays=new PureButton[31];//max of 31 days
    private int padding=3;
    private int leftX=14, leftY=108;
    private int lastNumOfDays=0;
    private String[] shortDayOfWeek={"Su","Mo","Tu","We","Th","Fr","Sa"};
    private java.awt.Font dayOfWeekFont=App.getFont("calibrii.ttf").deriveFont(18.0f);
    private final int buttonWidth=26, buttonHeight=23;
    private final int viewWidth=230, viewHeight=288;
    private PureButton okButton;
    private javax.swing.JLabel dateLabel;
    private java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM,java.util.Locale.US);
    private boolean isPositiveResponse;
    private java.awt.Component parentComponent;
    
    private void setup(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        String closingKey=getClass().getName()+":WINDOW_CLOSING";
        javax.swing.JRootPane root=getRootPane();
        root.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),
                closingKey);
        root.getActionMap().put(closingKey,new javax.swing.AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                dispose();
            }
        });

        calendar.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
        view=new CalendarView();
        calendarImage=App.getImage("calendar2.gif");
        java.awt.Toolkit toolkit=java.awt.Toolkit.getDefaultToolkit();
        java.awt.Dimension scrnSize=toolkit.getScreenSize();
        //place in center of screen
        setLocation((scrnSize.width-viewWidth)>>1,(scrnSize.height-viewHeight)>>1);

        getContentPane().setPreferredSize(new java.awt.Dimension(viewWidth,viewHeight));
        getContentPane().add(view);
        setResizable(false);
        pack();
    }
    public Calendar(AppWindow appWindow,java.util.Calendar startCalendar){
        super(appWindow,true);
        setTitle("icashbook Calendar");
        this.parentComponent=appWindow;
        this.startCalendar=startCalendar;
        yearField=new ItemSlider(ItemSlider.YEAR);
        monthField=new ItemSlider(ItemSlider.MONTH);
        setup();
    }
    public Calendar(ChoiceDialog dialog,java.util.Calendar startCalendar){
        super(dialog,"Choose Date",true);
        setTitle("icashbook Calendar");
        this.parentComponent=dialog;
        this.startCalendar=startCalendar;
        yearField=new ItemSlider(ItemSlider.YEAR);
        monthField=new ItemSlider(ItemSlider.MONTH);
        setup();
    }
    public void forward(){
        calendar.add(java.util.Calendar.DATE,1);
    }
    public void backward(){
        calendar.add(java.util.Calendar.DATE,-1);
    }
    public java.util.Calendar getCalendar(){
        return calendar;
    }
    public String getDateString(){
        return dateLabel.getText();
    }
    public boolean isPositiveResponse(){
        return isPositiveResponse;
    }
    public void setVisible(boolean b){
        isPositiveResponse=false;
        super.setVisible(b);
        setLocationRelativeTo(parentComponent);
    }

    private class CalendarView extends javax.swing.JComponent{
        public CalendarView(){
            yearField.setLocation(31,25-12);
            monthField.setLocation(31,53-12);
            okButton=new PureButton("OK",54,18,2,1){
                public void doAction(){
                    if(calendar.before(startCalendar)||calendar.after(new java.util.GregorianCalendar())){//too early or too late
                        javax.swing.JOptionPane.showMessageDialog(parentComponent,"The selected date is not within accessible range!","Date Not Available",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    isPositiveResponse=true;
                    Calendar.this.dispose();
                }
            };
            okButton.setLocation(leftX+5*(buttonWidth+padding),viewHeight-30);
            dateLabel=new javax.swing.JLabel();
            dateLabel.setFont(App.getFont("vijayab.ttf").deriveFont(18.0f));//segoe, digi
            dateLabel.setForeground(java.awt.Color.black);
            dateLabel.setSize(145,20);
            dateLabel.setLocation(14,260);
            add(yearField);
            add(monthField);
            add(okButton);
            add(dateLabel);

            for(int i=1;i<=ddays.length;i++)//22,108  right:+22+7..........down:+208+7
                ddays[i-1]=new CalendarButton(i,25,22,0,0,new java.awt.Color[]{PureButton.DEFAULT_MOUSE_EXITED_FROM,PureButton.DEFAULT_MOUSE_EXITED_TO},
                        new java.awt.Color[]{PureButton.DEFAULT_MOUSE_ENTERED_FROM,PureButton.DEFAULT_MOUSE_ENTERED_TO},
                        new java.awt.Color[]{PureButton.DEFAULT_MOUSE_EXITED_FROM,PureButton.DEFAULT_MOUSE_EXITED_TO},
                        new java.awt.Color[]{PureButton.DEFAULT_MOUSE_ENTERED_FROM,java.awt.Color.red});

            updateView();
        }
        private void resetPositions(){
            int numOfDays=31;
            int daysCount=0;
            int xCoord, yCoord=leftY-buttonHeight-padding;
            for(int i=0;i<5;i++){//for each column
                yCoord+=buttonHeight+padding;
                xCoord=leftX-padding-buttonWidth;
                for(int j=0;j<7&&daysCount<numOfDays;j++,daysCount++){//for each row
                    xCoord+=buttonWidth+padding;
                    ddays[daysCount].setLocation(xCoord,yCoord);
                }
            }
        }
        /**
         * Pushes each button to the right by the specified amount (in pixels).
         * If a button is at the rightmost side, the a next push on it moves
         * it down to the leftmost side.
         * @param the amount to move the buttons by
         */
        private void updateView(){
            int day=calendar.get(java.util.Calendar.DATE);
            calendar.set(java.util.Calendar.DATE,1);
            int amount=calendar.get(java.util.Calendar.DAY_OF_WEEK)-1;
            calendar.set(java.util.Calendar.DATE,day);
            int numOfDays=calendar.getActualMaximum(java.util.Calendar.DATE);
            dateLabel.setText(dateFormat.format(calendar.getTime()));
            resetPositions();
            if(numOfDays<lastNumOfDays)
                //remove excess
                for(int i=numOfDays;i<lastNumOfDays;i++)
                    remove(ddays[i]);
            else if(numOfDays>lastNumOfDays)
                //add more
                for(int i=lastNumOfDays;i<numOfDays;i++)
                    add(ddays[i]);



            //first shift all rows to the right once;
            //check for those that cross boundary
            int x_boundary=leftX+(buttonWidth+padding)*7-padding;
            for(int count=0;count<amount;count++)
                for(int i=0;i<numOfDays;i++){
                    ddays[i].setLocation(ddays[i].getX()+buttonWidth+padding,ddays[i].getY());
                    if(ddays[i].getX()>x_boundary)
                        ddays[i].setLocation(leftX,ddays[i].getY()+buttonHeight+padding);
                }
            lastNumOfDays=numOfDays;
            revalidate();// Do this and after repositioning the button and date panel and resizing this dialog.
            repaint();
        }
        private java.awt.Color sundayColor=new java.awt.Color(214,77,77);//214,77,77
        private java.awt.Color otherColor=java.awt.Color.black;//new java.awt.Color(160,240,240);
        private boolean firstTime1=true;
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            final java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
            g2d.drawImage(calendarImage,0,0,null);

            if(firstTime1){
                java.awt.FontMetrics fm=g2d.getFontMetrics();
                int st=leftX;
                theWidths=new int[shortDayOfWeek.length];
                for(int i=0;i<theWidths.length;i++){
                    int width=fm.stringWidth(shortDayOfWeek[i]);
                    theWidths[i]=st+(Calendar.this.buttonWidth-width)/2;
                    st+=Calendar.this.buttonWidth+padding;
                }
                firstTime1=false;
            }
            g2d.setFont(dayOfWeekFont);
            g2d.setPaint(sundayColor);
            g2d.drawString(shortDayOfWeek[0],theWidths[0],100);
            g2d.setPaint(otherColor);
            for(int i=1;i<theWidths.length;i++)
                g2d.drawString(shortDayOfWeek[i],theWidths[i],100);
        }
        private int[] theWidths;
    }
    private class CalendarButton extends PureButton{
        private int label;
        public CalendarButton(int label){
            super(Integer.toString(label));
            this.label=label;
        }
        public CalendarButton(int label,int w,int h,int rounding,int offset){
            super(Integer.toString(label),w,h,rounding,offset);
            this.label=label;
        }
        public CalendarButton(int label,java.awt.Color backgroundColor,java.awt.Color textColor){
            super(Integer.toString(label),backgroundColor,textColor);
            this.label=label;
        }
        public CalendarButton(int label,int w,int h,int rounding,int offset,java.awt.Color mouseExitedFrom,java.awt.Color mouseExitedTo,
                java.awt.Color mouseEnteredFrom,java.awt.Color mouseEnteredTo,java.awt.Color backgroundColor,java.awt.Color textColor){
            super(Integer.toString(label),w,h,rounding,offset,new java.awt.Color[]{mouseExitedFrom,mouseExitedTo},new java.awt.Color[]{mouseEnteredFrom,mouseEnteredTo},
                    new java.awt.Color[]{mouseExitedFrom,mouseExitedTo},new java.awt.Color[]{mouseEnteredFrom,mouseEnteredTo},backgroundColor,textColor);
            this.label=label;
        }
        public CalendarButton(int label,int w,int h,int rounding,int offset,java.awt.Color[]mouseReleasedExited,java.awt.Color[]mouseReleasedEntered,
                java.awt.Color[]mousePressedExited,java.awt.Color[]mousePressedEntered){
            super(Integer.toString(label),w,h,rounding,offset,mouseReleasedExited,mouseReleasedEntered,mousePressedExited,mousePressedEntered,
                    DEFAULT_BACKGROUNDCOLOR,DEFAULT_TEXTCOLOR);
            this.label=label;
        }
        public CalendarButton(int label,int w,int h,int rounding,int offset,java.awt.Color[]mouseReleasedExited,java.awt.Color[]mouseReleasedEntered,
                java.awt.Color[]mousePressedExited,java.awt.Color[]mousePressedEntered,java.awt.Color backgroundColor,java.awt.Color textColor){
            super(Integer.toString(label),w,h,rounding,offset,mouseReleasedExited,mouseReleasedEntered,mousePressedExited,mousePressedEntered,
                    backgroundColor,textColor);
            this.label=label;
        }
        public void doAction(){
            calendar.set(java.util.Calendar.DATE,label);
            dateLabel.setText(dateFormat.format(calendar.getTime()));
        }
    }

    private class ItemSlider extends javax.swing.JComponent{
        public final static int YEAR=1;
        public final static int MONTH=2;
        private final OnButton leftArrow;
        private final OnButton rightArrow;
        private javax.swing.JLabel labelField;
        private final int field;
        private String[] members;
        private int start;
        private int end;
        
        public ItemSlider(final int mode){
            if(mode==YEAR){
                start=startCalendar.get(java.util.Calendar.YEAR);
                end=9999;
                field=java.util.Calendar.YEAR;
                members=getYears(start,end);
            }else if(mode==MONTH){
                start=java.util.Calendar.JANUARY;
                end=java.util.Calendar.DECEMBER;
                field=java.util.Calendar.MONTH;
                members=java.text.DateFormatSymbols.getInstance().getMonths();
            }else
                throw new IllegalArgumentException(getClass().getName()+" : Unknown Mode.");

            leftArrow=new OnButton("leftArrow_entered.gif","leftArrow_exited.gif"){
                public boolean isFocusable(){
                    return false;
                }
                public void doAction(){
                    if(calendar.get(field)==start)
                        calendar.set(field,end);
                    else
                        calendar.roll(field,false);
                    labelField.setText(members[calendar.get(field)-start]);
                    view.updateView();
                }
            };
            rightArrow=new OnButton("rightArrow_entered.gif","rightArrow_exited.gif"){
                public boolean isFocusable(){
                    return false;
                }
                public void doAction(){
                    if(calendar.get(field)==end)
                        calendar.set(field,start);
                    else
                        calendar.roll(field,true);
                    labelField.setText(members[calendar.get(field)-start]);
                    view.updateView();
                }
            };

            leftArrow.setLocation(2,3);
            leftArrow.setSize(16,18);

            rightArrow.setLocation(152,3);
            rightArrow.setSize(16,18);
            labelField=new javax.swing.JLabel(members[calendar.get(field)-start],javax.swing.JLabel.CENTER);//This will originally be replace by the current Date and time.
            labelField.setFont(App.getFont("calibrii.ttf").deriveFont(java.awt.Font.PLAIN,15));
            labelField.setLocation(21,4);
            labelField.setSize(127,21);

            add(leftArrow);//Remember default layout for JComponent is null. Set layout to null if any other component is used as parent
            add(labelField);
            add(rightArrow);

            setSize(170,25);
        }
        /**
         * @param from The year to begin
         * @param to The year to end
         * @return the range of years as specified or null if from is greater than to or the values given in from or to are not four digits long
         */
        private String[] getYears(int from,int to){
            if(from>to)//years don't count backwards
                return null;
            String[] years=new String[to-from+1];
            for(int i=0;i<years.length;i++)
                years[i]=Integer.toString(from+i);
            return years;
        }
    }
}