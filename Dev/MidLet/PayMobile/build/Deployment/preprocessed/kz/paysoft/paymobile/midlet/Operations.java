/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: R.
 * Last error index: 005.
 *
 * 2010.03.19   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;

/**
 * @author Yerzhan Tulepov.
 */
final class Operations implements CommandListener {

    private static final String UNDEFINED_OPERATION = "Операция не определена.";
    private static final String INCORRECT_AMOUNT = "Сумма некорректна.";
    private static final String ACCOUNTS_AND_CURRENCY_MUST_BE_DEFINED = "Счета и валюта должны быть определены.";
    private static final String ACCOUNT_MUST_BE_DEFINED = "Счет должен быть определен.";
    private static final long MAX_AMOUNT_LIMIT = 999999999999L;
    private static final byte TRANSFER = 0;
    private static final byte STATEMENT = 1;
    private static final byte BALANCE = 2;
    private final PayMobile midlet;
    static final byte UPDATE_ACCOUNTS = 3;
    static byte currentOperation;
    static boolean returnToSetup = false;
    static boolean showResponseData = false;
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form balanseForm;
    private ChoiceGroup balanseSourceAccountsChoiceGroup;
    private Form passwordForm;
    private TextField passwordTextField;
    private Form statementForm;
    private ChoiceGroup statementTypesChoiceGroup;
    private ChoiceGroup statementSourceAccountsChoiceGroup;
    private Form transferForm;
    private ChoiceGroup transferSourceAccountsChoiceGroup;
    private ChoiceGroup transferDestinationAccountsChoiceGroup;
    private TextField paymentTypeCodeTextField;
    private ChoiceGroup currenciesChoiceGroup;
    private TextField amountTextField;
    private TextBox descriptionTextBox;
    private List presetTransfersList;
    private Form responseDataForm;
    private StringItem responseDataStringItem;
    private Command okCommand;
    private Command cancelCommand;
    private Command backCommand;
    private Command nextCommand;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The Operations constructor.
     * @param midlet the midlet used for getting
     */
    Operations(PayMobile midlet) {
        this.midlet = midlet;
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|2-switchDisplayable|0|2-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|2-switchDisplayable|0|2-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|2-switchDisplayable|1|2-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|2-switchDisplayable|1|2-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|2-switchDisplayable|2|
    //</editor-fold>//GEN-END:|2-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: balanseForm ">//GEN-BEGIN:|11-getter|0|11-preInit
    /**
     * Returns an initiliazed instance of balanseForm component.
     * @return the initialized component instance
     */
    public Form getBalanseForm() {
        if (balanseForm == null) {//GEN-END:|11-getter|0|11-preInit
            // write pre-init user code here
            balanseForm = new Form("\u0411\u0430\u043B\u0430\u043D\u0441", new Item[] { getBalanseSourceAccountsChoiceGroup() });//GEN-BEGIN:|11-getter|1|11-postInit
            balanseForm.addCommand(getOkCommand());
            balanseForm.addCommand(getCancelCommand());
            balanseForm.setCommandListener(this);//GEN-END:|11-getter|1|11-postInit
            // write post-init user code here
        }//GEN-BEGIN:|11-getter|2|
        return balanseForm;
    }
    //</editor-fold>//GEN-END:|11-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: balanseFormEntry ">//GEN-BEGIN:|12-entry|0|13-preAction
    /**
     * Performs an action assigned to the balanseFormEntry entry-point.
     */
    public void balanseFormEntry() {//GEN-END:|12-entry|0|13-preAction
        // write pre-action user code here
        switchDisplayable(null, getBalanseForm());//GEN-LINE:|12-entry|1|13-postAction
        // write post-action user code here
    }//GEN-BEGIN:|12-entry|2|
    //</editor-fold>//GEN-END:|12-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|4-commandAction|0|4-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|4-commandAction|0|4-preCommandAction
        // write pre-action user code here
        if (displayable == balanseForm) {//GEN-BEGIN:|4-commandAction|1|18-preAction
            if (command == cancelCommand) {//GEN-END:|4-commandAction|1|18-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|2|18-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|3|16-preAction
                // write pre-action user code here
                String src = balanseSourceAccountsChoiceGroup.getString(balanseSourceAccountsChoiceGroup.getSelectedIndex());
                if (src.trim().equals("")) {
                    switchDisplayable(midlet.getGeneralAlertAsError(ACCOUNT_MUST_BE_DEFINED), getBalanseForm());
                } else {
                    currentOperation = BALANCE;
                    Service.sourceAccount = src;
                    switchDisplayable(null, getPasswordForm());//GEN-LINE:|4-commandAction|4|16-postAction
                }
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|5|43-preAction
        } else if (displayable == descriptionTextBox) {
            if (command == backCommand) {//GEN-END:|4-commandAction|5|43-preAction
                // write pre-action user code here
                switchDisplayable(null, getTransferForm());//GEN-LINE:|4-commandAction|6|43-postAction
                // write post-action user code here
            } else if (command == cancelCommand) {//GEN-LINE:|4-commandAction|7|41-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|8|41-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|9|40-preAction
                // write pre-action user code here
                switchDisplayable(null, getPasswordForm());//GEN-LINE:|4-commandAction|10|40-postAction
                // write post-action user code here
                currentOperation = TRANSFER;
                Service.description = descriptionTextBox.getString();
            }//GEN-BEGIN:|4-commandAction|11|63-preAction
        } else if (displayable == passwordForm) {
            if (command == cancelCommand) {//GEN-END:|4-commandAction|11|63-preAction
                // write pre-action user code here
                if (returnToSetup) {
                    midlet.setup.settingsListEntry();
                } else {
                    midlet.operationsListEntry();//GEN-LINE:|4-commandAction|12|63-postAction
                }
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|13|62-preAction
                // write pre-action user code here
                if (passwordTextField.size() < PayMobile.MIN_PASSWORD_LENGTH) {
                    switchDisplayable(midlet.getGeneralAlertAsError(PayMobile.PASSWORD_IS_SHORT_PREFIX + " " + PayMobile.MIN_PASSWORD_LENGTH + PayMobile.PASSWORD_IS_SHORT_SUFFIX),
                            getPasswordForm());
                } else {
                    PayMobile.clearBuffer(Service.password);
                    Service.password = new char[passwordTextField.size()];
                    passwordTextField.getChars(Service.password);
                    midlet.clearTextField(passwordTextField);

                    switch (currentOperation) {
                        case TRANSFER:
                            midlet.showRequestProcessingForm();
                            new Transfer().start();
                            break;

                        case STATEMENT:
                            midlet.showRequestProcessingForm();
                            new Statement().start();
                            break;

                        case BALANCE:
                            midlet.showRequestProcessingForm();
                            new Balanse().start();
                            break;

                        case UPDATE_ACCOUNTS:
                            midlet.showRequestProcessingForm();
                            new UpdateAccounts().start();
                            break;

                        default:
                            midlet.showError("R-001\n" + UNDEFINED_OPERATION);
                    }
                }
                /*
showResponseData ();//GEN-LINE:|4-commandAction|14|62-postAction
                 */
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|15|52-preAction
        } else if (displayable == presetTransfersList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|4-commandAction|15|52-preAction
                // write pre-action user code here
                switchDisplayable(null, getTransferForm());//GEN-LINE:|4-commandAction|16|52-postAction
                // write post-action user code here
                try {
                    fillTransfer();
                    getTransferForm().addCommand(getBackCommand());
                } catch (RecordStoreException ex) {
                    midlet.showError("R-004\n" + ex.getMessage());
                } catch (IOException ex) {
                    midlet.showError("R-005\n" + ex.getMessage());
                }
            } else if (command == cancelCommand) {//GEN-LINE:|4-commandAction|17|54-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|18|54-postAction
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|19|65-preAction
        } else if (displayable == responseDataForm) {
            if (command == okCommand) {//GEN-END:|4-commandAction|19|65-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|20|65-postAction
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|21|25-preAction
        } else if (displayable == statementForm) {
            if (command == cancelCommand) {//GEN-END:|4-commandAction|21|25-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|22|25-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|23|24-preAction
                // write pre-action user code here
                String src = statementSourceAccountsChoiceGroup.getString(statementSourceAccountsChoiceGroup.getSelectedIndex());
                if (src.trim().equals("")) {
                    switchDisplayable(midlet.getGeneralAlertAsError(ACCOUNT_MUST_BE_DEFINED), getStatementForm());
                } else {
                    currentOperation = STATEMENT;
                    Service.sourceAccount = src;
                    Byte type = (Byte) PayMobile.statementTypes.get(statementTypesChoiceGroup.getString(statementTypesChoiceGroup.getSelectedIndex()));
                    Statement.statementType = type.byteValue();
                    switchDisplayable(null, getPasswordForm());//GEN-LINE:|4-commandAction|24|24-postAction
                }
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|25|33-preAction
        } else if (displayable == transferForm) {
            if (command == cancelCommand) {//GEN-END:|4-commandAction|25|33-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|26|33-postAction
                // write post-action user code here
            } else if (command == nextCommand) {//GEN-LINE:|4-commandAction|27|111-preAction
                // write pre-action user code here
                String src = transferSourceAccountsChoiceGroup.getString(transferSourceAccountsChoiceGroup.getSelectedIndex());
                String dst = transferDestinationAccountsChoiceGroup.getString(transferDestinationAccountsChoiceGroup.getSelectedIndex());
                String curr = currenciesChoiceGroup.getString(currenciesChoiceGroup.getSelectedIndex());
                if (src.trim().equals("") || dst.trim().equals("") || curr.trim().equals("")) {
                    switchDisplayable(midlet.getGeneralAlertAsError(ACCOUNTS_AND_CURRENCY_MUST_BE_DEFINED), getTransferForm());
                } else if (convertAmount()) {
                    switchDisplayable(midlet.getGeneralAlertAsError(INCORRECT_AMOUNT), getTransferForm());
                } else {
                    Service.sourceAccount = src;
                    Service.destinationAccount = dst;
                    Service.currency = curr;
                    Service.paymentTypeCode = paymentTypeCodeTextField.getString();
                    switchDisplayable(null, getDescriptionTextBox());//GEN-LINE:|4-commandAction|28|111-postAction
                    // write post-action user code here
                }
            }//GEN-BEGIN:|4-commandAction|29|4-postCommandAction
        }//GEN-END:|4-commandAction|29|4-postCommandAction
        // write post-action user code here
        if (displayable == transferForm) {
            if (command == backCommand) {
                switchDisplayable(null, getPresetTransfersList());
            }
        }
    }//GEN-BEGIN:|4-commandAction|30|
    //</editor-fold>//GEN-END:|4-commandAction|30|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|15-getter|0|15-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|15-getter|0|15-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|15-getter|1|15-postInit
            // write post-init user code here
        }//GEN-BEGIN:|15-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|15-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand ">//GEN-BEGIN:|17-getter|0|17-preInit
    /**
     * Returns an initiliazed instance of cancelCommand component.
     * @return the initialized component instance
     */
    public Command getCancelCommand() {
        if (cancelCommand == null) {//GEN-END:|17-getter|0|17-preInit
            // write pre-init user code here
            cancelCommand = new Command("\u041E\u0442\u043C\u0435\u043D\u0430", Command.CANCEL, 0);//GEN-LINE:|17-getter|1|17-postInit
            // write post-init user code here
        }//GEN-BEGIN:|17-getter|2|
        return cancelCommand;
    }
    //</editor-fold>//GEN-END:|17-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: statementFormEntry ">//GEN-BEGIN:|28-entry|0|29-preAction
    /**
     * Performs an action assigned to the statementFormEntry entry-point.
     */
    public void statementFormEntry() {//GEN-END:|28-entry|0|29-preAction
        // write pre-action user code here
        switchDisplayable(null, getStatementForm());//GEN-LINE:|28-entry|1|29-postAction
        // write post-action user code here
    }//GEN-BEGIN:|28-entry|2|
    //</editor-fold>//GEN-END:|28-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: passwordForm ">//GEN-BEGIN:|21-getter|0|21-preInit
    /**
     * Returns an initiliazed instance of passwordForm component.
     * @return the initialized component instance
     */
    public Form getPasswordForm() {
        if (passwordForm == null) {//GEN-END:|21-getter|0|21-preInit
            // write pre-init user code here
            passwordForm = new Form("\u041F\u0430\u0440\u043E\u043B\u044C", new Item[] { getPasswordTextField() });//GEN-BEGIN:|21-getter|1|21-postInit
            passwordForm.addCommand(getOkCommand());
            passwordForm.addCommand(getCancelCommand());
            passwordForm.setCommandListener(this);//GEN-END:|21-getter|1|21-postInit
            // write post-init user code here
        }//GEN-BEGIN:|21-getter|2|
        return passwordForm;
    }
    //</editor-fold>//GEN-END:|21-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: statementForm ">//GEN-BEGIN:|23-getter|0|23-preInit
    /**
     * Returns an initiliazed instance of statementForm component.
     * @return the initialized component instance
     */
    public Form getStatementForm() {
        if (statementForm == null) {//GEN-END:|23-getter|0|23-preInit
            // write pre-init user code here
            statementForm = new Form("\u0412\u044B\u043F\u0438\u0441\u043A\u0430", new Item[] { getStatementSourceAccountsChoiceGroup(), getStatementTypesChoiceGroup() });//GEN-BEGIN:|23-getter|1|23-postInit
            statementForm.addCommand(getOkCommand());
            statementForm.addCommand(getCancelCommand());
            statementForm.setCommandListener(this);//GEN-END:|23-getter|1|23-postInit
            // write post-init user code here
        }//GEN-BEGIN:|23-getter|2|
        return statementForm;
    }
    //</editor-fold>//GEN-END:|23-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: transferForm ">//GEN-BEGIN:|31-getter|0|31-preInit
    /**
     * Returns an initiliazed instance of transferForm component.
     * @return the initialized component instance
     */
    public Form getTransferForm() {
        if (transferForm == null) {//GEN-END:|31-getter|0|31-preInit
            // write pre-init user code here
            transferForm = new Form("\u041F\u0435\u0440\u0435\u0432\u043E\u0434", new Item[] { getTransferSourceAccountsChoiceGroup(), getTransferDestinationAccountsChoiceGroup(), getAmountTextField(), getCurrenciesChoiceGroup(), getPaymentTypeCodeTextField() });//GEN-BEGIN:|31-getter|1|31-postInit
            transferForm.addCommand(getNextCommand());
            transferForm.addCommand(getCancelCommand());
            transferForm.setCommandListener(this);//GEN-END:|31-getter|1|31-postInit
            // write post-init user code here
        }//GEN-BEGIN:|31-getter|2|
        return transferForm;
    }
    //</editor-fold>//GEN-END:|31-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: transferSourceAccountsChoiceGroup ">//GEN-BEGIN:|34-getter|0|34-preInit
    /**
     * Returns an initiliazed instance of transferSourceAccountsChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getTransferSourceAccountsChoiceGroup() {
        if (transferSourceAccountsChoiceGroup == null) {//GEN-END:|34-getter|0|34-preInit
            // write pre-init user code here
            transferSourceAccountsChoiceGroup = new ChoiceGroup("\u041E\u0442\u043F\u0440\u0430\u0432\u0438\u0442\u0435\u043B\u044C", Choice.POPUP);//GEN-LINE:|34-getter|1|34-postInit
            // write post-init user code here
        }//GEN-BEGIN:|34-getter|2|
        return transferSourceAccountsChoiceGroup;
    }
    //</editor-fold>//GEN-END:|34-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: transferDestinationAccountsChoiceGroup ">//GEN-BEGIN:|35-getter|0|35-preInit
    /**
     * Returns an initiliazed instance of transferDestinationAccountsChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getTransferDestinationAccountsChoiceGroup() {
        if (transferDestinationAccountsChoiceGroup == null) {//GEN-END:|35-getter|0|35-preInit
            // write pre-init user code here
            transferDestinationAccountsChoiceGroup = new ChoiceGroup("\u041F\u043E\u043B\u0443\u0447\u0430\u0442\u0435\u043B\u044C", Choice.POPUP);//GEN-LINE:|35-getter|1|35-postInit
            // write post-init user code here
        }//GEN-BEGIN:|35-getter|2|
        return transferDestinationAccountsChoiceGroup;
    }
    //</editor-fold>//GEN-END:|35-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: currenciesChoiceGroup ">//GEN-BEGIN:|37-getter|0|37-preInit
    /**
     * Returns an initiliazed instance of currenciesChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getCurrenciesChoiceGroup() {
        if (currenciesChoiceGroup == null) {//GEN-END:|37-getter|0|37-preInit
            // write pre-init user code here
            currenciesChoiceGroup = new ChoiceGroup("\u0412\u0430\u043B\u044E\u0442\u0430", Choice.POPUP);//GEN-BEGIN:|37-getter|1|37-postInit
            currenciesChoiceGroup.setSelectedFlags(new boolean[] {  });//GEN-END:|37-getter|1|37-postInit
            // write post-init user code here
        }//GEN-BEGIN:|37-getter|2|
        return currenciesChoiceGroup;
    }
    //</editor-fold>//GEN-END:|37-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: paymentTypeCodeTextField ">//GEN-BEGIN:|38-getter|0|38-preInit
    /**
     * Returns an initiliazed instance of paymentTypeCodeTextField component.
     * @return the initialized component instance
     */
    public TextField getPaymentTypeCodeTextField() {
        if (paymentTypeCodeTextField == null) {//GEN-END:|38-getter|0|38-preInit
            // write pre-init user code here
            paymentTypeCodeTextField = new TextField("(\u0415)\u041A\u041D\u041F", null, 32, TextField.ANY | TextField.NON_PREDICTIVE);//GEN-LINE:|38-getter|1|38-postInit
            // write post-init user code here
        }//GEN-BEGIN:|38-getter|2|
        return paymentTypeCodeTextField;
    }
    //</editor-fold>//GEN-END:|38-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: transferFormEntry ">//GEN-BEGIN:|45-entry|0|46-preAction
    /**
     * Performs an action assigned to the transferFormEntry entry-point.
     */
    public void transferFormEntry() {//GEN-END:|45-entry|0|46-preAction
        // write pre-action user code here
        switchDisplayable(null, getTransferForm());//GEN-LINE:|45-entry|1|46-postAction
        // write post-action user code here
        getTransferForm().removeCommand(getBackCommand());
        clearTransferFields();
    }//GEN-BEGIN:|45-entry|2|
    //</editor-fold>//GEN-END:|45-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: presetTransfersListEntry ">//GEN-BEGIN:|56-entry|0|57-preAction
    /**
     * Performs an action assigned to the presetTransfersListEntry entry-point.
     */
    public void presetTransfersListEntry() {//GEN-END:|56-entry|0|57-preAction
        // write pre-action user code here
        switchDisplayable(null, getPresetTransfersList());//GEN-LINE:|56-entry|1|57-postAction
        // write post-action user code here
    }//GEN-BEGIN:|56-entry|2|
    //</editor-fold>//GEN-END:|56-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: descriptionTextBox ">//GEN-BEGIN:|39-getter|0|39-preInit
    /**
     * Returns an initiliazed instance of descriptionTextBox component.
     * @return the initialized component instance
     */
    public TextBox getDescriptionTextBox() {
        if (descriptionTextBox == null) {//GEN-END:|39-getter|0|39-preInit
            // write pre-init user code here
            descriptionTextBox = new TextBox("\u041E\u043F\u0438\u0441\u0430\u043D\u0438\u0435", null, 100, TextField.ANY);//GEN-BEGIN:|39-getter|1|39-postInit
            descriptionTextBox.addCommand(getOkCommand());
            descriptionTextBox.addCommand(getBackCommand());
            descriptionTextBox.addCommand(getCancelCommand());
            descriptionTextBox.setCommandListener(this);//GEN-END:|39-getter|1|39-postInit
            // write post-init user code here
        }//GEN-BEGIN:|39-getter|2|
        return descriptionTextBox;
    }
    //</editor-fold>//GEN-END:|39-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: presetTransfersList ">//GEN-BEGIN:|50-getter|0|50-preInit
    /**
     * Returns an initiliazed instance of presetTransfersList component.
     * @return the initialized component instance
     */
    public List getPresetTransfersList() {
        if (presetTransfersList == null) {//GEN-END:|50-getter|0|50-preInit
            // write pre-init user code here
            presetTransfersList = new List("\u041F\u0440\u0435\u0434\u0443\u0441\u0442-\u044B\u0435 \u043F\u0435\u0440\u0435\u0432\u043E\u0434\u044B", Choice.IMPLICIT);//GEN-BEGIN:|50-getter|1|50-postInit
            presetTransfersList.addCommand(getCancelCommand());
            presetTransfersList.setCommandListener(this);//GEN-END:|50-getter|1|50-postInit
            // write post-init user code here
        }//GEN-BEGIN:|50-getter|2|
        return presetTransfersList;
    }
    //</editor-fold>//GEN-END:|50-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: presetTransfersListAction ">//GEN-BEGIN:|50-action|0|50-preAction
    /**
     * Performs an action assigned to the selected list element in the presetTransfersList component.
     */
    public void presetTransfersListAction() {//GEN-END:|50-action|0|50-preAction
        // enter pre-action user code here
        String __selectedString = getPresetTransfersList().getString(getPresetTransfersList().getSelectedIndex());//GEN-LINE:|50-action|1|50-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|50-action|2|
    //</editor-fold>//GEN-END:|50-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|42-getter|0|42-preInit
    /**
     * Returns an initiliazed instance of backCommand component.
     * @return the initialized component instance
     */
    public Command getBackCommand() {
        if (backCommand == null) {//GEN-END:|42-getter|0|42-preInit
            // write pre-init user code here
            backCommand = new Command("\u041D\u0430\u0437\u0430\u0434", Command.BACK, 0);//GEN-LINE:|42-getter|1|42-postInit
            // write post-init user code here
        }//GEN-BEGIN:|42-getter|2|
        return backCommand;
    }
    //</editor-fold>//GEN-END:|42-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: responseDataForm ">//GEN-BEGIN:|64-getter|0|64-preInit
    /**
     * Returns an initiliazed instance of responseDataForm component.
     * @return the initialized component instance
     */
    public Form getResponseDataForm() {
        if (responseDataForm == null) {//GEN-END:|64-getter|0|64-preInit
            // write pre-init user code here
            responseDataForm = new Form("\u0414\u0430\u043D\u043D\u044B\u0435", new Item[] { getResponseDataStringItem() });//GEN-BEGIN:|64-getter|1|64-postInit
            responseDataForm.addCommand(getOkCommand());
            responseDataForm.setCommandListener(this);//GEN-END:|64-getter|1|64-postInit
            // write post-init user code here
        }//GEN-BEGIN:|64-getter|2|
        return responseDataForm;
    }
    //</editor-fold>//GEN-END:|64-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: showResponseData ">//GEN-BEGIN:|68-if|0|68-preIf
    /**
     * Performs an action assigned to the showResponseData if-point.
     */
    public void showResponseData() {//GEN-END:|68-if|0|68-preIf
        // enter pre-if user code here
        if (showResponseData) {//GEN-LINE:|68-if|1|69-preAction
            // write pre-action user code here
            switchDisplayable(null, getResponseDataForm());//GEN-LINE:|68-if|2|69-postAction
            // write post-action user code here
        } else {//GEN-LINE:|68-if|3|70-preAction
            // write pre-action user code here
            midlet.responseAlertEntry();//GEN-LINE:|68-if|4|70-postAction
            // write post-action user code here
        }//GEN-LINE:|68-if|5|68-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|68-if|6|
    //</editor-fold>//GEN-END:|68-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: passwordFormEntry ">//GEN-BEGIN:|77-entry|0|78-preAction
    /**
     * Performs an action assigned to the passwordFormEntry entry-point.
     */
    public void passwordFormEntry() {//GEN-END:|77-entry|0|78-preAction
        // write pre-action user code here
        switchDisplayable(null, getPasswordForm());//GEN-LINE:|77-entry|1|78-postAction
        // write post-action user code here
    }//GEN-BEGIN:|77-entry|2|
    //</editor-fold>//GEN-END:|77-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: passwordTextField ">//GEN-BEGIN:|88-getter|0|88-preInit
    /**
     * Returns an initiliazed instance of passwordTextField component.
     * @return the initialized component instance
     */
    public TextField getPasswordTextField() {
        if (passwordTextField == null) {//GEN-END:|88-getter|0|88-preInit
            // write pre-init user code here
            passwordTextField = new TextField("\u041F\u0430\u0440\u043E\u043B\u044C", null, midlet.MAX_PASSWORD_LENGTH, TextField.ANY | TextField.PASSWORD | TextField.SENSITIVE | TextField.NON_PREDICTIVE);//GEN-LINE:|88-getter|1|88-postInit
            // write post-init user code here
        }//GEN-BEGIN:|88-getter|2|
        return passwordTextField;
    }
    //</editor-fold>//GEN-END:|88-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: responseDataStringItem ">//GEN-BEGIN:|89-getter|0|89-preInit
    /**
     * Returns an initiliazed instance of responseDataStringItem component.
     * @return the initialized component instance
     */
    public StringItem getResponseDataStringItem() {
        if (responseDataStringItem == null) {//GEN-END:|89-getter|0|89-preInit
            // write pre-init user code here
            responseDataStringItem = new StringItem(null, null);//GEN-LINE:|89-getter|1|89-postInit
            // write post-init user code here
        }//GEN-BEGIN:|89-getter|2|
        return responseDataStringItem;
    }
    //</editor-fold>//GEN-END:|89-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: balanseSourceAccountsChoiceGroup ">//GEN-BEGIN:|90-getter|0|90-preInit
    /**
     * Returns an initiliazed instance of balanseSourceAccountsChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getBalanseSourceAccountsChoiceGroup() {
        if (balanseSourceAccountsChoiceGroup == null) {//GEN-END:|90-getter|0|90-preInit
            // write pre-init user code here
            balanseSourceAccountsChoiceGroup = new ChoiceGroup("\u0421\u0447\u0435\u0442", Choice.POPUP);//GEN-LINE:|90-getter|1|90-postInit
            // write post-init user code here
        }//GEN-BEGIN:|90-getter|2|
        return balanseSourceAccountsChoiceGroup;
    }
    //</editor-fold>//GEN-END:|90-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: statementSourceAccountsChoiceGroup ">//GEN-BEGIN:|91-getter|0|91-preInit
    /**
     * Returns an initiliazed instance of statementSourceAccountsChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getStatementSourceAccountsChoiceGroup() {
        if (statementSourceAccountsChoiceGroup == null) {//GEN-END:|91-getter|0|91-preInit
            // write pre-init user code here
            statementSourceAccountsChoiceGroup = new ChoiceGroup("\u0421\u0447\u0435\u0442", Choice.POPUP);//GEN-LINE:|91-getter|1|91-postInit
            // write post-init user code here
        }//GEN-BEGIN:|91-getter|2|
        return statementSourceAccountsChoiceGroup;
    }
    //</editor-fold>//GEN-END:|91-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: statementTypesChoiceGroup ">//GEN-BEGIN:|92-getter|0|92-preInit
    /**
     * Returns an initiliazed instance of statementTypesChoiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getStatementTypesChoiceGroup() {
        if (statementTypesChoiceGroup == null) {//GEN-END:|92-getter|0|92-preInit
            // write pre-init user code here
            statementTypesChoiceGroup = new ChoiceGroup("\u0422\u0438\u043F", Choice.POPUP);//GEN-LINE:|92-getter|1|92-postInit
            // write post-init user code here
        }//GEN-BEGIN:|92-getter|2|
        return statementTypesChoiceGroup;
    }
    //</editor-fold>//GEN-END:|92-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: amountTextField ">//GEN-BEGIN:|106-getter|0|106-preInit
    /**
     * Returns an initiliazed instance of amountTextField component.
     * @return the initialized component instance
     */
    public TextField getAmountTextField() {
        if (amountTextField == null) {//GEN-END:|106-getter|0|106-preInit
            // write pre-init user code here
            amountTextField = new TextField("\u0421\u0443\u043C\u043C\u0430", null, 13, TextField.DECIMAL | TextField.NON_PREDICTIVE);//GEN-LINE:|106-getter|1|106-postInit
            // write post-init user code here
        }//GEN-BEGIN:|106-getter|2|
        return amountTextField;
    }
    //</editor-fold>//GEN-END:|106-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: nextCommand ">//GEN-BEGIN:|110-getter|0|110-preInit
    /**
     * Returns an initiliazed instance of nextCommand component.
     * @return the initialized component instance
     */
    public Command getNextCommand() {
        if (nextCommand == null) {//GEN-END:|110-getter|0|110-preInit
            // write pre-init user code here
            nextCommand = new Command("\u0414\u0430\u043B\u0435\u0435", Command.SCREEN, 0);//GEN-LINE:|110-getter|1|110-postInit
            // write post-init user code here
        }//GEN-BEGIN:|110-getter|2|
        return nextCommand;
    }
    //</editor-fold>//GEN-END:|110-getter|2|

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    private Display getDisplay() {
        return Display.getDisplay(midlet);
    }

    void init() {
        initAccountsChoiceGroups();
        midlet.fillCurrenciesChoiceGroup(getCurrenciesChoiceGroup());
    }

    void initAccountsChoiceGroups() {
        try {
            midlet.fillSourceAccoutsChoiceGroup(getBalanseSourceAccountsChoiceGroup());
            midlet.fillSourceAccoutsChoiceGroup(getStatementSourceAccountsChoiceGroup());
            midlet.fillSourceAccoutsChoiceGroup(getTransferSourceAccountsChoiceGroup());
            midlet.fillDestinationAccoutsChoiceGroup(getTransferDestinationAccountsChoiceGroup());
        } catch (IOException ex) {
            midlet.showError("R-002\n" + ex.getMessage());
        } catch (RecordStoreException ex) {
            midlet.showError("R-003\n" + ex.getMessage());
        }
    }

    void cleanUp() {
    }

    private boolean convertAmount() {
        String amount = amountTextField.getString();

        if (amount.equals("")) {
            return true;
        }

        int decimalPointPos = amount.indexOf('.');

        if ((decimalPointPos != -1) && ((amount.length() - decimalPointPos) == 1)) {
            return true;
        }

        if ((decimalPointPos != -1) && ((amount.length() - decimalPointPos) > 3)) {
            return true;
        }

        if (decimalPointPos == -1) {
            amount += "00";
        } else if (amount.length() - decimalPointPos == 2) {
            amount = amount.substring(0, decimalPointPos) + amount.substring(decimalPointPos + 1, decimalPointPos + 2) + "0";
        } else if (amount.length() - decimalPointPos == 3) {
            amount = amount.substring(0, decimalPointPos) + amount.substring(decimalPointPos + 1, decimalPointPos + 3);
        }

        long amnt = Long.parseLong(amount);
        if (amnt < 1 || amnt > MAX_AMOUNT_LIMIT) {
            return true;
        }
        Service.amount = amnt;

        return false;
    }

    private void clearTransferFields() {
        transferSourceAccountsChoiceGroup.setSelectedIndex(0, true);
        transferDestinationAccountsChoiceGroup.setSelectedIndex(0, true);
        getAmountTextField().setString("");
        getCurrenciesChoiceGroup().setSelectedIndex(0, true);
        getPaymentTypeCodeTextField().setString("");
        getDescriptionTextBox().setString("");
    }

    private void fillTransfer() throws RecordStoreException, IOException {
        PayMobile.presetTransferSearchName = presetTransfersList.getString(presetTransfersList.getSelectedIndex());
        RecordEnumeration re = PayMobile.presetTransfersStore.enumerateRecords(midlet, null, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(re.nextRecord());
        DataInputStream dis = new DataInputStream(bais);
        re.destroy();

        dis.readByte();
        dis.readUTF();
        midlet.selecteChoiceGroupElement(getTransferSourceAccountsChoiceGroup(), dis.readUTF());
        midlet.selecteChoiceGroupElement(getTransferDestinationAccountsChoiceGroup(), dis.readUTF());
        getAmountTextField().setString(dis.readUTF());
        midlet.selecteChoiceGroupElement(getCurrenciesChoiceGroup(), dis.readUTF());
        getPaymentTypeCodeTextField().setString(dis.readUTF());
        getDescriptionTextBox().setString(dis.readUTF());
    }
}
