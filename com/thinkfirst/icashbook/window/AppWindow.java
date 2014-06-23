package com.thinkfirst.icashbook.window;

import com.thinkfirst.icashbook.App;

/**
 * The Application window
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class AppWindow extends javax.swing.JFrame{
    private int windowWidth=960;
    private int windowHeight=540;
    private App app;
    private java.awt.CardLayout cardLayout;
    private boolean isLoginView=true;
    
    public void nextView(){
        cardLayout.next(AppWindow.this.getContentPane());
    }
    public boolean isLoginView(){
        return isLoginView;
    }
    public void setLoginView(boolean toLoginScreen){
        isLoginView=toLoginScreen;
    }
    
    public AppWindow(App app,String title){
        setTitle(title);
        this.app=app;
        setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
        
        
        setResizable(false);//setResizable() should be called before pack() to alleviate the excess space problem i was battling with
        getContentPane().setPreferredSize(new java.awt.Dimension(windowWidth,windowHeight));
        
        //Set the cashbook screen as the current visible screen.
        getContentPane().setLayout(cardLayout=new java.awt.CardLayout());
        pack();
        setLocationRelativeTo(null);//This must be after the call to pack() or setVisible() for it to be properly centered on the screen.
        
        
        addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent e){
                if(AppWindow.this.app.getCashBook().hasBeenModified()){//there are some unsaved data
                    int response=javax.swing.JOptionPane.showConfirmDialog(AppWindow.this,"Recent changes have not been saved!\n"
                            + "Would you like to save them before you leave?","CONFIRMATION",javax.swing.JOptionPane.YES_NO_OPTION,javax.swing.JOptionPane.QUESTION_MESSAGE);
                    switch(response){
                        case javax.swing.JOptionPane.CLOSED_OPTION:return;
                        case javax.swing.JOptionPane.YES_OPTION:AppWindow.this.app.getModel().saveData(AppWindow.this);
                        case javax.swing.JOptionPane.NO_OPTION:
                            //Filter down to the dispose() method below.
                    }
                }//else all data were already saved, if window is not closing from login view, confirm close from user.
                else if(!AppWindow.this.app.getAppWindow().isLoginView&&javax.swing.JOptionPane.showConfirmDialog(AppWindow.this,"Are you sure you want to leave?","ALERT",javax.swing.JOptionPane.YES_NO_OPTION,javax.swing.JOptionPane.QUESTION_MESSAGE)
                !=javax.swing.JOptionPane.YES_OPTION)
                    return;//user doesn't wish to exit, return back to the program.
                //exit the application
                dispose();//Release window resources.
                AppWindow.this.app.getModel().shutdownModel(AppWindow.this);
            }
        });        
    }
}
