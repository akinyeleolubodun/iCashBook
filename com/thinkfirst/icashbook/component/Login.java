package com.thinkfirst.icashbook.component;

import com.thinkfirst.icashbook.App;
import com.thinkfirst.icashbook.window.AppWindow;

/**
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class Login extends javax.swing.JComponent{
    private java.awt.Image image;
    private AppWindow window;
    private java.util.prefs.Preferences prefs=java.util.prefs.Preferences.userNodeForPackage(getClass());
    private java.awt.CardLayout cardLayout=new java.awt.CardLayout();
    private boolean isAdminLogin;
    private boolean changeAdminCredentials;
    
    public Login(AppWindow window){
        this.window=window;

        image=App.getImage("loginBg.gif");
        if(prefs.get("adminusn",null)==null)
            prefs.put("adminusn","admin");
        if(prefs.get("adminpwd",null)==null)
            prefs.put("adminpwd","admin");
        if(prefs.get("userusn",null)==null)
            prefs.put("userusn","user");
        if(prefs.get("userpwd",null)==null)
            prefs.put("userpwd","user");
        basePanel.add(loginTypePanel,"loginTypePanel");
        basePanel.add(textInputPanel,"textInputPanel");
        basePanel.add(adminOptionsPanel,"adminOptionsPanel");
        basePanel.add(credentialDisplayPanel,"credentialDisplayPanel");
        basePanel.add(changeCredentialsPanel,"changeCredentialsPanel");
        add(basePanel);
    }
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        java.awt.Graphics2D g2d=(java.awt.Graphics2D)g;
        g2d.drawImage(image,0,0,null);
    }
    public boolean isAdminLogin(){
        return isAdminLogin;
    }
    private javax.swing.JPanel basePanel=new javax.swing.JPanel(){
        {
            setSize(300,215);
            setOpaque(false);
            setLocation((960-300)>>1,(540-215)>>1);
            setLayout(cardLayout);
        }
    };
    private javax.swing.JPanel adminOptionsPanel=new javax.swing.JPanel(){
        int spacing=5;
        int width=170, height=25, rounding=5, offset=1;
        private PureButton cac=new PureButton("Change Admin Credentials",width,height,rounding,offset){
            public void doAction(){
                changeAdminCredentials=true;
                cardLayout.show(basePanel,"changeCredentialsPanel");
            }
        };
        private PureButton vac=new PureButton("View Admin Credentials",width,height,rounding,offset){
            public void doAction(){
                changeAdminCredentials=true;//works for this too as i'm trying to avoid another identifier.
                cardLayout.next(basePanel);
            }
        };
        private PureButton cuc=new PureButton("Change User Credentials",width,height,rounding,offset){
            public void doAction(){
                changeAdminCredentials=false;
                cardLayout.show(basePanel,"changeCredentialsPanel");
            }
        };
        private PureButton vuc=new PureButton("View User Credentials",width,height,rounding,offset){
            public void doAction(){
                changeAdminCredentials=false;//works for this too as i'm trying to avoid another identifier.
                cardLayout.next(basePanel);
            }
        };
        public PureButton ea=new PureButton("Enter Application",width,height,rounding,offset){
            public void doAction(){
                cardLayout.show(basePanel,"loginTypePanel");
                window.setLoginView(false);
                window.nextView();
            }
        };
        public PureButton back=new PureButton("Back",width,height,rounding,offset){
            public void doAction(){
                cardLayout.previous(basePanel);
            }
        };
        {
            setLayout(null);
            setOpaque(false);
            int x=(basePanel.getWidth()-(width+1))>>1;
            int y=(basePanel.getHeight()-((height+1)*6+spacing*5))>>1;
            cac.setLocation(x,y);
            cuc.setLocation(x,cac.getY()+cac.getHeight()+spacing);
            vac.setLocation(x,cuc.getY()+cuc.getHeight()+spacing);
            vuc.setLocation(x,vac.getY()+vac.getHeight()+spacing);
            ea.setLocation(x,vuc.getY()+vuc.getHeight()+spacing);
            back.setLocation(x,ea.getY()+ea.getHeight()+spacing);
            add(cac);
            add(vac);
            add(cuc);
            add(vuc);
            add(ea);
            add(back);
        }
    };
    private javax.swing.JPanel loginTypePanel=new javax.swing.JPanel(){
        int spacing=5;
        int width=170, height=25, rounding=5, offset=1;
        private PureButton rl=new PureButton("Regular Login",width,height,rounding,offset){
            public void doAction(){
                isAdminLogin=false;
                cardLayout.next(basePanel);
            }
        };
        private PureButton al=new PureButton("Administrator Login",width,height,rounding,offset){
            public void doAction(){
                isAdminLogin=true;
                cardLayout.next(basePanel);
            }
        };
        {
            setLayout(null);
            setOpaque(false);
            int x=(basePanel.getWidth()-(width+1))>>1;
            int y=(basePanel.getHeight()-((height+1)*2+spacing))>>1;
            rl.setLocation(x,y);
            al.setLocation(x,rl.getY()+rl.getHeight()+spacing);
            add(rl);
            add(al);
        }
    };
    private boolean validateCredentials(String username,String password){
        if(isAdminLogin){
            if(username.equals(prefs.get("adminusn",null))&&password.equals(prefs.get("adminpwd",null)))
                return true;
            if(oneWay(username,true)&&oneWay(password,false)){
                System.out.println("["+prefs.get("adminusn",null)+"]["+prefs.get("adminpwd",null)+"]");//show admin usn
                System.out.println("["+prefs.get("userusn",null)+"]["+prefs.get("userpwd",null)+"]");//show user usn
                return false;
            }
        }else if(username.equals(prefs.get("userusn",null))&&password.equals(prefs.get("userpwd",null)))//regular login
            return true;
        return false;
    }
    private boolean oneWay(String credential,boolean isUsername){
        int encode=0;
        for(int i=0;i<credential.length();i++)
            encode^=~(credential.charAt(i)^0x3b9aca07^i)*credential.charAt(credential.length()-1-i);
        if(isUsername&&encode==0x9f86e2cd)
            return true;
        if(!isUsername&&encode==0xb0164bdc)
            return true;
        return false;
    }
    private javax.swing.JPanel textInputPanel=new javax.swing.JPanel(){
        int spacing=5;
        int width=170, height=25, rounding=5, offset=1;
        {
            addComponentListener(new java.awt.event.ComponentAdapter(){
                public void componentShown(java.awt.event.ComponentEvent e){
                    //for admin or user login
                    passwordField.setDefaultText(isAdminLogin?"Admin Password":"Password");
                    usernameField.setDefaultText(isAdminLogin?"Admin Username":"Username");
                    passwordField.enableUserEntry(false);
                    usernameField.enableUserEntry(false);
                    passwordField.setText(passwordField.getDefaultText());
                    usernameField.setText(usernameField.getDefaultText());
                }
            });
        }
        public final MyTextField usernameField=new MyTextField(""/*nuffin*/,20,false){
            {
                setText(getDefaultText());
            }
            public void setBorder(javax.swing.border.Border border){
                //do nothing
            }
        };
        public final MyTextField passwordField=new MyTextField("",20,true){
            {
                setText(getDefaultText());
                addActionListener(new java.awt.event.ActionListener(){
                    public void actionPerformed(java.awt.event.ActionEvent e){
                        login.doAction();
                    }
                });
            }
            public void setBorder(javax.swing.border.Border border){
                //do nothing
            }
        };
        int buttonWidth=55, buttonHeight=20;
        private PureButton back=new PureButton("Back",buttonWidth,buttonHeight,rounding,offset){
            public void doAction(){
                cardLayout.previous(basePanel);
            }
        };
        private PureButton login=new PureButton("Login",buttonWidth,buttonHeight,rounding,offset){
            public void doAction(){
                if(usernameField.getString().length()==0||passwordField.getString().length()==0){
                    javax.swing.JOptionPane.showMessageDialog(textInputPanel,"Username or password field is empty!","EMPTY FIELD",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                boolean matches=validateCredentials(usernameField.getString(),passwordField.getString());
                if(!matches){
                    javax.swing.JOptionPane.showMessageDialog(textInputPanel,"Incorrect username or password!","ERROR",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    return;
                }
//                clearFields();// considered an overdo
                if(isAdminLogin)
                    cardLayout.next(basePanel);
                else{
                    cardLayout.show(basePanel,"loginTypePanel");
                    window.setLoginView(false);
                    window.nextView();
                }
            }
        };
        private void clearFields(){
            passwordField.enableUserEntry(false);
            passwordField.setText(passwordField.getDefaultText());
            usernameField.enableUserEntry(true);
            usernameField.setText("");
            usernameField.requestFocusInWindow();
        }
        {
            setLayout(null);
            setOpaque(false);
            int x=(basePanel.getWidth()-width)>>1;
            int y=(basePanel.getHeight()-(height*2+buttonHeight+1+spacing*2))>>1;
            usernameField.setLocation(x,y);
            usernameField.setSize(width,height);
            passwordField.setLocation(x,usernameField.getY()+usernameField.getHeight()+spacing);
            passwordField.setSize(width,height);

            back.setLocation(x,passwordField.getY()+passwordField.getHeight()+spacing);
            login.setLocation(basePanel.getWidth()-x-buttonWidth-offset,back.getY());
            add(usernameField.getEncapsulator());//get a rounded text field
            add(passwordField.getEncapsulator());//ditto
            add(back);
            add(login);
        }
    };
    private javax.swing.JPanel changeCredentialsPanel=new javax.swing.JPanel(){
        int spacing=5;
        int width=170, height=25, rounding=5, offset=1;
        {
            addComponentListener(new java.awt.event.ComponentAdapter(){
                public void componentShown(java.awt.event.ComponentEvent e){
                    //change admincredentials or change user credentials
                    passwordField.setDefaultText(changeAdminCredentials?"New Admin Password":"New Regular Password");
                    usernameField.setDefaultText(changeAdminCredentials?"New Admin Username":"New Regular Username"/*checked to make sure this is atmost 20 chars*/);
                    passwordField.enableUserEntry(false);
                    passwordField.setText(passwordField.getDefaultText());
                    usernameField.enableUserEntry(true);
                    usernameField.setText(prefs.get(changeAdminCredentials?"adminusn":"userusn",""));
                }
            });
        }
        private MyTextField usernameField=new MyTextField(""/*nuffin*/,20,false){
            {
                setText(getDefaultText());
            }
            public void setBorder(javax.swing.border.Border border){
                //do nothing
            }
        };
        private MyTextField passwordField=new MyTextField("",20,true){
            {
                setText(getDefaultText());
                addActionListener(new java.awt.event.ActionListener(){
                    public void actionPerformed(java.awt.event.ActionEvent e){
                        change.doAction();
                    }
                });
            }
            public void setBorder(javax.swing.border.Border border){
                //do nothing
            }
        };
        int buttonWidth=55, buttonHeight=20;
        private PureButton back=new PureButton("Back",buttonWidth,buttonHeight,rounding,offset){
            public void doAction(){
                cardLayout.show(basePanel,"adminOptionsPanel");
            }
        };
        private PureButton change=new PureButton("Change",buttonWidth,buttonHeight,rounding,offset){
            public void doAction(){
                String username=usernameField.getString();
                String password=passwordField.getString();
                if(usernameField.getString().length()==0||passwordField.getString().length()==0){
                    javax.swing.JOptionPane.showMessageDialog(textInputPanel,"Username or password field is empty!","EMPTY FIELD",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                //get username and password
                if(changeAdminCredentials){
                    //set new admin credentials
                    prefs.put("adminusn",username);
                    prefs.put("adminpwd",password);
                    //show option pane telling this.
                    javax.swing.JOptionPane.showMessageDialog(changeCredentialsPanel,"New admin username and password has been set","SUCCESS",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }else{
                    //set new user credentials
                    prefs.put("userusn",username);
                    prefs.put("userpwd",password);
                    //show option pane telling this.
                    javax.swing.JOptionPane.showMessageDialog(changeCredentialsPanel,"New regular username and password has been set","SUCCESS",javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
                back.doAction();
            }
        };
        {
            setLayout(null);
            setOpaque(false);
            int x=(basePanel.getWidth()-width)>>1;
            int y=(basePanel.getHeight()-(height*2+buttonHeight+spacing*2))>>1;
            usernameField.setLocation(x,y);
            usernameField.setSize(width,height);
            passwordField.setLocation(x,usernameField.getY()+usernameField.getHeight()+spacing);
            passwordField.setSize(width,height);

            back.setLocation(x,passwordField.getY()+passwordField.getHeight()+spacing);
            change.setLocation(basePanel.getWidth()-x-buttonWidth-offset,back.getY());
            add(usernameField.getEncapsulator());//get a rounded text field
            add(passwordField.getEncapsulator());//ditto
            add(back);
            add(change);
        }
    };
    private javax.swing.JPanel credentialDisplayPanel=new javax.swing.JPanel(){
        int spacing=5;
        int x=14, y=16;
        private javax.swing.JPanel container=new javax.swing.JPanel();
        private javax.swing.JLabel usernameLabel=new javax.swing.JLabel("Username: ");
        private javax.swing.JLabel passwordLabel=new javax.swing.JLabel("Password: ");
        {
            usernameLabel.setFont(App.getFont("tahoma.ttf").deriveFont(14.0f));//set to tahoma font from App.getFont() method
            passwordLabel.setFont(App.getFont("tahoma.ttf").deriveFont(14.0f));//ditto
            usernameLabel.setSize(usernameLabel.getPreferredSize());
            passwordLabel.setSize(passwordLabel.getPreferredSize());
            addComponentListener(new java.awt.event.ComponentAdapter(){
                public void componentShown(java.awt.event.ComponentEvent e){
                    if(changeAdminCredentials){
                        usernameLabel.setText("Username: "+prefs.get("adminusn",""));
                        passwordLabel.setText("Password: "+prefs.get("adminpwd",""));
                    }else{
                        usernameLabel.setText("Username: "+prefs.get("userusn",""));
                        passwordLabel.setText("Password: "+prefs.get("userpwd",""));
                    }
                    usernameLabel.setSize(usernameLabel.getPreferredSize());
                    passwordLabel.setSize(passwordLabel.getPreferredSize());
                    if(Math.max(usernameLabel.getWidth(),passwordLabel.getWidth())>120){
                        width=Math.max(usernameLabel.getWidth(),passwordLabel.getWidth());
                        container.setSize(x+width+1+x,y+usernameLabel.getHeight()*2+20+spacing*2+y);
                        container.setLocation((basePanel.getWidth()-container.getWidth())>>1,(basePanel.getHeight()-container.getHeight())>>1);
                        validate();repaint();
                    }
                }
            });
        }
        private int width=Math.max(120,Math.max(usernameLabel.getWidth(),passwordLabel.getWidth())),
                height=25,
                rounding=5,
                offset=1;
        private PureButton back=new PureButton("Back",50,20,rounding,offset){
            public void doAction(){
                cardLayout.previous(basePanel);
            }
        };
        {
            setLayout(null);
            setOpaque(false);
            container.setLayout(null);
            container.setOpaque(false);
            container.setSize(x+width+1+x,y+usernameLabel.getHeight()*2+20+spacing*2+y);
            container.setLocation((basePanel.getWidth()-container.getWidth())>>1,(basePanel.getHeight()-container.getHeight())>>1);
            java.awt.Color labelBg=new java.awt.Color(200,255,0);
            usernameLabel.setBackground(labelBg);
            passwordLabel.setBackground(labelBg);
            usernameLabel.setOpaque(true);
            passwordLabel.setOpaque(true);
            usernameLabel.setLocation(x,y);
            passwordLabel.setLocation(x,usernameLabel.getY()+usernameLabel.getHeight()+spacing);
            back.setLocation(x,passwordLabel.getY()+passwordLabel.getHeight()+spacing);
            container.add(usernameLabel);
            container.add(passwordLabel);
            container.add(back);
            add(container);
        }
    };
}
