package com.thinkfirst.icashbook.component;

import com.thinkfirst.icashbook.App;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public abstract class OnButton extends javax.swing.JLabel implements java.awt.event.KeyListener,
        java.awt.event.MouseListener,java.awt.event.FocusListener{
    public static final int ENTERED=1;
    public static final int EXITED=0;
    private java.awt.Image image[]=new java.awt.Image[2];
    protected java.awt.Image currentImage;
    private boolean isFocus;//true when this button is the focus
    public OnButton(String imageEntered,String imageExited){
        currentImage=image[0]=App.getImage(imageExited);
        image[1]=App.getImage(imageEntered);
        addMouseListener(this);
        addFocusListener(this);
        addKeyListener(this);
    }
    /**
     * Action to be performed when this button is clicked on.
     */
    public abstract void doAction();
    public boolean isFocusable(){
        return true;
    }
    public void focusGained(java.awt.event.FocusEvent e){
        isFocus=true;
        setState(ENTERED);
    }
    public void focusLost(java.awt.event.FocusEvent e){
        isFocus=false;
        setState(EXITED);
    }
    public void keyReleased(java.awt.event.KeyEvent e){
        if(e.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER){
            doAction();
            setState(ENTERED);
        }
    }
    public void keyPressed(java.awt.event.KeyEvent e){
        if(e.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER){
            setState(EXITED);
        }
    }
    public void keyTyped(java.awt.event.KeyEvent e){
    }
    public void mouseEntered(java.awt.event.MouseEvent e){
        if(!isFocus)
            setState(ENTERED);
    }
    public void mouseExited(java.awt.event.MouseEvent e){
        if(!isFocus)
            setState(EXITED);
    }
    public void mouseClicked(java.awt.event.MouseEvent e){
        doAction();
        if(!isFocusOwner())
            requestFocusInWindow();
    }
    public void mousePressed(java.awt.event.MouseEvent e){
        setState(EXITED);
    }
    public void mouseReleased(java.awt.event.MouseEvent e){
        setState(ENTERED);
    }
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
        g2d.drawImage(currentImage,0,0,null);
    }
    protected void setNoRepaintState(int state){
        if(currentImage==image[state])
            return;
        currentImage=image[state];
    }
    protected void setState(int state){
        if(currentImage==image[state])
            return;
        currentImage=image[state];
        repaint();
    }
}
