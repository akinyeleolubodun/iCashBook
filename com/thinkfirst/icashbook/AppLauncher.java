
package com.thinkfirst.icashbook;
/**
 * The Application
 * @author ESSIENNTA EMMANUEL
 * @version 1.0
 */
public class AppLauncher {    
    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("com.thinkfirst.icashbook.App", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    } // main()
}
