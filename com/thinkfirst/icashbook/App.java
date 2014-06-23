package com.thinkfirst.icashbook;

import com.thinkfirst.icashbook.component.Login;
import com.thinkfirst.icashbook.db.DatabaseModel;
import com.thinkfirst.icashbook.view.ICashBook;
import com.thinkfirst.icashbook.window.AppWindow;
import com.thinkfirst.icashbook.window.Startup;
import com.thinkfirst.icashbook.window.Trans;

/**
 * The Application
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class App {
    private static App app;
    private static YearManager yearManager;
    private static Trans trans;//The transaction dialog for edit and add
    private AppWindow appWindow;
    private DatabaseModel model;
    private ICashBook icashbook;
    private static Login loginView;
    private static java.util.HashMap<String,java.awt.Image>imageMap=new java.util.HashMap<String,java.awt.Image>(35,1.0f);
    private static java.util.HashMap<String,java.awt.Font>fontMap=new java.util.HashMap<String,java.awt.Font>(8,1.0f);
    private static Startup startUp;
    
    public static void addImage(String key,java.awt.Image value){
        imageMap.put(key,value);
    }
    public static java.awt.Image getImage(String key){
        return imageMap.get(key);//remove the image and return it
    }
    public static void addFont(String key,java.awt.Font value){
        fontMap.put(key,value);
    }
    public static java.awt.Font getFont(String key){
        return fontMap.get(key);
    }
    
    public DatabaseModel getModel(){
        return model;
    }
    public AppWindow getAppWindow(){
        return appWindow;
    }
    public static YearManager getYearManager(){
        return yearManager;
    }
    public static Trans getTrans(){
        return trans;
    }
    public ICashBook getCashBook(){
        return icashbook;
    }
    public static Login getLogin(){
        return loginView;
    }
    
    private void createGUI(){
        model=new DatabaseModel();//model should be created before AppWindow
        appWindow=new AppWindow(this,"icashbook");
        loginView=new Login(appWindow);
        icashbook=new ICashBook(this,"meicashbook.gif");
        model.addObserver(icashbook);
        appWindow.getContentPane().add(loginView,"login");
        appWindow.getContentPane().add(icashbook,"cashbook");
        appWindow.setIconImage(getImage("cashbookIcon.gif"));
        trans=new Trans(this);
        trans.pack();
        startUp.dispose();//dispose the startUp screen.
        appWindow.setVisible(true);
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        //Run startup screen first;
        startUp=new Startup(566,297);
        //Startup screen has finished running at this point
        
        
        javax.swing.UIManager.put("Button.defaultButtonFollowsFocus",java.lang.Boolean.TRUE);
        
//        
//
        String lnf=javax.swing.UIManager.getSystemLookAndFeelClassName();
        try{
            javax.swing.UIManager.setLookAndFeel(lnf);
        }catch(ClassNotFoundException e){
        }catch(InstantiationException e){
        }catch(IllegalAccessException e){
        }catch(javax.swing.UnsupportedLookAndFeelException e){
        }
        
        //Then create the main GUI
        //in the event dispatch thread
        
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                app=new App();
                app.createGUI();
            }
        });
    }
}