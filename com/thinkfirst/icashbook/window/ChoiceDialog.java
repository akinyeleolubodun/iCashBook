package com.thinkfirst.icashbook.window;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.component.PureButton;
import com.thinkfirst.icashbook.window.Calendar;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class ChoiceDialog extends javax.swing.JDialog{
    private java.awt.Image choiceImage;
    private ChoiceView view;
    private javax.swing.JLabel from=new javax.swing.JLabel("From:");
    private javax.swing.JLabel to=new javax.swing.JLabel("To:");
    private javax.swing.JTextField fromField=new javax.swing.JTextField(){
        public void setBorder(javax.swing.border.Border border){}//do nothing
    };
    private javax.swing.JTextField toField=new javax.swing.JTextField(){
        public void setBorder(javax.swing.border.Border border){}//do nothing
    };
    private Calendar calendar;
    private java.util.Calendar fromCalendar, toCalendar;
    private boolean isPositiveResponse;
    private boolean fromIsSet, toIsSet;
    

    public boolean isPositiveResponse(){
        return isPositiveResponse;
    }
    public java.util.Calendar getFrom(){
        return fromCalendar;
    }
    public java.util.Calendar getTo(){
        return toCalendar;
    }
    
    PureButton okButton=new PureButton("Ok",54,18,2,1){
        public void doAction(){
            if(fromIsSet&&toIsSet){
                if(toCalendar.before(fromCalendar)){
                    javax.swing.JOptionPane.showMessageDialog(ChoiceDialog.this,"Date in From field can't be later than Date in To field","ALERT",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                isPositiveResponse=true;
                exitButton.doAction();
            }else javax.swing.JOptionPane.showMessageDialog(ChoiceDialog.this,"Update all fields","ALERT",javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    };
    PureButton exitButton=new PureButton("Exit",54,18,2,1){
        public void doAction(){
            dispose();
        }
    };
    public ChoiceDialog(App app){
        super(app.getAppWindow(),"Choose Range",true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        String closingKey=getClass().getName()+":WINDOW_CLOSING";
        javax.swing.JRootPane root=getRootPane();
        root.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0),
                closingKey);
        root.getActionMap().put(closingKey,new javax.swing.AbstractAction(){
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e){
                exitButton.doAction();
            }
        });
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e){
                exitButton.doAction();
            }
        });
        
        this.getContentPane().setPreferredSize(new java.awt.Dimension(286,90));
        setResizable(false);
        view=new ChoiceView();
        this.choiceImage=App.getImage("choiceImage.gif");
        
        calendar=new Calendar(this,app.getModel().getStartCalendar());
        
        add(view);
        pack();
        setLocationRelativeTo(null);
    }
    
    public void setVisible(boolean b){
        //Clear fields
        fromField.setText("");
        toField.setText("");
        isPositiveResponse=false;
        fromIsSet=false;
        toIsSet=false;
        super.setVisible(b);
    }
    
    private class ChoiceView extends javax.swing.JComponent{
        public ChoiceView(){
            java.awt.Font theFont=App.getFont("tahoma.ttf").deriveFont(11.0f);
            from.setFont(theFont);
            to.setFont(theFont);
            from.setSize(from.getPreferredSize());
            to.setSize(to.getPreferredSize());
            fromField.setSize(131,26);
            toField.setSize(fromField.getSize());
            
            
            from.setLocation(8,9);
            to.setLocation(147,9);
            fromField.setLocation(8,28);
            fromField.setEditable(false);
            fromField.addMouseListener(new java.awt.event.MouseAdapter(){
                public void mouseClicked(java.awt.event.MouseEvent e){
                    calendar.setTitle("FROM:");
                    calendar.setVisible(true);
                    if(calendar.isPositiveResponse()){
                        fromIsSet=true;
                        fromField.setText(calendar.getDateString());
                        fromCalendar=(java.util.Calendar)calendar.getCalendar().clone();
                    }
                }
            });
            toField.setLocation(147,28);
            toField.setEditable(false);
            
            toField.addMouseListener(new java.awt.event.MouseAdapter(){
                public void mouseClicked(java.awt.event.MouseEvent e){
                    calendar.setTitle("TO:");
                    calendar.setVisible(true);
                    if(calendar.isPositiveResponse()){
                        toIsSet=true;
                        toField.setText(calendar.getDateString());
                        toCalendar=(java.util.Calendar)calendar.getCalendar().clone();
                    }
                }
            });
            okButton.setLocation(8,61);
            exitButton.setLocation(209+12,61);
            
            add(from);
            add(to);
            add(fromField);
            add(toField);
            add(okButton);
            add(exitButton);
        }
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
            g2d.drawImage(choiceImage,0,0,null);
        }
    }
}
