/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: P.
 * Last error index: 003.
 *
 * 2010.03.18   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * @author Yerzhan Tulepov.
 */
public final class PayMobile extends MIDlet implements CommandListener, ItemStateListener, Runnable, RecordComparator, RecordFilter {

    private static final String CURRENCIES[] = {"KZT", "USD", "EUR", "GBP", "JPY", "CHF", "RUR", "KGS", "AUD", "CAD"};
    private static final String COPYRIGHT_YEAR = "2010";
    private static final String P_002_MESSAGE = "Ошибка среды исполнения.";
    private static final String MISCELLANEOUS_STORE = "m";
    private static final String SOURCE_ACCOUNTS_STORE = "s";
    private static final String DESTINATION_ACCOUNTS_STORE = "d";
    private static final String PRESET_TRANSFERS_STORE = "p";
    private static final String ERROR_TITLE = "Ошибка";
    private static final String INFO_TITLE = "Сообщение";
    private static final String INCORRECT_PHONE_NUMBER = "Неверный № телефона.";
    private static final String INCORRECT_CSI_NUMBER = "Неверный № заявки.";
    private static final String PASSWORDS_ARE_DIFFERENT = "Пароли не совпадают.";
    private static final String PASSWORD_VALIDATION_ERROR = "Пароль не удовлетворяет требованиям банка.";
    private static final String REQUEST_PROCESSING_TIMED_OUT = "Время ожидания ответа истекло. Вы можете отменить дальнейшее ожидание.";
    private static final String STMT_TYPE_LAST_OPERATIONS = "Последние операции";
    private static final String STMT_TYPE_THIS_MONTH = "За текущий месяц";
    private static final String STMT_TYPE_PREVIOUS_MONTH = "За предыдущий месяц";
    private static final int REQUEST_PROCESSING_SLEEP = 400;
    private static final int REQUEST_PROCESSING_TIMEOUT = 90 * 1000 / REQUEST_PROCESSING_SLEEP; // 90 sec.
    private final String ABOUT_TEXT;
    private boolean midletPaused = false;
    private boolean initializationError = false;
    private boolean noMK = true;
    private boolean noAccounts = true;
    private boolean mustExit = true;
    static final Hashtable statementTypes = new Hashtable();
    static final String PASSWORD_IS_SHORT_PREFIX = "Короткий пароль. Минимальная длина ";
    static final String PASSWORD_IS_SHORT_SUFFIX = " символа.";
    static final byte MAX_PASSWORD_LENGTH = 127;
    static final byte MIN_PASSWORD_LENGTH = 4;
    static final byte PASSWORD_ACCEPTED = 0;
    static final byte PASSWORD_ERROR = 1;
    static final byte ANY_OTHER_ERROR = 2;
    static Thread requestProcessingThread;
    static PayMobile payMobile;
    static RecordStore miscellaneousStore, sourceAccoutsStore, destinationAccountsStore, presetTransfersStore;
    static String presetTransferSearchName;
    static byte passwordInitResult;
    final Operations operations = new Operations(this);
    final Setup setup = new Setup(this);
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form requestProcessingForm;
    private Gauge requestProcessingGauge;
    private StringItem requestProcessingStringItem;
    private Alert splashAlert;
    private Alert errorAlert;
    private Alert generalAlert;
    private List operationsList;
    private Alert aboutAlert;
    private Form passwordInitForm;
    private TextField passwordTextField;
    private ChoiceGroup showPasswordChoiceGroup;
    private TextField passwordCheckTextField;
    private TextField phoneNumberTextField;
    private TextField csiTextField;
    private Alert responseAlert;
    private Command okCommand;
    private Command aboutCommand;
    private Command exitCommand;
    private Command settingsCommand;
    private Command cancelCommand;
    private Image splashImage;
    private Image errorImage;
    private Image infoImage;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The PayMobile constructor.
     */
    public PayMobile() {
        payMobile = this;
        ABOUT_TEXT = getAppProperty("MIDlet-Name") + " " + getAppProperty("MIDlet-Version") + ".\nCopyright (C) " + COPYRIGHT_YEAR + " "
                + getAppProperty("MIDlet-Vendor") + "\nAll rights reserved.";
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
        try {
            if (miscellaneousStore == null && sourceAccoutsStore == null && destinationAccountsStore == null && presetTransfersStore == null) {
                miscellaneousStore = RecordStore.openRecordStore(MISCELLANEOUS_STORE, true);
                sourceAccoutsStore = RecordStore.openRecordStore(SOURCE_ACCOUNTS_STORE, true);
                destinationAccountsStore = RecordStore.openRecordStore(DESTINATION_ACCOUNTS_STORE, true);
                presetTransfersStore = RecordStore.openRecordStore(PRESET_TRANSFERS_STORE, true);

//#mdebug
////#define INIT_MISC
////#define INIT_ACC

//#ifdef INIT_MISC
//#                 Service.debugInit();
//#endif
//#ifdef INIT_ACC
//#                 UpdateAccounts.debugInit();
//#endif
//#enddebug

                noMK = miscellaneousStore.getNumRecords() == 0 ? true : false;
                noAccounts = sourceAccoutsStore.getNumRecords() == 0 ? true : false;

                statementTypes.put(STMT_TYPE_LAST_OPERATIONS, new Byte((byte) 0));
                operations.getStatementTypesChoiceGroup().append(STMT_TYPE_LAST_OPERATIONS, null);
                statementTypes.put(STMT_TYPE_THIS_MONTH, new Byte((byte) 1));
                operations.getStatementTypesChoiceGroup().append(STMT_TYPE_THIS_MONTH, null);
                statementTypes.put(STMT_TYPE_PREVIOUS_MONTH, new Byte((byte) 2));
                operations.getStatementTypesChoiceGroup().append(STMT_TYPE_PREVIOUS_MONTH, null);

                operations.init();
                setup.init();
                Service.init();
            } else {
                initializationError = true;
                getErrorAlert().setString("P-002\n" + P_002_MESSAGE);
            }
        } catch (RecordStoreException ex) {
            initializationError = true;
            getErrorAlert().setString("P-001\n" + ex.getMessage());
        } catch (IOException ex) {
            initializationError = true;
            getErrorAlert().setString("P-003\n" + ex.getMessage());
        }
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
        initializationError();//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashImage ">//GEN-BEGIN:|35-getter|0|35-preInit
    /**
     * Returns an initiliazed instance of splashImage component.
     * @return the initialized component instance
     */
    public Image getSplashImage() {
        if (splashImage == null) {//GEN-END:|35-getter|0|35-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|35-getter|1|35-@java.io.IOException
                splashImage = Image.createImage("/kz/paysoft/paymobile/midlet/PayMobile.png");
            } catch (java.io.IOException e) {//GEN-END:|35-getter|1|35-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|35-getter|2|35-postInit
            // write post-init user code here
        }//GEN-BEGIN:|35-getter|3|
        return splashImage;
    }
    //</editor-fold>//GEN-END:|35-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: splashAlert ">//GEN-BEGIN:|36-getter|0|36-preInit
    /**
     * Returns an initiliazed instance of splashAlert component.
     * @return the initialized component instance
     */
    public Alert getSplashAlert() {
        if (splashAlert == null) {//GEN-END:|36-getter|0|36-preInit
            // write pre-init user code here
            splashAlert = new Alert(null, null, getSplashImage(), null);//GEN-BEGIN:|36-getter|1|36-postInit
            splashAlert.addCommand(getOkCommand());
            splashAlert.setCommandListener(this);
            splashAlert.setTimeout(3000);//GEN-END:|36-getter|1|36-postInit
            // write post-init user code here
        }//GEN-BEGIN:|36-getter|2|
        return splashAlert;
    }
    //</editor-fold>//GEN-END:|36-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|38-getter|0|38-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|38-getter|0|38-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|38-getter|1|38-postInit
            // write post-init user code here
        }//GEN-BEGIN:|38-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|38-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == errorAlert) {//GEN-BEGIN:|7-commandAction|1|81-preAction
            if (command == exitCommand) {//GEN-END:|7-commandAction|1|81-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|2|81-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|3|72-preAction
        } else if (displayable == operationsList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|3|72-preAction
                // write pre-action user code here
                operationsListAction();//GEN-LINE:|7-commandAction|4|72-postAction
                // write post-action user code here
            } else if (command == aboutCommand) {//GEN-LINE:|7-commandAction|5|87-preAction
                // write pre-action user code here
                switchDisplayable(getAboutAlert(), getOperationsList());//GEN-LINE:|7-commandAction|6|87-postAction
                // write post-action user code here
            } else if (command == exitCommand) {//GEN-LINE:|7-commandAction|7|84-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|8|84-postAction
                // write post-action user code here
            } else if (command == settingsCommand) {//GEN-LINE:|7-commandAction|9|93-preAction
                // write pre-action user code here
                setup.settingsListEntry();//GEN-LINE:|7-commandAction|10|93-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|11|110-preAction
        } else if (displayable == passwordInitForm) {
            if (command == exitCommand) {//GEN-END:|7-commandAction|11|110-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|12|110-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|13|114-preAction
                // write pre-action user code here
                if (phoneNumberTextField.getString().equals("") || Long.parseLong(phoneNumberTextField.getString()) < 1) {
                    switchDisplayable(getGeneralAlertAsError(INCORRECT_PHONE_NUMBER), getPasswordInitForm());
                } else if (csiTextField.getString().equals("") || Long.parseLong(csiTextField.getString()) < 1) {
                    switchDisplayable(getGeneralAlertAsError(INCORRECT_CSI_NUMBER), getPasswordInitForm());
                } else if (passwordTextField.size() < MIN_PASSWORD_LENGTH) {
                    switchDisplayable(getGeneralAlertAsError(PASSWORD_IS_SHORT_PREFIX + " " + MIN_PASSWORD_LENGTH + PASSWORD_IS_SHORT_SUFFIX), getPasswordInitForm());
                } else if (passwordTextField.size() != passwordCheckTextField.size()) {
                    switchDisplayable(getGeneralAlertAsError(PASSWORDS_ARE_DIFFERENT), getPasswordInitForm());
                } else {
                    boolean areEqual = true;
                    Service.password = new char[passwordTextField.size()];
                    char passwordCheck[] = new char[Service.password.length];
                    passwordTextField.getChars(Service.password);
                    passwordCheckTextField.getChars(passwordCheck);
                    for (int i = 0; i < Service.password.length; i++) {
                        if (Service.password[i] != passwordCheck[i]) {
                            clearBuffer(Service.password);
                            clearBuffer(passwordCheck);  // Not to be removed, as switchDisplayable may throw an exception.
                            areEqual = false;
                            switchDisplayable(getGeneralAlertAsError(PASSWORDS_ARE_DIFFERENT), getPasswordInitForm());
                            break;
                        }
                    }

                    if (areEqual) {
                        clearBuffer(passwordCheck);
                        clearTextField(passwordTextField);
                        clearTextField(passwordCheckTextField);
                        Service.phoneNumber = Long.parseLong(phoneNumberTextField.getString());
                        Service.csiNumber = Long.parseLong(csiTextField.getString());
                        showRequestProcessingForm();
                        new InitPassword().start();
                    }
//GEN-LINE:|7-commandAction|14|114-postAction
                }
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|15|309-preAction
        } else if (displayable == requestProcessingForm) {
            if (command == cancelCommand) {//GEN-END:|7-commandAction|15|309-preAction
                // write pre-action user code here
                requestProcessingThread.interrupt();
                Service.cleanUpAndDisconnect();
                mustExit();//GEN-LINE:|7-commandAction|16|309-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|17|212-preAction
        } else if (displayable == responseAlert) {
            if (command == okCommand) {//GEN-END:|7-commandAction|17|212-preAction
                // write pre-action user code here
                mustExit();//GEN-LINE:|7-commandAction|18|212-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|19|50-preAction
        } else if (displayable == splashAlert) {
            if (command == okCommand) {//GEN-END:|7-commandAction|19|50-preAction
                // write pre-action user code here
                noMK();//GEN-LINE:|7-commandAction|20|50-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|21|7-postCommandAction
        }//GEN-END:|7-commandAction|21|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|22|
    //</editor-fold>//GEN-END:|7-commandAction|22|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initializationError ">//GEN-BEGIN:|43-if|0|43-preIf
    /**
     * Performs an action assigned to the initializationError if-point.
     */
    public void initializationError() {//GEN-END:|43-if|0|43-preIf
        // enter pre-if user code here
        if (initializationError) {//GEN-LINE:|43-if|1|44-preAction
            // write pre-action user code here
            switchDisplayable(null, getErrorAlert());//GEN-LINE:|43-if|2|44-postAction
            // write post-action user code here
        } else {//GEN-LINE:|43-if|3|45-preAction
            // write pre-action user code here
            switchDisplayable(null, getSplashAlert());//GEN-LINE:|43-if|4|45-postAction
            // write post-action user code here
        }//GEN-LINE:|43-if|5|43-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|43-if|6|
    //</editor-fold>//GEN-END:|43-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: errorAlert ">//GEN-BEGIN:|46-getter|0|46-preInit
    /**
     * Returns an initiliazed instance of errorAlert component.
     * @return the initialized component instance
     */
    public Alert getErrorAlert() {
        if (errorAlert == null) {//GEN-END:|46-getter|0|46-preInit
            // write pre-init user code here
            errorAlert = new Alert("\u041E\u0448\u0438\u0431\u043A\u0430", "\u041D\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043D\u0430\u044F \u043E\u0448\u0438\u0431\u043A\u0430", getErrorImage(), AlertType.ERROR);//GEN-BEGIN:|46-getter|1|46-postInit
            errorAlert.addCommand(getExitCommand());
            errorAlert.setCommandListener(this);
            errorAlert.setTimeout(Alert.FOREVER);//GEN-END:|46-getter|1|46-postInit
            // write post-init user code here
        }//GEN-BEGIN:|46-getter|2|
        return errorAlert;
    }
    //</editor-fold>//GEN-END:|46-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: operationsList ">//GEN-BEGIN:|70-getter|0|70-preInit
    /**
     * Returns an initiliazed instance of operationsList component.
     * @return the initialized component instance
     */
    public List getOperationsList() {
        if (operationsList == null) {//GEN-END:|70-getter|0|70-preInit
            // write pre-init user code here
            operationsList = new List("\u041E\u043F\u0435\u0440\u0430\u0446\u0438\u0438", Choice.IMPLICIT);//GEN-BEGIN:|70-getter|1|70-postInit
            operationsList.append("\u041F\u0435\u0440\u0435\u0432\u043E\u0434", null);
            operationsList.append("\u041F\u0440\u0435\u0434\u0443\u0441\u0442-\u044B\u0435 \u043F\u0435\u0440\u0435\u0432\u043E\u0434\u044B", null);
            operationsList.append("\u0412\u044B\u043F\u0438\u0441\u043A\u0430", null);
            operationsList.append("\u041E\u0441\u0442\u0430\u0442\u043E\u043A", null);
            operationsList.addCommand(getExitCommand());
            operationsList.addCommand(getSettingsCommand());
            operationsList.addCommand(getAboutCommand());
            operationsList.setCommandListener(this);
            operationsList.setSelectedFlags(new boolean[] { false, false, false, false });//GEN-END:|70-getter|1|70-postInit
            // write post-init user code here
        }//GEN-BEGIN:|70-getter|2|
        return operationsList;
    }
    //</editor-fold>//GEN-END:|70-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: operationsListAction ">//GEN-BEGIN:|70-action|0|70-preAction
    /**
     * Performs an action assigned to the selected list element in the operationsList component.
     */
    public void operationsListAction() {//GEN-END:|70-action|0|70-preAction
        // enter pre-action user code here
        String __selectedString = getOperationsList().getString(getOperationsList().getSelectedIndex());//GEN-BEGIN:|70-action|1|94-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("\u041F\u0435\u0440\u0435\u0432\u043E\u0434")) {//GEN-END:|70-action|1|94-preAction
                // write pre-action user code here
                operations.transferFormEntry();//GEN-LINE:|70-action|2|94-postAction
                // write post-action user code here
            } else if (__selectedString.equals("\u041F\u0440\u0435\u0434\u0443\u0441\u0442-\u044B\u0435 \u043F\u0435\u0440\u0435\u0432\u043E\u0434\u044B")) {//GEN-LINE:|70-action|3|95-preAction
                // write pre-action user code here
                operations.presetTransfersListEntry();//GEN-LINE:|70-action|4|95-postAction
                // write post-action user code here
            } else if (__selectedString.equals("\u0412\u044B\u043F\u0438\u0441\u043A\u0430")) {//GEN-LINE:|70-action|5|96-preAction
                // write pre-action user code here
                operations.statementFormEntry();//GEN-LINE:|70-action|6|96-postAction
                // write post-action user code here
            } else if (__selectedString.equals("\u041E\u0441\u0442\u0430\u0442\u043E\u043A")) {//GEN-LINE:|70-action|7|97-preAction
                // write pre-action user code here
                operations.balanseFormEntry();//GEN-LINE:|70-action|8|97-postAction
                // write post-action user code here
            }//GEN-BEGIN:|70-action|9|70-postAction
        }//GEN-END:|70-action|9|70-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|70-action|10|
    //</editor-fold>//GEN-END:|70-action|10|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|80-getter|0|80-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|80-getter|0|80-preInit
            // write pre-init user code here
            exitCommand = new Command("\u0412\u044B\u0445\u043E\u0434", Command.EXIT, 0);//GEN-LINE:|80-getter|1|80-postInit
            // write post-init user code here
        }//GEN-BEGIN:|80-getter|2|
        return exitCommand;
    }
    //</editor-fold>//GEN-END:|80-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aboutAlert ">//GEN-BEGIN:|88-getter|0|88-preInit
    /**
     * Returns an initiliazed instance of aboutAlert component.
     * @return the initialized component instance
     */
    public Alert getAboutAlert() {
        if (aboutAlert == null) {//GEN-END:|88-getter|0|88-preInit
            // write pre-init user code here
            aboutAlert = new Alert("\u041E \u043F\u0440\u043E\u0433\u0440\u0430\u043C\u043C\u0435", ABOUT_TEXT, getSplashImage(), null);//GEN-BEGIN:|88-getter|1|88-postInit
            aboutAlert.setTimeout(Alert.FOREVER);//GEN-END:|88-getter|1|88-postInit
            // write post-init user code here
        }//GEN-BEGIN:|88-getter|2|
        return aboutAlert;
    }
    //</editor-fold>//GEN-END:|88-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: aboutCommand ">//GEN-BEGIN:|86-getter|0|86-preInit
    /**
     * Returns an initiliazed instance of aboutCommand component.
     * @return the initialized component instance
     */
    public Command getAboutCommand() {
        if (aboutCommand == null) {//GEN-END:|86-getter|0|86-preInit
            // write pre-init user code here
            aboutCommand = new Command("\u041E \u043F\u0440\u043E\u0433\u0440\u0430\u043C\u043C\u0435", Command.SCREEN, 1);//GEN-LINE:|86-getter|1|86-postInit
            // write post-init user code here
        }//GEN-BEGIN:|86-getter|2|
        return aboutCommand;
    }
    //</editor-fold>//GEN-END:|86-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: errorImage ">//GEN-BEGIN:|91-getter|0|91-preInit
    /**
     * Returns an initiliazed instance of errorImage component.
     * @return the initialized component instance
     */
    public Image getErrorImage() {
        if (errorImage == null) {//GEN-END:|91-getter|0|91-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|91-getter|1|91-@java.io.IOException
                errorImage = Image.createImage("/kz/paysoft/paymobile/midlet/Error.png");
            } catch (java.io.IOException e) {//GEN-END:|91-getter|1|91-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|91-getter|2|91-postInit
            // write post-init user code here
        }//GEN-BEGIN:|91-getter|3|
        return errorImage;
    }
    //</editor-fold>//GEN-END:|91-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: settingsCommand ">//GEN-BEGIN:|92-getter|0|92-preInit
    /**
     * Returns an initiliazed instance of settingsCommand component.
     * @return the initialized component instance
     */
    public Command getSettingsCommand() {
        if (settingsCommand == null) {//GEN-END:|92-getter|0|92-preInit
            // write pre-init user code here
            settingsCommand = new Command("\u041D\u0430\u0441\u0442\u0440\u043E\u0439\u043A\u0438", Command.SCREEN, 0);//GEN-LINE:|92-getter|1|92-postInit
            // write post-init user code here
        }//GEN-BEGIN:|92-getter|2|
        return settingsCommand;
    }
    //</editor-fold>//GEN-END:|92-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: noMK ">//GEN-BEGIN:|98-if|0|98-preIf
    /**
     * Performs an action assigned to the noMK if-point.
     */
    public void noMK() {//GEN-END:|98-if|0|98-preIf
        // enter pre-if user code here
        if (noMK) {//GEN-LINE:|98-if|1|99-preAction
            // write pre-action user code here
            switchDisplayable(null, getPasswordInitForm());//GEN-LINE:|98-if|2|99-postAction
            // write post-action user code here
        } else {//GEN-LINE:|98-if|3|100-preAction
            // write pre-action user code here
            mustExit = false;
            noAccounts();//GEN-LINE:|98-if|4|100-postAction
            // write post-action user code here
        }//GEN-LINE:|98-if|5|98-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|98-if|6|
    //</editor-fold>//GEN-END:|98-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: noAccounts ">//GEN-BEGIN:|103-if|0|103-preIf
    /**
     * Performs an action assigned to the noAccounts if-point.
     */
    public void noAccounts() {//GEN-END:|103-if|0|103-preIf
        // enter pre-if user code here
        if (noAccounts) {//GEN-LINE:|103-if|1|104-preAction
            // write pre-action user code here
            Operations.returnToSetup = false;//GEN-BEGIN:|103-if|2|104-postAction
            Operations.currentOperation = Operations.UPDATE_ACCOUNTS;
            operations.passwordFormEntry();//GEN-END:|103-if|2|104-postAction
            // write post-action user code here
        } else {//GEN-LINE:|103-if|3|105-preAction
            // write pre-action user code here
            switchDisplayable(null, getOperationsList());//GEN-LINE:|103-if|4|105-postAction
            // write post-action user code here
        }//GEN-LINE:|103-if|5|103-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|103-if|6|
    //</editor-fold>//GEN-END:|103-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: passwordInitForm ">//GEN-BEGIN:|108-getter|0|108-preInit
    /**
     * Returns an initiliazed instance of passwordInitForm component.
     * @return the initialized component instance
     */
    public Form getPasswordInitForm() {
        if (passwordInitForm == null) {//GEN-END:|108-getter|0|108-preInit
            // write pre-init user code here
            passwordInitForm = new Form("\u0418\u043D\u0438\u0446\u0438\u0430\u043B\u0438\u0437\u0430\u0446\u0438\u044F", new Item[] { getPhoneNumberTextField(), getCsiTextField(), getPasswordTextField(), getPasswordCheckTextField(), getShowPasswordChoiceGroup() });//GEN-BEGIN:|108-getter|1|108-postInit
            passwordInitForm.addCommand(getExitCommand());
            passwordInitForm.addCommand(getOkCommand());
            passwordInitForm.setCommandListener(this);//GEN-END:|108-getter|1|108-postInit
            // write post-init user code here
            passwordInitForm.setItemStateListener(this);
        }//GEN-BEGIN:|108-getter|2|
        return passwordInitForm;
    }
    //</editor-fold>//GEN-END:|108-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand ">//GEN-BEGIN:|125-getter|0|125-preInit
    /**
     * Returns an initiliazed instance of cancelCommand component.
     * @return the initialized component instance
     */
    public Command getCancelCommand() {
        if (cancelCommand == null) {//GEN-END:|125-getter|0|125-preInit
            // write pre-init user code here
            cancelCommand = new Command("\u041E\u0442\u043C\u0435\u043D\u0430", Command.CANCEL, 0);//GEN-LINE:|125-getter|1|125-postInit
            // write post-init user code here
        }//GEN-BEGIN:|125-getter|2|
        return cancelCommand;
    }
    //</editor-fold>//GEN-END:|125-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: responseAlert ">//GEN-BEGIN:|129-getter|0|129-preInit
    /**
     * Returns an initiliazed instance of responseAlert component.
     * @return the initialized component instance
     */
    public Alert getResponseAlert() {
        if (responseAlert == null) {//GEN-END:|129-getter|0|129-preInit
            // write pre-init user code here
            responseAlert = new Alert("\u0421\u043E\u043E\u0431\u0449\u0435\u043D\u0438\u0435", null, null, AlertType.INFO);//GEN-BEGIN:|129-getter|1|129-postInit
            responseAlert.addCommand(getOkCommand());
            responseAlert.setCommandListener(this);
            responseAlert.setTimeout(Alert.FOREVER);//GEN-END:|129-getter|1|129-postInit
            // write post-init user code here
        }//GEN-BEGIN:|129-getter|2|
        return responseAlert;
    }
    //</editor-fold>//GEN-END:|129-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: mustExit ">//GEN-BEGIN:|175-if|0|175-preIf
    /**
     * Performs an action assigned to the mustExit if-point.
     */
    public void mustExit() {//GEN-END:|175-if|0|175-preIf
        // enter pre-if user code here
        if (mustExit) {//GEN-LINE:|175-if|1|176-preAction
            // write pre-action user code here
            exitMIDlet();//GEN-LINE:|175-if|2|176-postAction
            // write post-action user code here
        } else {//GEN-LINE:|175-if|3|177-preAction
            // write pre-action user code here
            switchDisplayable(null, getOperationsList());//GEN-LINE:|175-if|4|177-postAction
            // write post-action user code here
        }//GEN-LINE:|175-if|5|175-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|175-if|6|
    //</editor-fold>//GEN-END:|175-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: passwordInitResult ">//GEN-BEGIN:|240-switch|0|240-preSwitch
    /**
     * Performs an action assigned to the passwordInitResult switch-point.
     */
    public void passwordInitResult() {//GEN-END:|240-switch|0|240-preSwitch
        // enter pre-switch user code here
        switch (passwordInitResult) {//GEN-BEGIN:|240-switch|1|243-preAction
            case ANY_OTHER_ERROR://GEN-END:|240-switch|1|243-preAction
                // write pre-action user code here
                switchDisplayable(null, getErrorAlert());//GEN-LINE:|240-switch|2|243-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|240-switch|3|242-preAction
            case PASSWORD_ERROR://GEN-END:|240-switch|3|242-preAction
                // write pre-action user code here
                phoneNumberTextField.setConstraints(phoneNumberTextField.getConstraints() | TextField.UNEDITABLE);
                csiTextField.setConstraints(csiTextField.getConstraints() | TextField.UNEDITABLE);
                switchDisplayable(getGeneralAlertAsError(PASSWORD_VALIDATION_ERROR), getPasswordInitForm());
                /*
switchDisplayable (null, getPasswordInitForm ());//GEN-LINE:|240-switch|4|242-postAction
                 */
                // write post-action user code here
                break;//GEN-BEGIN:|240-switch|5|241-preAction
            case PASSWORD_ACCEPTED://GEN-END:|240-switch|5|241-preAction
                // write pre-action user code here
                switchDisplayable(null, getResponseAlert());//GEN-LINE:|240-switch|6|241-postAction
                // write post-action user code here
                break;//GEN-BEGIN:|240-switch|7|240-postSwitch
        }//GEN-END:|240-switch|7|240-postSwitch
        // enter post-switch user code here
    }//GEN-BEGIN:|240-switch|8|
    //</editor-fold>//GEN-END:|240-switch|8|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: operationsListEntry ">//GEN-BEGIN:|277-entry|0|278-preAction
    /**
     * Performs an action assigned to the operationsListEntry entry-point.
     */
    public void operationsListEntry() {//GEN-END:|277-entry|0|278-preAction
        // write pre-action user code here
        switchDisplayable(null, getOperationsList());//GEN-LINE:|277-entry|1|278-postAction
        // write post-action user code here
    }//GEN-BEGIN:|277-entry|2|
    //</editor-fold>//GEN-END:|277-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: responseAlertEntry ">//GEN-BEGIN:|286-entry|0|287-preAction
    /**
     * Performs an action assigned to the responseAlertEntry entry-point.
     */
    public void responseAlertEntry() {//GEN-END:|286-entry|0|287-preAction
        // write pre-action user code here
        switchDisplayable(null, getResponseAlert());//GEN-LINE:|286-entry|1|287-postAction
        // write post-action user code here
    }//GEN-BEGIN:|286-entry|2|
    //</editor-fold>//GEN-END:|286-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: exitEntry ">//GEN-BEGIN:|295-entry|0|296-preAction
    /**
     * Performs an action assigned to the exitEntry entry-point.
     */
    public void exitEntry() {//GEN-END:|295-entry|0|296-preAction
        // write pre-action user code here
        exitMIDlet();//GEN-LINE:|295-entry|1|296-postAction
        // write post-action user code here
    }//GEN-BEGIN:|295-entry|2|
    //</editor-fold>//GEN-END:|295-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: requestProcessingForm ">//GEN-BEGIN:|308-getter|0|308-preInit
    /**
     * Returns an initiliazed instance of requestProcessingForm component.
     * @return the initialized component instance
     */
    public Form getRequestProcessingForm() {
        if (requestProcessingForm == null) {//GEN-END:|308-getter|0|308-preInit
            // write pre-init user code here
            requestProcessingForm = new Form("\u041E\u0431\u0440\u0430\u0431\u043E\u0442\u043A\u0430", new Item[] { getRequestProcessingGauge(), getRequestProcessingStringItem() });//GEN-BEGIN:|308-getter|1|308-postInit
            requestProcessingForm.addCommand(getCancelCommand());
            requestProcessingForm.setCommandListener(this);//GEN-END:|308-getter|1|308-postInit
            // write post-init user code here
        }//GEN-BEGIN:|308-getter|2|
        return requestProcessingForm;
    }
    //</editor-fold>//GEN-END:|308-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: requestProcessingGauge ">//GEN-BEGIN:|311-getter|0|311-preInit
    /**
     * Returns an initiliazed instance of requestProcessingGauge component.
     * @return the initialized component instance
     */
    public Gauge getRequestProcessingGauge() {
        if (requestProcessingGauge == null) {//GEN-END:|311-getter|0|311-preInit
            // write pre-init user code here
            requestProcessingGauge = new Gauge("\u0417\u0430\u043F\u0440\u043E\u0441 \u043E\u0431\u0440\u0430\u0431\u0430\u0442\u044B\u0432\u0430\u0435\u0442\u0441\u044F...", false, REQUEST_PROCESSING_TIMEOUT, 0);//GEN-BEGIN:|311-getter|1|311-postInit
            requestProcessingGauge.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_VCENTER | Item.LAYOUT_VEXPAND | Item.LAYOUT_2);//GEN-END:|311-getter|1|311-postInit
            // write post-init user code here
        }//GEN-BEGIN:|311-getter|2|
        return requestProcessingGauge;
    }
    //</editor-fold>//GEN-END:|311-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: passwordTextField ">//GEN-BEGIN:|313-getter|0|313-preInit
    /**
     * Returns an initiliazed instance of passwordTextField component.
     * @return the initialized component instance
     */
    public TextField getPasswordTextField() {
        if (passwordTextField == null) {//GEN-END:|313-getter|0|313-preInit
            // write pre-init user code here
            passwordTextField = new TextField("\u041F\u0430\u0440\u043E\u043B\u044C", null, MAX_PASSWORD_LENGTH, TextField.ANY | TextField.PASSWORD | TextField.SENSITIVE | TextField.NON_PREDICTIVE);//GEN-LINE:|313-getter|1|313-postInit
            // write post-init user code here
        }//GEN-BEGIN:|313-getter|2|
        return passwordTextField;
    }
    //</editor-fold>//GEN-END:|313-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: passwordCheckTextField ">//GEN-BEGIN:|314-getter|0|314-preInit
    /**
     * Returns an initiliazed instance of passwordCheckTextField component.
     * @return the initialized component instance
     */
    public TextField getPasswordCheckTextField() {
        if (passwordCheckTextField == null) {//GEN-END:|314-getter|0|314-preInit
            // write pre-init user code here
            passwordCheckTextField = new TextField("\u041F\u0440\u043E\u0432\u0435\u0440\u043A\u0430", null, MAX_PASSWORD_LENGTH, TextField.ANY | TextField.PASSWORD | TextField.SENSITIVE | TextField.NON_PREDICTIVE);//GEN-LINE:|314-getter|1|314-postInit
            // write post-init user code here
        }//GEN-BEGIN:|314-getter|2|
        return passwordCheckTextField;
    }
    //</editor-fold>//GEN-END:|314-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: showPasswordChoiceGroup ">//GEN-BEGIN:|315-getter|0|315-preInit
    /**
     * Returns an initiliazed instance of showPasswordChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getShowPasswordChoiceGroup() {
        if (showPasswordChoiceGroup == null) {//GEN-END:|315-getter|0|315-preInit
            // write pre-init user code here
            showPasswordChoiceGroup = new ChoiceGroup(null, Choice.MULTIPLE);//GEN-BEGIN:|315-getter|1|315-postInit
            showPasswordChoiceGroup.append("\u041E\u0442\u043E\u0431\u0440\u0430\u0437\u0438\u0442\u044C", null);
            showPasswordChoiceGroup.setSelectedFlags(new boolean[] { false });//GEN-END:|315-getter|1|315-postInit
            // write post-init user code here
        }//GEN-BEGIN:|315-getter|2|
        return showPasswordChoiceGroup;
    }
    //</editor-fold>//GEN-END:|315-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: generalAlert ">//GEN-BEGIN:|318-getter|0|318-preInit
    /**
     * Returns an initiliazed instance of generalAlert component.
     * @return the initialized component instance
     */
    public Alert getGeneralAlert() {
        if (generalAlert == null) {//GEN-END:|318-getter|0|318-preInit
            // write pre-init user code here
            generalAlert = new Alert("\u041E\u0448\u0438\u0431\u043A\u0430", null, null, AlertType.ERROR);//GEN-BEGIN:|318-getter|1|318-postInit
            generalAlert.setTimeout(Alert.FOREVER);//GEN-END:|318-getter|1|318-postInit
            // write post-init user code here
        }//GEN-BEGIN:|318-getter|2|
        return generalAlert;
    }
    //</editor-fold>//GEN-END:|318-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: phoneNumberTextField ">//GEN-BEGIN:|322-getter|0|322-preInit
    /**
     * Returns an initiliazed instance of phoneNumberTextField component.
     * @return the initialized component instance
     */
    public TextField getPhoneNumberTextField() {
        if (phoneNumberTextField == null) {//GEN-END:|322-getter|0|322-preInit
            // write pre-init user code here
            phoneNumberTextField = new TextField("\u2116 \u0442\u0435\u043B\u0435\u0444\u043E\u043D\u0430", null, 15, TextField.NUMERIC | TextField.NON_PREDICTIVE);//GEN-LINE:|322-getter|1|322-postInit
            // write post-init user code here
        }//GEN-BEGIN:|322-getter|2|
        return phoneNumberTextField;
    }
    //</editor-fold>//GEN-END:|322-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: csiTextField ">//GEN-BEGIN:|323-getter|0|323-preInit
    /**
     * Returns an initiliazed instance of csiTextField component.
     * @return the initialized component instance
     */
    public TextField getCsiTextField() {
        if (csiTextField == null) {//GEN-END:|323-getter|0|323-preInit
            // write pre-init user code here
            csiTextField = new TextField("\u2116 \u0437\u0430\u044F\u0432\u043A\u0438", null, 12, TextField.NUMERIC | TextField.NON_PREDICTIVE);//GEN-LINE:|323-getter|1|323-postInit
            // write post-init user code here
        }//GEN-BEGIN:|323-getter|2|
        return csiTextField;
    }
    //</editor-fold>//GEN-END:|323-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: requestProcessingStringItem ">//GEN-BEGIN:|326-getter|0|326-preInit
    /**
     * Returns an initiliazed instance of requestProcessingStringItem component.
     * @return the initialized component instance
     */
    public StringItem getRequestProcessingStringItem() {
        if (requestProcessingStringItem == null) {//GEN-END:|326-getter|0|326-preInit
            // write pre-init user code here
            requestProcessingStringItem = new StringItem(null, null);//GEN-LINE:|326-getter|1|326-postInit
            // write post-init user code here
        }//GEN-BEGIN:|326-getter|2|
        return requestProcessingStringItem;
    }
    //</editor-fold>//GEN-END:|326-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: infoImage ">//GEN-BEGIN:|329-getter|0|329-preInit
    /**
     * Returns an initiliazed instance of infoImage component.
     * @return the initialized component instance
     */
    public Image getInfoImage() {
        if (infoImage == null) {//GEN-END:|329-getter|0|329-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|329-getter|1|329-@java.io.IOException
                infoImage = Image.createImage("/kz/paysoft/paymobile/midlet/Info.png");
            } catch (java.io.IOException e) {//GEN-END:|329-getter|1|329-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|329-getter|2|329-postInit
            // write post-init user code here
        }//GEN-BEGIN:|329-getter|3|
        return infoImage;
    }
    //</editor-fold>//GEN-END:|329-getter|3|

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    private Display getDisplay() {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    private void exitMIDlet() {
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
        Service.cleanUp();
        operations.cleanUp();
        setup.cleanUp();

        try {
            if (miscellaneousStore != null) {
                miscellaneousStore.closeRecordStore();
            }
            if (sourceAccoutsStore != null) {
                sourceAccoutsStore.closeRecordStore();
            }
            if (destinationAccountsStore != null) {
                destinationAccountsStore.closeRecordStore();
            }
            if (presetTransfersStore != null) {
                presetTransfersStore.closeRecordStore();
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }

    static void clearBuffer(byte[] buf) {
        for (int i = 0; buf != null && i < buf.length; i++) {
            buf[i] = 0;
        }
    }

    static void clearBuffer(char[] buf) {
        for (int i = 0; buf != null && i < buf.length; i++) {
            buf[i] = 0;
        }
    }

    // As I don't trust to setChars(null, 0, 0).
    void clearTextField(TextField field) {
        if (field != null) {
            char buf[] = new char[field.getMaxSize()];
            field.setChars(buf, 0, buf.length);
            field.setChars(null, 0, 0);
        }
    }

    private Alert getGeneralAlert(String title, String string, AlertType type, Image img) {
        getGeneralAlert().setTitle(title);
        generalAlert.setString(string);
        generalAlert.setType(type);
        generalAlert.setImage(img);
        return generalAlert;
    }

    Alert getGeneralAlertAsError(String string) {
        return getGeneralAlert(ERROR_TITLE, string, AlertType.ERROR, getErrorImage());
    }

    Alert getGeneralAlertAsInfo(String string) {
        return getGeneralAlert(INFO_TITLE, string, AlertType.INFO, getInfoImage());
    }

    void setResponseAsInfo(String string) {
        getResponseAlert().setTitle(INFO_TITLE);
        responseAlert.setString(string);
        responseAlert.setType(AlertType.INFO);
        responseAlert.setImage(getInfoImage());
    }

    void showError(String string) {
        getErrorAlert().setString(string);
        switchDisplayable(null, errorAlert);
    }

    void appendErrorString(String string) {
        getErrorAlert().setString(getErrorAlert().getString() + "\n" + string);
    }

    public void itemStateChanged(Item item) {
        if (item == showPasswordChoiceGroup) {
            if (showPasswordChoiceGroup.isSelected(0)) {
                passwordTextField.setConstraints(TextField.ANY | TextField.SENSITIVE | TextField.NON_PREDICTIVE);
                passwordCheckTextField.setConstraints(TextField.ANY | TextField.SENSITIVE | TextField.NON_PREDICTIVE);
            } else {
                passwordTextField.setConstraints(TextField.ANY | TextField.PASSWORD | TextField.SENSITIVE | TextField.NON_PREDICTIVE);
                passwordCheckTextField.setConstraints(TextField.ANY | TextField.PASSWORD | TextField.SENSITIVE | TextField.NON_PREDICTIVE);
            }
        }
    }

    public void run() {
        getRequestProcessingStringItem().setText("");
        getRequestProcessingGauge().setValue(0);
        for (int i = 1; i <= REQUEST_PROCESSING_TIMEOUT; i++) {
            try {
                Thread.sleep(REQUEST_PROCESSING_SLEEP);
                getRequestProcessingGauge().setValue(i);
            } catch (InterruptedException ex) {
                return;
            }
        }
        getRequestProcessingStringItem().setText(REQUEST_PROCESSING_TIMED_OUT);
    }

    void cancelRequestProcessingForm() {
        requestProcessingThread.interrupt();
    }

    void showRequestProcessingForm() {
        switchDisplayable(null, getRequestProcessingForm());
        requestProcessingThread = new Thread(this);
        requestProcessingThread.start();
    }

    public int compare(byte[] rec1, byte[] rec2) {
        ByteArrayInputStream bais1 = new ByteArrayInputStream(rec1);
        ByteArrayInputStream bais2 = new ByteArrayInputStream(rec2);
        DataInputStream dis1 = new DataInputStream(bais1);
        DataInputStream dis2 = new DataInputStream(bais2);

        try {
            byte priority1 = dis1.readByte();
            byte priority2 = dis2.readByte();

            if (priority1 < priority2) {
                return RecordComparator.PRECEDES;
            } else if (priority1 > priority2) {
                return RecordComparator.FOLLOWS;
            } else {
                return RecordComparator.EQUIVALENT;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return RecordComparator.EQUIVALENT;
        }
    }

    public boolean matches(byte[] candidate) {
        ByteArrayInputStream bais = new ByteArrayInputStream(candidate);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            return presetTransferSearchName.equals(dis.readUTF());
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    void cleanUpAndRemoveAllStores() throws RecordStoreException {
        RecordEnumeration re = miscellaneousStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
            int id = re.nextRecordId();
            byte buf[] = new byte[miscellaneousStore.getRecordSize(id)];
            miscellaneousStore.setRecord(id, buf, 0, buf.length);
            miscellaneousStore.deleteRecord(id);
//#mdebug
            System.out.println("Misc Store:");
            System.out.println(id + ", " + buf.length);
//#enddebug
        }
        re.destroy();
        miscellaneousStore.closeRecordStore();
        RecordStore.deleteRecordStore(MISCELLANEOUS_STORE);
        miscellaneousStore = null;

        cleanUpAccountsStores();
        sourceAccoutsStore.closeRecordStore();
        RecordStore.deleteRecordStore(SOURCE_ACCOUNTS_STORE);
        sourceAccoutsStore = null;
        destinationAccountsStore.closeRecordStore();
        RecordStore.deleteRecordStore(DESTINATION_ACCOUNTS_STORE);
        destinationAccountsStore = null;

        re = presetTransfersStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
            int id = re.nextRecordId();
            byte buf[] = new byte[presetTransfersStore.getRecordSize(id)];
            presetTransfersStore.setRecord(id, buf, 0, buf.length);
            presetTransfersStore.deleteRecord(id);
//#mdebug
            System.out.println("Preset Transfers Store:");
            System.out.println(id + ", " + buf.length);
//#enddebug
        }
        re.destroy();
        presetTransfersStore.closeRecordStore();
        RecordStore.deleteRecordStore(PRESET_TRANSFERS_STORE);
        presetTransfersStore = null;
    }

    static void cleanUpAccountsStores() throws RecordStoreException {
        RecordEnumeration re = sourceAccoutsStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
            int id = re.nextRecordId();
            byte buf[] = new byte[sourceAccoutsStore.getRecordSize(id)];
            sourceAccoutsStore.setRecord(id, buf, 0, buf.length);
            sourceAccoutsStore.deleteRecord(id);
//#mdebug
            System.out.println("Source Accounts Store:");
            System.out.println(id + ", " + buf.length);
//#enddebug
        }
        re.destroy();
        re = destinationAccountsStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
            int id = re.nextRecordId();
            byte buf[] = new byte[destinationAccountsStore.getRecordSize(id)];
            destinationAccountsStore.setRecord(id, buf, 0, buf.length);
            destinationAccountsStore.deleteRecord(id);
//#mdebug
            System.out.println("Destination Accounts Store:");
            System.out.println(id + ", " + buf.length);
//#enddebug
        }
        re.destroy();
        re = null;
    }

    private String getAccountName(byte data[]) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        return dis.readUTF();
    }

    void fillSourceAccoutsChoiceGroup(ChoiceGroup group) throws IOException, RecordStoreException {
        group.deleteAll();
        RecordEnumeration re = sourceAccoutsStore.enumerateRecords(null, null, false);

        group.append(" ", null);
        while (re.hasNextElement()) {
            group.append(getAccountName(re.nextRecord()), null);
        }

        re.destroy();
    }

    void fillDestinationAccoutsChoiceGroup(ChoiceGroup group) throws IOException, RecordStoreException {
        group.deleteAll();
        RecordEnumeration re = destinationAccountsStore.enumerateRecords(null, null, false);

        group.append(" ", null);
        while (re.hasNextElement()) {
            group.append(getAccountName(re.nextRecord()), null);
        }

        re.destroy();
    }

    void fillCurrenciesChoiceGroup(ChoiceGroup group) {
        group.append(" ", null);
        for (int i = 0; i < CURRENCIES.length; i++) {
            group.append(CURRENCIES[i], null);
        }
    }

    void selecteChoiceGroupElement(ChoiceGroup group, String string) {
        for (int i = 0; i < group.size(); i++) {
            if (group.getString(i).equals(string)) {
                group.setSelectedIndex(i, true);
                return;
            }
        }
        group.setSelectedIndex(0, true);
    }
}
