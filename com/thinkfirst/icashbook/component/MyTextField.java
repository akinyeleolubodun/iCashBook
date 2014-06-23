package com.thinkfirst.icashbook.component;
/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class MyTextField extends javax.swing.JTextField{
        private boolean isEmpty=true;
        private String defaultText;
        private boolean isTyping=false;
        private java.awt.Color dimColor=java.awt.Color.lightGray;
        private java.awt.Color deepColor=java.awt.Color.black;
        private java.awt.Font plain=getFont().deriveFont(12.0f);
        private java.awt.Font italics=plain.deriveFont(java.awt.Font.ITALIC);
        private static double cos45=Math.cos(Math.PI/4);
        private Document doc;
        public String getDefaultText(){
            return defaultText;
        }
        /**
         * This method is expected to be called after the text field has been setup.
         * The returned panel takes the size that was set for the text field and readjust the size of the text field.
         * @return a JPanel containing this text field. The returned component looks like a rounded text field.
         */
        public javax.swing.JPanel getEncapsulator(){
            final java.awt.Color color=new java.awt.Color(230,231,232);
            final int round=10;
            //create panel, draw a round rect on it. add the text field
            javax.swing.JPanel panel=new javax.swing.JPanel(new java.awt.BorderLayout()){
                public void paintComponent(java.awt.Graphics g){
                    super.paintComponent(g);
                    java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
                    g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setPaint(color);
                    g2d.fillRoundRect(0,0,getWidth(),getHeight(),round,round);
                }
            };
            panel.setLayout(null);
            panel.setOpaque(false);
            setBackground(color);//set the background of the mytextfield.
            panel.setLocation(getLocation());
            panel.setSize(getSize());
            int r=round-(int)(round*cos45);
            setLocation(r,r);
            setSize(getWidth()-2*r,getHeight()-2*r);
            panel.add(this);//add this MyTextField object to the panel.
            return panel;
        }
        public void enableUserEntry(boolean enable){
            setText("");//clear text in the text field.
            if(enable){
                isTyping=true;
                setFont(plain);
                setForeground(deepColor);
            }else{
                isTyping=false;
                setFont(italics);
                setForeground(dimColor);
            }
        }
        public MyTextField(String defaultText,int maxChars,final boolean forPassword){
            super();
            this.defaultText=defaultText;
            addFocusListener(new java.awt.event.FocusAdapter(){
                public void focusGained(java.awt.event.FocusEvent e){
                    if(isEmpty)//this is to avoid overwriting the text that the user has already entered.
                        enableUserEntry(true);
                }
                public void focusLost(java.awt.event.FocusEvent e){
                    if(isEmpty){//this is to avoid overwriting the text that the user has already entered.
                        enableUserEntry(false);
                        setText(MyTextField.this.defaultText);
                    }
                }
            });
            setDocument(forPassword?(doc=new PasswordDoc(maxChars)):(doc=new Document(maxChars)));
            setFont(italics);
            setForeground(dimColor);
//            setText(defaultText);
        }
        /**
         * Sets the new text to be displayed in dim color in this text area.
         * @param text 
         */
        public void setDefaultText(String text){
            this.defaultText=text;
        }
        public String getString(){
            String string=null;
            try{
                string=doc.getString();
            }catch(javax.swing.text.BadLocationException e){}
            return string;
        }

        private class PasswordDoc extends Document{
            private StringBuilder builder;
            char[] asterisk;
            private PasswordDoc(int maxChars){
                super(maxChars);
                asterisk=new char[maxChars];
                for(int i=0;i<asterisk.length;i++)
                    asterisk[i]='*';
                builder=new StringBuilder(maxChars);
            }
            public void insertString(int offs,String str,javax.swing.text.AttributeSet attr)
                    throws javax.swing.text.BadLocationException{
                if(getLength()+str.length()>asterisk.length){
                    super.complain();
                    return;
                }
                if(isTyping){
                    super.insertString(offs,String.valueOf(asterisk,0,str.length()),attr);
                    builder.insert(offs,str);
                }else
                    super.insertString(offs,str,attr);
            }
            public void remove(int offs,int length) throws javax.swing.text.BadLocationException{
                super.remove(offs,length);
                if(isTyping)
                    builder.delete(offs,offs+length);
            }
            public String getString() throws javax.swing.text.BadLocationException{//override to return builder's string
                return builder.toString();
            }
        }

        private class Document extends javax.swing.text.PlainDocument{
            private int maxChars;
            private Document(int maxChars){
                this.maxChars=maxChars;
            }
            public String getString() throws javax.swing.text.BadLocationException{
                return isEmpty?"":getText(0,getLength());
            }
            public void complain(){
                java.awt.Toolkit.getDefaultToolkit().beep();
            }
            public void insertString(int offs,String str,javax.swing.text.AttributeSet attr)
                    throws javax.swing.text.BadLocationException{
                if(getLength()+str.length()>maxChars){
                    complain();
                    return;
                }
                super.insertString(offs,str,attr);

                if(isTyping)//cursor blinking for user to insert text
                    isEmpty=false;
                else{//focus lost entered "Username"
                    //empty determined here. typing determined there
                }
            }
            public void remove(int offs,int length) throws javax.swing.text.BadLocationException{
                super.remove(offs,length);
                if(getLength()==0)
                    isEmpty=true;
            }
        }
    
}
