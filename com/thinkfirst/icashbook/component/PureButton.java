package com.thinkfirst.icashbook.component;
/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public abstract class PureButton extends javax.swing.JLabel{
    public static final java.awt.Color DEFAULT_BACKGROUNDCOLOR=java.awt.Color.gray;
    public static final java.awt.Color DEFAULT_TEXTCOLOR=java.awt.Color.black;
    public static final java.awt.Color DEFAULT_MOUSE_EXITED_FROM=new java.awt.Color(241,242,242);
    public static final java.awt.Color DEFAULT_MOUSE_EXITED_TO=new java.awt.Color(143,161,161);
    public static final java.awt.Color DEFAULT_MOUSE_ENTERED_FROM=new java.awt.Color(241,242,242);
    public static final java.awt.Color DEFAULT_MOUSE_ENTERED_TO=new java.awt.Color(166,186,186);
    
    final private int RELEASED=0, PRESSED=1, EXITED=0, ENTERED=1;
    private final int width, height;//width and height
    private java.awt.GradientPaint gradient[][]=new java.awt.GradientPaint[2][2];//[mousePressed][mouseEntered]..4 gradients
    private java.awt.Color backgroundColor;
    private java.awt.Color textColor;
    java.awt.image.BufferedImage[][] off$on=new java.awt.image.BufferedImage[2][2];//[mouseInside][mousePressed]
    int mousePressed;//1 when mouse is pressed, 0 when mouse is released.
    private int mouseInside;//1 when mouse is inside, 0 when mouse is outside.
    private final String label;
    private final int offset;//offset when the button is down.
    private final int rounding;//rounding of the corner edges of the button.
    public PureButton(String label){
        this(label,DEFAULT_BACKGROUNDCOLOR,DEFAULT_TEXTCOLOR);
    }
    public PureButton(String label,int w,int h,int rounding,int offset){
        this(label,w,h,rounding,offset,
                new java.awt.Color[]{DEFAULT_MOUSE_EXITED_FROM,DEFAULT_MOUSE_EXITED_TO},
                new java.awt.Color[]{DEFAULT_MOUSE_ENTERED_FROM,DEFAULT_MOUSE_ENTERED_TO},
                new java.awt.Color[]{DEFAULT_MOUSE_EXITED_FROM,DEFAULT_MOUSE_EXITED_TO},
                new java.awt.Color[]{DEFAULT_MOUSE_ENTERED_FROM,DEFAULT_MOUSE_ENTERED_TO}
                ,DEFAULT_BACKGROUNDCOLOR,DEFAULT_TEXTCOLOR);
    }
    public PureButton(String label,java.awt.Color backgroundColor,java.awt.Color textColor){
        this(label,200,60,10,2,
                new java.awt.Color[]{DEFAULT_MOUSE_EXITED_FROM,DEFAULT_MOUSE_EXITED_TO},
                new java.awt.Color[]{DEFAULT_MOUSE_ENTERED_FROM,DEFAULT_MOUSE_ENTERED_TO},
                new java.awt.Color[]{DEFAULT_MOUSE_EXITED_FROM,DEFAULT_MOUSE_EXITED_TO},
                new java.awt.Color[]{DEFAULT_MOUSE_ENTERED_FROM,DEFAULT_MOUSE_ENTERED_TO},
                backgroundColor,textColor);
    }
    public PureButton(String label,int w,int h,int rounding,int offset,java.awt.Color[] mouseReleasedExited,java.awt.Color[] mouseReleasedEntered,
            java.awt.Color[] mousePressedExited,java.awt.Color[] mousePressedEntered,java.awt.Color backgroundColor,java.awt.Color textColor){
        super();
        this.label=label;
        this.width=w;
        this.height=h;
        if(rounding>h/2)
            rounding=h/2;//adjust rounding
        this.rounding=rounding;
        this.offset=offset;
        this.backgroundColor=backgroundColor;
        this.textColor=textColor;
        gradient[RELEASED][EXITED]=new java.awt.GradientPaint(0,0,mouseReleasedExited[0],w,h,mouseReleasedExited[1]);//on mouseExit
        gradient[RELEASED][ENTERED]=new java.awt.GradientPaint(0,0,mouseReleasedEntered[0],w,h,mouseReleasedEntered[1]);//on mouseEnter
        gradient[PRESSED][EXITED]=new java.awt.GradientPaint(0,0,mousePressedExited[0],w,h,mousePressedExited[1]);
        gradient[PRESSED][ENTERED]=new java.awt.GradientPaint(0,0,mousePressedEntered[0],w,h,mousePressedEntered[1]);


        setPreferredSize(new java.awt.Dimension(w+offset+1,h+offset+1));
        setSize(w+offset+1,h+offset+1);//in case a this component is placed on a null layout

        addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){
                mouseInside=1;
                if(mousePressed==1){
                    mousePressed(e);
                    return;
                }
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e){
                mouseInside=0;
                repaint();
            }
            public void mousePressed(java.awt.event.MouseEvent e){
                mousePressed=1;
                repaint();
            }
            public void mouseReleased(java.awt.event.MouseEvent e){
                mousePressed=0;
                if(mouseInside==1)
                    mouseEntered(e);
                else
                    mouseExited(e);
            }
            public void mouseClicked(java.awt.event.MouseEvent e){
                doAction();
            }
        });
    }
    private boolean firstTime=true;
    private final double COS45=Math.cos(Math.PI/4);
    public void paintComponent(java.awt.Graphics graphics){
        super.paintComponent(graphics);
        if(firstTime){
            java.awt.Rectangle rect=new java.awt.Rectangle(0,0,width,height);
            java.awt.geom.GeneralPath button=new java.awt.geom.GeneralPath();
            java.awt.geom.GeneralPath off=new java.awt.geom.GeneralPath();
            java.awt.geom.GeneralPath on=new java.awt.geom.GeneralPath();
            int roundingcos45=(int)Math.round(rounding*COS45);
            java.awt.Point l=new java.awt.Point(rect.x,rect.y+rounding);
            java.awt.Point b=new java.awt.Point(rect.x+rounding,rect.y);
            java.awt.Point c=new java.awt.Point(rect.width-rounding,rect.y);
            java.awt.Point e=new java.awt.Point(rect.width,rect.y+rounding);
            java.awt.Point f=new java.awt.Point(rect.width,rect.height-rounding);
            java.awt.Point h=new java.awt.Point(rect.width-rounding,rect.height);
            java.awt.Point i=new java.awt.Point(rect.x+rounding,rect.height);
            java.awt.Point k=new java.awt.Point(rect.x,rect.height-rounding);

            java.awt.Point a=new java.awt.Point(b.x-roundingcos45,l.y-roundingcos45);
            java.awt.Point d=new java.awt.Point(c.x+roundingcos45,e.y-roundingcos45);
            java.awt.Point g=new java.awt.Point(h.x+roundingcos45,f.y+roundingcos45);
            java.awt.Point j=new java.awt.Point(i.x-roundingcos45,k.y+roundingcos45);



            button=new java.awt.geom.GeneralPath();
            button.moveTo(l.x,l.y);
            button.quadTo(rect.x,rect.y,b.x,b.y);
            button.lineTo(c.x,c.y);
            button.quadTo(rect.width,rect.y,e.x,e.y);
            button.lineTo(f.x,f.y);
            button.quadTo(rect.width,rect.height,h.x,h.y);
            button.lineTo(i.x,i.y);
            button.quadTo(rect.x,rect.height,k.x,k.y);
            button.closePath();//join to l.x,l.y


            off=new java.awt.geom.GeneralPath();
            off.moveTo(l.x,l.y);
            off.quadTo(rect.x,rect.y,b.x,b.y);
            off.lineTo(c.x,c.y);
//            off.lineTo(d.x,d.y);
            off.quadTo(rect.width,rect.y,e.x,e.y);
            off.lineTo(d.x,d.y);
            off.lineTo(d.x+offset,d.y+offset);
            off.lineTo(c.x+offset,c.y+offset);

            off.quadTo(rect.width+offset,rect.y+offset,e.x+offset,e.y+offset);
            off.lineTo(f.x+offset,f.y+offset);
            off.quadTo(rect.width+offset,rect.height+offset,h.x+offset,h.y+offset);
            off.lineTo(i.x+offset,i.y+offset);
            off.quadTo(rect.x+offset,rect.height+offset,k.x+offset,k.y+offset);
            off.lineTo(j.x+offset,j.y+offset);
            off.lineTo(j.x,j.y);
            off.lineTo(k.x,k.y);
            off.closePath();

            on=new java.awt.geom.GeneralPath();
            on=(java.awt.geom.GeneralPath)off.clone();

            java.awt.FontMetrics fm;
            int stringWidth;
            int stringHeight;

            //[inside][pressed]
            off$on[EXITED][RELEASED]=new java.awt.image.BufferedImage(width+offset+1,height+offset+1,java.awt.image.BufferedImage.TYPE_INT_ARGB);//(java.awt.image.BufferedImage)createImage(width+offset+1,height+offset+1);
            java.awt.Graphics2D imageGraphics=off$on[EXITED][RELEASED].createGraphics();
            fm=imageGraphics.getFontMetrics();//font metrics for the current font
            stringWidth=fm.stringWidth(label);
            stringHeight=fm.getHeight();

            imageGraphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.fill(off);
            imageGraphics.draw(off);
            imageGraphics.setPaint(gradient[RELEASED][EXITED]);
            imageGraphics.fill(button);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.draw(button);
            imageGraphics.setPaint(textColor);
            imageGraphics.drawString(label,(width-stringWidth)>>1,(height-stringHeight)/2+fm.getAscent());

            off$on[ENTERED][RELEASED]=new java.awt.image.BufferedImage(width+offset+1,height+offset+1,java.awt.image.BufferedImage.TYPE_INT_ARGB);//(java.awt.image.BufferedImage)createImage(width+offset+1,height+offset+1);
            imageGraphics=off$on[ENTERED][RELEASED].createGraphics();
            fm=imageGraphics.getFontMetrics();//font metrics for the current font
            stringWidth=fm.stringWidth(label);
            stringHeight=fm.getHeight();

            imageGraphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.fill(off);
            imageGraphics.draw(off);
            imageGraphics.setPaint(gradient[RELEASED][ENTERED]);
            imageGraphics.fill(button);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.draw(button);
            imageGraphics.setPaint(textColor);
            imageGraphics.drawString(label,(width-stringWidth)>>1,(height-stringHeight)/2+fm.getAscent());

            off$on[EXITED][PRESSED]=new java.awt.image.BufferedImage(width+offset+1,height+offset+1,java.awt.image.BufferedImage.TYPE_INT_ARGB);//(java.awt.image.BufferedImage)createImage(width+offset+1,height+offset+1);
            imageGraphics=off$on[EXITED][PRESSED].createGraphics();
            fm=imageGraphics.getFontMetrics();//font metrics for the current font
            stringWidth=fm.stringWidth(label);
            stringHeight=fm.getHeight();

            imageGraphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.fill(on);
            imageGraphics.draw(on);
            imageGraphics.setPaint(gradient[PRESSED][EXITED]);
            imageGraphics.translate(offset,offset);
            imageGraphics.fill(button);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.draw(button);
            imageGraphics.setPaint(textColor);
            imageGraphics.drawString(label,(width-stringWidth)>>1,(height-stringHeight)/2+fm.getAscent());

            off$on[ENTERED][PRESSED]=new java.awt.image.BufferedImage(width+offset+1,height+offset+1,java.awt.image.BufferedImage.TYPE_INT_ARGB);//(java.awt.image.BufferedImage)createImage(width+offset+1,height+offset+1);
            imageGraphics=off$on[ENTERED][PRESSED].createGraphics();
            fm=imageGraphics.getFontMetrics();//font metrics for the current font
            stringWidth=fm.stringWidth(label);
            stringHeight=fm.getHeight();

            imageGraphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.fill(on);
            imageGraphics.draw(on);
            imageGraphics.setPaint(gradient[PRESSED][ENTERED]);
            imageGraphics.translate(offset,offset);
            imageGraphics.fill(button);
            imageGraphics.setPaint(backgroundColor);
            imageGraphics.draw(button);
            imageGraphics.setPaint(textColor);
            imageGraphics.drawString(label,(width-stringWidth)>>1,(height-stringHeight)/2+fm.getAscent());
            firstTime=false;
        }
        java.awt.Graphics2D g2d=(java.awt.Graphics2D)graphics;
        g2d.drawImage(off$on[mouseInside][mousePressed],0,0,this);
    }
    public abstract void doAction();
}
