package com.thinkfirst.icashbook.window;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.persistence.Model;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Startup extends javax.swing.JWindow{
    private java.awt.Image image;
    private View view;
    private java.awt.Component baseComponent=new java.awt.Component(){};
    public Startup(int width,int height){
        loadImage("login_to-_app-copy23.gif",view=new View());
        image=App.getImage("login_to-_app-copy23.gif");
        getContentPane().setPreferredSize(new java.awt.Dimension(566,297));
        getContentPane().add(view);
        pack();
        //place in center of screen
        setLocationRelativeTo(null);
        setVisible(true);
        startAnimation();
    }
    private ProgressBar pbar;
    private void startAnimation(){
        pbar=new ProgressBar(86,228,393,6);
        view.add(pbar);
        view.validate();
        pbar.startAnimation();
        
        loadImage("login_to-_app-copy232.gif",baseComponent);
        image=App.getImage("login_to-_app-copy232.gif");
        repaint();
    }
    private class View extends javax.swing.JComponent{
        public View(){
            super();
        }
        public void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
            g2d.drawImage(image,0,0,null);
        }
    }
    
    private void loadImage(String name,java.awt.Component comp){
        java.net.URL url=App.class.getClassLoader().getResource("images/"+name);
        if(url==null)return;//do this to avoid NullPointerException later
        java.awt.Image image=java.awt.Toolkit.getDefaultToolkit().getImage(url);
        java.awt.MediaTracker tracker=new java.awt.MediaTracker(comp);
        tracker.addImage(image,0);
        try{
            tracker.waitForID(0);
        }catch(InterruptedException e){
            System.err.println("Error occured while loading image");
        }
        App.addImage(name,image);
    }
    
    
    public static void loadFont(String name){
        java.awt.Font font=null;
        String fName="fonts/"+name;
        try{
            java.io.InputStream is=App.class.getClassLoader().getResourceAsStream(fName);
            font=java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,is);
        }catch(Exception ex){
            Model.errorMessage("Error occured while loading font.",ex);
            System.err.println(fName+" not loaded.  Using serif font.");
            font=new java.awt.Font("serif",java.awt.Font.PLAIN,12);
        }
        App.addFont(name,font);
    }

    
    private class ProgressBar extends javax.swing.JLabel{        
    //Load all the progress bar images
    private java.awt.Image background;
    private int currIndex;
    public final String[]images={
        "meicashbook.gif",
        "cashbookIcon.gif",
//        "login_to-_app-copy23.gif", ...loaded specially later
        "leftArrow_entered.gif",
        "leftArrow_exited.gif",
        "rightArrow_entered.gif",
        "rightArrow_exited.gif",
        "calendar2.gif",
        "choiceImage.gif",
        "addOn.gif",
        "addOff.gif",
        "deleteOn.gif",
        "deleteOff.gif",
        "viewOn.gif",
        "viewOff.gif",
        "loginBg.gif",
//        "BlueProgressBar.gif", ...this will be loaded first in the constructor
//        "login_to-_app-copy232.gif",
        "_addOn.gif",
        "_addOff.gif",
        "_editOn.gif",
        "_editOff.gif",
        "_closeOn.gif",
        "_closeOff.gif",
        "input.gif",
        "upArrowOn.gif",
        "upArrowOff.gif",
        "downArrowOn.gif",
        "downArrowOff.gif",
        "checkOn.gif",
        "checkOff.gif",
        "check.gif",
        "edit_entered.gif",
        "edit_exited.gif",
        "transactionRow_entered.gif",
        "transactionRow_exited.gif"
    };
    public final String[]fonts={
        "calibrii.ttf",
        "sylfaen.ttf",
        "tahoma.ttf",
        "vijayab.ttf",
        "BAUHS93.TTF",
        "bgothl.ttf"
    };
    public ProgressBar(int x,int y,int width,int height){
        setBounds(x,y,width,height);
        loadImage("BlueProgressBar.gif",baseComponent);
        background=App.getImage("BlueProgressBar.gif");
    }
    public void startAnimation(){
        run();
    }
    int d;    
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
        g2d.drawImage(background,0,0,null);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(java.awt.Color.white);
        g2d.fillRoundRect(0,1,currIndex*393/(images.length+fonts.length),4,2,2);
    }
    public void run(){
        //load images
        for(currIndex=1;currIndex<=images.length;currIndex++){
            loadImage(images[currIndex-1],baseComponent);
            repaint();//paint this bar
            try{
                Thread.sleep(50);//sleep 50-950
            }catch(InterruptedException e){}
        }
        //load fonts
        int now=currIndex-1;
        for(;currIndex<=images.length+fonts.length;currIndex++){
            loadFont(fonts[currIndex-now-1]);
            repaint();
            try{
                Thread.sleep(50);//sleep 50-950
            }catch(InterruptedException e){}
        }
    }
    }
}

