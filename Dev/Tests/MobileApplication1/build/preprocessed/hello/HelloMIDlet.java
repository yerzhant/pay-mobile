/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SecureConnection;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.pki.CertificateException;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

/**
 * @author ADevUser
 */
public class HelloMIDlet extends MIDlet implements CommandListener {

    private boolean midletPaused = false;
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Command okCommand1;
    private Command exitCommand;
    private Command itemCommand;
    private Command okCommand;
    private Form mainForm;
    private ImageItem imageItem;
    private ImageItem imageItem1;
    private StringItem stringItem;
    private Alert alert2;
    private Alert alert1;
    private Alert alert;
    private Image image4;
    private Image image5;
    private Image image3;
    private Image image1;
    private Image image2;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The HelloMIDlet constructor.
     */
    public HelloMIDlet() {
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|
    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        switchDisplayable(null, getMainForm());//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
        //switchDisplayable(getAlert(), getTextBox());
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == alert) {//GEN-BEGIN:|7-commandAction|1|141-preAction
            if (command == okCommand) {//GEN-END:|7-commandAction|1|141-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainForm());//GEN-LINE:|7-commandAction|2|141-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|3|140-preAction
        } else if (displayable == alert1) {
            if (command == okCommand) {//GEN-END:|7-commandAction|3|140-preAction
                // write pre-action user code here
                switchDisplayable(null, getMainForm());//GEN-LINE:|7-commandAction|4|140-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|5|19-preAction
        } else if (displayable == mainForm) {
            if (command == exitCommand) {//GEN-END:|7-commandAction|5|19-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|6|19-postAction
                // write post-action user code here
            } else if (command == itemCommand) {//GEN-LINE:|7-commandAction|7|27-preAction
                // write pre-action user code here
                //getConnected();
                //sendSMS();
                switchDisplayable(getAlert(), getMainForm());//GEN-LINE:|7-commandAction|8|27-postAction
                // write post-action user code here
            } else if (command == okCommand1) {//GEN-LINE:|7-commandAction|9|132-preAction
                // write pre-action user code here
                switchDisplayable(null, getAlert1());
//GEN-LINE:|7-commandAction|10|132-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|11|7-postCommandAction
        }//GEN-END:|7-commandAction|11|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|12|
    //</editor-fold>//GEN-END:|7-commandAction|12|





    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|18-getter|0|18-preInit
            // write pre-init user code here
            exitCommand = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|18-getter|1|18-postInit
            // write post-init user code here
        }//GEN-BEGIN:|18-getter|2|
        return exitCommand;
    }
    //</editor-fold>//GEN-END:|18-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: mainForm ">//GEN-BEGIN:|14-getter|0|14-preInit
    /**
     * Returns an initiliazed instance of mainForm component.
     * @return the initialized component instance
     */
    public Form getMainForm() {
        if (mainForm == null) {//GEN-END:|14-getter|0|14-preInit
            // write pre-init user code here
            mainForm = new Form("Main Form", new Item[] { getStringItem(), getImageItem(), getImageItem1() });//GEN-BEGIN:|14-getter|1|14-postInit
            mainForm.addCommand(getItemCommand());
            mainForm.addCommand(getExitCommand());
            mainForm.addCommand(getOkCommand1());
            mainForm.setCommandListener(this);//GEN-END:|14-getter|1|14-postInit
            // write post-init user code here
        }//GEN-BEGIN:|14-getter|2|
        return mainForm;
    }
    //</editor-fold>//GEN-END:|14-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommand ">//GEN-BEGIN:|26-getter|0|26-preInit
    /**
     * Returns an initiliazed instance of itemCommand component.
     * @return the initialized component instance
     */
    public Command getItemCommand() {
        if (itemCommand == null) {//GEN-END:|26-getter|0|26-preInit
            // write pre-init user code here
            itemCommand = new Command("Item", Command.ITEM, 0);//GEN-LINE:|26-getter|1|26-postInit
            // write post-init user code here
        }//GEN-BEGIN:|26-getter|2|
        return itemCommand;
    }
    //</editor-fold>//GEN-END:|26-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|28-getter|0|28-preInit
    /**
     * Returns an initiliazed instance of stringItem component.
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {//GEN-END:|28-getter|0|28-preInit
            // write pre-init user code here
            stringItem = new StringItem("Info: ", "The nil");//GEN-LINE:|28-getter|1|28-postInit
            // write post-init user code here
        }//GEN-BEGIN:|28-getter|2|
        return stringItem;
    }
    //</editor-fold>//GEN-END:|28-getter|2|








    //</editor-fold>























    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: alert ">//GEN-BEGIN:|115-getter|0|115-preInit
    /**
     * Returns an initiliazed instance of alert component.
     * @return the initialized component instance
     */
    public Alert getAlert() {
        if (alert == null) {//GEN-END:|115-getter|0|115-preInit
            // write pre-init user code here
            alert = new Alert("\u041E\u0448\u0438\u0431\u043A\u0430", "Some error text.", getImage1(), AlertType.ERROR);//GEN-BEGIN:|115-getter|1|115-postInit
            alert.addCommand(getOkCommand());
            alert.setCommandListener(this);
            alert.setTimeout(Alert.FOREVER);//GEN-END:|115-getter|1|115-postInit
            // write post-init user code here
        }//GEN-BEGIN:|115-getter|2|
        return alert;
    }
    //</editor-fold>//GEN-END:|115-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image1 ">//GEN-BEGIN:|116-getter|0|116-preInit
    /**
     * Returns an initiliazed instance of image1 component.
     * @return the initialized component instance
     */
    public Image getImage1() {
        if (image1 == null) {//GEN-END:|116-getter|0|116-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|116-getter|1|116-@java.io.IOException
                image1 = Image.createImage("/Error.png");
            } catch (java.io.IOException e) {//GEN-END:|116-getter|1|116-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|116-getter|2|116-postInit
            // write post-init user code here
        }//GEN-BEGIN:|116-getter|3|
        return image1;
    }
    //</editor-fold>//GEN-END:|116-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|119-getter|0|119-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|119-getter|0|119-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|119-getter|1|119-postInit
            // write post-init user code here
        }//GEN-BEGIN:|119-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|119-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: alert1 ">//GEN-BEGIN:|118-getter|0|118-preInit
    /**
     * Returns an initiliazed instance of alert1 component.
     * @return the initialized component instance
     */
    public Alert getAlert1() {
        if (alert1 == null) {//GEN-END:|118-getter|0|118-preInit
            // write pre-init user code here
            alert1 = new Alert("\u0418\u043D\u0444\u043E\u0440\u043C\u0430\u0446\u0438\u044F", "\u041D\u0435\u043A\u0430\u044F \u0438\u043D\u0444\u043E\u0440\u043C\u0430\u0446\u0438\u044F.", getImage2(), AlertType.INFO);//GEN-BEGIN:|118-getter|1|118-postInit
            alert1.addCommand(getOkCommand());
            alert1.setCommandListener(this);
            alert1.setTimeout(Alert.FOREVER);//GEN-END:|118-getter|1|118-postInit
            // write post-init user code here
        }//GEN-BEGIN:|118-getter|2|
        return alert1;
    }
    //</editor-fold>//GEN-END:|118-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image2 ">//GEN-BEGIN:|121-getter|0|121-preInit
    /**
     * Returns an initiliazed instance of image2 component.
     * @return the initialized component instance
     */
    public Image getImage2() {
        if (image2 == null) {//GEN-END:|121-getter|0|121-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|121-getter|1|121-@java.io.IOException
                image2 = Image.createImage("/Info.png");
            } catch (java.io.IOException e) {//GEN-END:|121-getter|1|121-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|121-getter|2|121-postInit
            // write post-init user code here
        }//GEN-BEGIN:|121-getter|3|
        return image2;
    }
    //</editor-fold>//GEN-END:|121-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand1 ">//GEN-BEGIN:|131-getter|0|131-preInit
    /**
     * Returns an initiliazed instance of okCommand1 component.
     * @return the initialized component instance
     */
    public Command getOkCommand1() {
        if (okCommand1 == null) {//GEN-END:|131-getter|0|131-preInit
            // write pre-init user code here
            okCommand1 = new Command("Ok", Command.OK, 0);//GEN-LINE:|131-getter|1|131-postInit
            // write post-init user code here
        }//GEN-BEGIN:|131-getter|2|
        return okCommand1;
    }
    //</editor-fold>//GEN-END:|131-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: alert2 ">//GEN-BEGIN:|134-getter|0|134-preInit
    /**
     * Returns an initiliazed instance of alert2 component.
     * @return the initialized component instance
     */
    public Alert getAlert2() {
        if (alert2 == null) {//GEN-END:|134-getter|0|134-preInit
            // write pre-init user code here
            alert2 = new Alert("", null, getImage3(), null);//GEN-BEGIN:|134-getter|1|134-postInit
            alert2.setTimeout(1);//GEN-END:|134-getter|1|134-postInit
            // write post-init user code here
        }//GEN-BEGIN:|134-getter|2|
        return alert2;
    }
    //</editor-fold>//GEN-END:|134-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image3 ">//GEN-BEGIN:|135-getter|0|135-preInit
    /**
     * Returns an initiliazed instance of image3 component.
     * @return the initialized component instance
     */
    public Image getImage3() {
        if (image3 == null) {//GEN-END:|135-getter|0|135-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|135-getter|1|135-@java.io.IOException
                image3 = Image.createImage("/Logo.png");
            } catch (java.io.IOException e) {//GEN-END:|135-getter|1|135-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|135-getter|2|135-postInit
            // write post-init user code here
        }//GEN-BEGIN:|135-getter|3|
        return image3;
    }
    //</editor-fold>//GEN-END:|135-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageItem ">//GEN-BEGIN:|136-getter|0|136-preInit
    /**
     * Returns an initiliazed instance of imageItem component.
     * @return the initialized component instance
     */
    public ImageItem getImageItem() {
        if (imageItem == null) {//GEN-END:|136-getter|0|136-preInit
            // write pre-init user code here
            imageItem = new ImageItem("", getImage4(), ImageItem.LAYOUT_DEFAULT, "<Missing Image>");//GEN-LINE:|136-getter|1|136-postInit
            // write post-init user code here
        }//GEN-BEGIN:|136-getter|2|
        return imageItem;
    }
    //</editor-fold>//GEN-END:|136-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: imageItem1 ">//GEN-BEGIN:|138-getter|0|138-preInit
    /**
     * Returns an initiliazed instance of imageItem1 component.
     * @return the initialized component instance
     */
    public ImageItem getImageItem1() {
        if (imageItem1 == null) {//GEN-END:|138-getter|0|138-preInit
            // write pre-init user code here
            imageItem1 = new ImageItem("", getImage5(), ImageItem.LAYOUT_DEFAULT, "<Missing Image>");//GEN-LINE:|138-getter|1|138-postInit
            // write post-init user code here
        }//GEN-BEGIN:|138-getter|2|
        return imageItem1;
    }
    //</editor-fold>//GEN-END:|138-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image4 ">//GEN-BEGIN:|137-getter|0|137-preInit
    /**
     * Returns an initiliazed instance of image4 component.
     * @return the initialized component instance
     */
    public Image getImage4() {
        if (image4 == null) {//GEN-END:|137-getter|0|137-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|137-getter|1|137-@java.io.IOException
                image4 = Image.createImage("/10.png");
            } catch (java.io.IOException e) {//GEN-END:|137-getter|1|137-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|137-getter|2|137-postInit
            // write post-init user code here
        }//GEN-BEGIN:|137-getter|3|
        return image4;
    }
    //</editor-fold>//GEN-END:|137-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image5 ">//GEN-BEGIN:|139-getter|0|139-preInit
    /**
     * Returns an initiliazed instance of image5 component.
     * @return the initialized component instance
     */
    public Image getImage5() {
        if (image5 == null) {//GEN-END:|139-getter|0|139-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|139-getter|1|139-@java.io.IOException
                image5 = Image.createImage("/8.png");
            } catch (java.io.IOException e) {//GEN-END:|139-getter|1|139-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|139-getter|2|139-postInit
            // write post-init user code here
        }//GEN-BEGIN:|139-getter|3|
        return image5;
    }
    //</editor-fold>//GEN-END:|139-getter|3|

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }

    private void getConnected() {
        //stringItem.setText("Hi\nand\nhifdj  fjklj ;lkjf; ;lfjjal; jflk j;lkfjafjasfjjjjjjjkl jasjfjsfjsfj         asssssljjjjjjjjjjjjjjjjjjj123");
        try {
            SecureConnection sc = (SecureConnection) Connector.open("ssl://www.entrust.net:443");
//            HttpConnection sc = (HttpConnection) Connector.open("http://www.paysoft.kz");
//            HttpsConnection sc = (HttpsConnection) Connector.open("https://192.168.1.54");

            //sc.setRequestProperty("Connection", "Keep-Alive");
            System.out.println("Open");
            DataOutputStream os = sc.openDataOutputStream();
            System.out.println("os1");
            os.write("GET / HTTP/1.1\n".getBytes());
            os.write("Host: www.entrust.net\n\n".getBytes());
//            os.writeByte(0x11);
//            os.writeByte(0x11);
//            os.writeByte(0x11);
//            os.writeByte(0x11);
//            os.writeByte(0x11);
            os.flush();

            DataInputStream is = sc.openDataInputStream();
            System.out.println("is1");
//            long l = sc.getLength();
//            System.out.println(l);

            String str1 = "";
//            str1 += is.readByte() + " ";
//            str1 += is.readByte() + " ";
//            str1 += is.readByte() + " ";
//            str1 += is.readByte() + " ";
            for (int i = 0; i < 256; i++) {
                System.out.print((char)is.readByte());
            }

            System.out.println("os2");
//            os.writeByte(0x14);
//            os.writeByte(0x15);
//            os.writeByte(0x16);
//            os.writeByte(0x17);
//            os.writeByte(0x18);
//            os.writeByte(0x19);
//            os.writeByte(0x20);
//            os.flush();
            //OutputStream os = sc.openOutputStream();

            //os.write("GET / HTTP/1.0\r\n\r\n".getBytes());
            //os.flush();

            /*StringBuffer s = new StringBuffer();
            //InputStreamReader in = new InputStreamReader(is);
            do {
            byte b = is.readByte();
            s.append((char) b);
            } while (is.available() > 0);
             */
            stringItem.setText(String.valueOf(val++) + ": " + str1);//s.toString());
            //in.close();
            System.out.println("Close:");
            is.close();
            System.out.println("is");
            os.close();
            System.out.println("os");
            sc.close();
            System.out.println("sc");
        } catch (CertificateException ex) {
            ex.printStackTrace();
            stringItem.setText("Cert Ex: " + ex.getMessage());
        } catch (ConnectionNotFoundException ex) {
            ex.printStackTrace();
            stringItem.setText("CNF Ex: " + ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            stringItem.setText("IO Ex: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            stringItem.setText("Ex: " + ex.getMessage());
        }
    }
    private int val = 0;

    private void sendSMS() {
        try {
            MessageConnection conn = (MessageConnection) Connector.open("sms://77770280439", Connector.WRITE);
            TextMessage text = (TextMessage) conn.newMessage(MessageConnection.TEXT_MESSAGE);
            text.setPayloadText("Hi there!");
            conn.send(text);
            conn.close();
            conn = null;
        } catch (Exception ex) {
            stringItem.setText("Ex: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
