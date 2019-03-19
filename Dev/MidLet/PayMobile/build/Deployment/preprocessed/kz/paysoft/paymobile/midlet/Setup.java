/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: S.
 * Last error index: 010.
 *
 * 2010.03.19   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * @author Yerzhan Tulepov.
 */
final class Setup implements CommandListener {

    private static final String CONFIRMATION_NOT_ACCEPTED = "Подтверждение не принято.";
    private static final String NAME_IS_USED = "Имя уже используется.";
    private static final String NAME_MUST_NOT_BE_EMPTY = "Имя не должно быть пустым.";
    private final PayMobile midlet;
    private int currentPresetTransferId;
    private boolean creatingNewTransfer = false;
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form presetTransferForm;
    private ChoiceGroup sourceAccountsChoiceGroup;
    private ChoiceGroup destinationAccountsChoiceGroup;
    private TextField paymentTypeCodeTextField;
    private TextField amountTextField;
    private ChoiceGroup currenciesChoiceGroup;
    private List settingsList;
    private List presetTransfersList;
    private TextBox descriptionTextBox;
    private Alert deleteAlert;
    private Alert clearAllAlert;
    private Form clearAllForm;
    private TextField clearAllConfirmationTextField;
    private Form presetTransferNameForm;
    private TextField presetTransferNameTextField;
    private TextField presetTransferPriorityTextField;
    private Command cancelCommand;
    private Command backCommand;
    private Command deleteCommand;
    private Command createCommand;
    private Command nextCommand;
    private Command saveCommand;
    private Command okCommand;
    private Image waringImage;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The Setup constructor.
     * @param midlet the midlet used for getting
     */
    Setup(PayMobile midlet) {
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
    void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|2-switchDisplayable|0|2-preSwitch
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

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: settingsListEntry ">//GEN-BEGIN:|11-entry|0|12-preAction
    /**
     * Performs an action assigned to the settingsListEntry entry-point.
     */
    void settingsListEntry() {//GEN-END:|11-entry|0|12-preAction
        // write pre-action user code here
        switchDisplayable(null, getSettingsList());//GEN-LINE:|11-entry|1|12-postAction
        // write post-action user code here
    }//GEN-BEGIN:|11-entry|2|
    //</editor-fold>//GEN-END:|11-entry|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: presetTransferForm ">//GEN-BEGIN:|13-getter|0|13-preInit
    /**
     * Returns an initiliazed instance of presetTransferForm component.
     * @return the initialized component instance
     */
    Form getPresetTransferForm() {
        if (presetTransferForm == null) {//GEN-END:|13-getter|0|13-preInit
            // write pre-init user code here
            presetTransferForm = new Form("\u0428\u0430\u0431\u043B\u043E\u043D", new Item[] { getSourceAccountsChoiceGroup(), getDestinationAccountsChoiceGroup(), getAmountTextField(), getCurrenciesChoiceGroup(), getPaymentTypeCodeTextField() });//GEN-BEGIN:|13-getter|1|13-postInit
            presetTransferForm.addCommand(getBackCommand());
            presetTransferForm.addCommand(getNextCommand());
            presetTransferForm.setCommandListener(this);//GEN-END:|13-getter|1|13-postInit
            // write post-init user code here
        }//GEN-BEGIN:|13-getter|2|
        return presetTransferForm;
    }
    //</editor-fold>//GEN-END:|13-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|4-commandAction|0|4-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|4-commandAction|0|4-preCommandAction
        // write pre-action user code here
        if (displayable == clearAllAlert) {//GEN-BEGIN:|4-commandAction|1|73-preAction
            if (command == cancelCommand) {//GEN-END:|4-commandAction|1|73-preAction
                // write pre-action user code here
                switchDisplayable(null, getSettingsList());//GEN-LINE:|4-commandAction|2|73-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|3|75-preAction
                // write pre-action user code here
                switchDisplayable(null, getClearAllForm());//GEN-LINE:|4-commandAction|4|75-postAction
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|5|86-preAction
        } else if (displayable == clearAllForm) {
            if (command == cancelCommand) {//GEN-END:|4-commandAction|5|86-preAction
                // write pre-action user code here
                switchDisplayable(null, getSettingsList());//GEN-LINE:|4-commandAction|6|86-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|7|85-preAction
                // write pre-action user code here
                if (!clearAllConfirmationTextField.getString().equals("1234")) {
                    switchDisplayable(midlet.getGeneralAlertAsError(CONFIRMATION_NOT_ACCEPTED), settingsList);
                } else {
                    try {
                        midlet.cleanUpAndRemoveAllStores();
                        midlet.exitEntry();//GEN-LINE:|4-commandAction|8|85-postAction
                    } catch (RecordStoreException e) {
                        midlet.showError("S-010\n" + e.getMessage());
                    }
                }
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|9|58-preAction
        } else if (displayable == deleteAlert) {
            if (command == cancelCommand) {//GEN-END:|4-commandAction|9|58-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransfersList());//GEN-LINE:|4-commandAction|10|58-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|4-commandAction|11|60-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransfersList());//GEN-LINE:|4-commandAction|12|60-postAction
                // write post-action user code here
                try {
                    deletePresetTransfer();
                    refreshPresetTransfersList();
                } catch (RecordStoreException ex) {
                    midlet.showError("S-008\n" + ex.getMessage());
                } catch (IOException ex) {
                    midlet.showError("S-009\n" + ex.getMessage());
                }
            }//GEN-BEGIN:|4-commandAction|13|51-preAction
        } else if (displayable == descriptionTextBox) {
            if (command == backCommand) {//GEN-END:|4-commandAction|13|51-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransferForm());//GEN-LINE:|4-commandAction|14|51-postAction
                // write post-action user code here
            } else if (command == saveCommand) {//GEN-LINE:|4-commandAction|15|54-preAction
                // write pre-action user code here
                try {
                    savePresetTransfer();
                    switchDisplayable(null, getPresetTransfersList());//GEN-LINE:|4-commandAction|16|54-postAction
                } catch (IOException ex) {
                    midlet.showError("S-002\n" + ex.getMessage());
                } catch (RecordStoreException ex) {
                    midlet.showError("S-003\n" + ex.getMessage());
                }
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|17|44-preAction
        } else if (displayable == presetTransferForm) {
            if (command == backCommand) {//GEN-END:|4-commandAction|17|44-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransferNameForm());//GEN-LINE:|4-commandAction|18|44-postAction
                // write post-action user code here
            } else if (command == nextCommand) {//GEN-LINE:|4-commandAction|19|47-preAction
                // write pre-action user code here
                switchDisplayable(null, getDescriptionTextBox());//GEN-LINE:|4-commandAction|20|47-postAction
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|21|93-preAction
        } else if (displayable == presetTransferNameForm) {
            if (command == backCommand) {//GEN-END:|4-commandAction|21|93-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransfersList());//GEN-LINE:|4-commandAction|22|93-postAction
                // write post-action user code here
            } else if (command == nextCommand) {//GEN-LINE:|4-commandAction|23|117-preAction
                // write pre-action user code here
                try {
                    if (presetTransferNameTextField.getString().equals("")) {
                        switchDisplayable(midlet.getGeneralAlertAsError(NAME_MUST_NOT_BE_EMPTY), presetTransferNameForm);
                    } else if (nameIsUsed()) {
                        switchDisplayable(midlet.getGeneralAlertAsError(NAME_IS_USED), presetTransferNameForm);
                    } else {
                        switchDisplayable(null, getPresetTransferForm());//GEN-LINE:|4-commandAction|24|117-postAction
                    }
                } catch (RecordStoreNotOpenException e) {
                    midlet.showError("S-001\n" + e.getMessage());
                }
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|25|27-preAction
        } else if (displayable == presetTransfersList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|4-commandAction|25|27-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransferNameForm());//GEN-LINE:|4-commandAction|26|27-postAction
                // write post-action user code here
                creatingNewTransfer = false;
                try {
                    fillPresetTransfer();
                } catch (RecordStoreException ex) {
                    midlet.showError("S-004\n" + ex.getMessage());
                } catch (IOException ex) {
                    midlet.showError("S-005\n" + ex.getMessage());
                }
            } else if (command == backCommand) {//GEN-LINE:|4-commandAction|27|31-preAction
                // write pre-action user code here
                switchDisplayable(null, getSettingsList());//GEN-LINE:|4-commandAction|28|31-postAction
                // write post-action user code here
            } else if (command == createCommand) {//GEN-LINE:|4-commandAction|29|34-preAction
                // write pre-action user code here
                creatingNewTransfer = true;
                switchDisplayable(null, getPresetTransferNameForm());//GEN-LINE:|4-commandAction|30|34-postAction
                // write post-action user code here
                clearPresetTransferFields();
            } else if (command == deleteCommand) {//GEN-LINE:|4-commandAction|31|36-preAction
                // write pre-action user code here
                if (canDeletePresetTransfer()) {
                    switchDisplayable(null, getDeleteAlert());//GEN-LINE:|4-commandAction|32|36-postAction
                }
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|33|16-preAction
        } else if (displayable == settingsList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|4-commandAction|33|16-preAction
                // write pre-action user code here
                settingsListAction();//GEN-LINE:|4-commandAction|34|16-postAction
                // write post-action user code here
            } else if (command == backCommand) {//GEN-LINE:|4-commandAction|35|121-preAction
                // write pre-action user code here
                midlet.operationsListEntry();//GEN-LINE:|4-commandAction|36|121-postAction
                // write post-action user code here
            }//GEN-BEGIN:|4-commandAction|37|4-postCommandAction
        }//GEN-END:|4-commandAction|37|4-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|4-commandAction|38|
    //</editor-fold>//GEN-END:|4-commandAction|38|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: settingsList ">//GEN-BEGIN:|14-getter|0|14-preInit
    /**
     * Returns an initiliazed instance of settingsList component.
     * @return the initialized component instance
     */
    List getSettingsList() {
        if (settingsList == null) {//GEN-END:|14-getter|0|14-preInit
            // write pre-init user code here
            settingsList = new List("\u041D\u0430\u0441\u0442\u0440\u043E\u0439\u043A\u0438", Choice.IMPLICIT);//GEN-BEGIN:|14-getter|1|14-postInit
            settingsList.append("\u041F\u0440\u0435\u0434\u0443\u0441\u0442-\u044B\u0435 \u043F\u0435\u0440\u0435\u0432\u043E\u0434\u044B", null);
            settingsList.append("\u041E\u0431\u043D\u043E\u0432\u0438\u0442\u044C \u0441\u0447\u0435\u0442\u0430", null);
            settingsList.append("\u041E\u0442\u0447\u0438\u0441\u0442\u0438\u0442\u044C \u0432\u0441\u0451", null);
            settingsList.addCommand(getBackCommand());
            settingsList.setCommandListener(this);
            settingsList.setSelectedFlags(new boolean[] { false, false, false });//GEN-END:|14-getter|1|14-postInit
            // write post-init user code here
        }//GEN-BEGIN:|14-getter|2|
        return settingsList;
    }
    //</editor-fold>//GEN-END:|14-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: settingsListAction ">//GEN-BEGIN:|14-action|0|14-preAction
    /**
     * Performs an action assigned to the selected list element in the settingsList component.
     */
    void settingsListAction() {//GEN-END:|14-action|0|14-preAction
        // enter pre-action user code here
        String __selectedString = getSettingsList().getString(getSettingsList().getSelectedIndex());//GEN-BEGIN:|14-action|1|23-preAction
        if (__selectedString != null) {
            if (__selectedString.equals("\u041F\u0440\u0435\u0434\u0443\u0441\u0442-\u044B\u0435 \u043F\u0435\u0440\u0435\u0432\u043E\u0434\u044B")) {//GEN-END:|14-action|1|23-preAction
                // write pre-action user code here
                switchDisplayable(null, getPresetTransfersList());//GEN-LINE:|14-action|2|23-postAction
                // write post-action user code here
            } else if (__selectedString.equals("\u041E\u0431\u043D\u043E\u0432\u0438\u0442\u044C \u0441\u0447\u0435\u0442\u0430")) {//GEN-LINE:|14-action|3|24-preAction
                // write pre-action user code here
                Operations.returnToSetup = true;//GEN-BEGIN:|14-action|4|24-postAction
                Operations.currentOperation = Operations.UPDATE_ACCOUNTS;
                midlet.operations.passwordFormEntry();//GEN-END:|14-action|4|24-postAction
                // write post-action user code here
            } else if (__selectedString.equals("\u041E\u0442\u0447\u0438\u0441\u0442\u0438\u0442\u044C \u0432\u0441\u0451")) {//GEN-LINE:|14-action|5|25-preAction
                // write pre-action user code here
                switchDisplayable(null, getClearAllAlert());//GEN-LINE:|14-action|6|25-postAction
                // write post-action user code here
            }//GEN-BEGIN:|14-action|7|14-postAction
        }//GEN-END:|14-action|7|14-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|14-action|8|
    //</editor-fold>//GEN-END:|14-action|8|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand ">//GEN-BEGIN:|19-getter|0|19-preInit
    /**
     * Returns an initiliazed instance of cancelCommand component.
     * @return the initialized component instance
     */
    Command getCancelCommand() {
        if (cancelCommand == null) {//GEN-END:|19-getter|0|19-preInit
            // write pre-init user code here
            cancelCommand = new Command("\u041E\u0442\u043C\u0435\u043D\u0430", Command.CANCEL, 0);//GEN-LINE:|19-getter|1|19-postInit
            // write post-init user code here
        }//GEN-BEGIN:|19-getter|2|
        return cancelCommand;
    }
    //</editor-fold>//GEN-END:|19-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: presetTransfersList ">//GEN-BEGIN:|26-getter|0|26-preInit
    /**
     * Returns an initiliazed instance of presetTransfersList component.
     * @return the initialized component instance
     */
    List getPresetTransfersList() {
        if (presetTransfersList == null) {//GEN-END:|26-getter|0|26-preInit
            // write pre-init user code here
            presetTransfersList = new List("\u041F\u0440\u0435\u0434\u0443\u0441\u0442-\u044B\u0435 \u043F\u0435\u0440\u0435\u0432\u043E\u0434\u044B", Choice.IMPLICIT);//GEN-BEGIN:|26-getter|1|26-postInit
            presetTransfersList.addCommand(getDeleteCommand());
            presetTransfersList.addCommand(getBackCommand());
            presetTransfersList.addCommand(getCreateCommand());
            presetTransfersList.setCommandListener(this);//GEN-END:|26-getter|1|26-postInit
            // write post-init user code here
        }//GEN-BEGIN:|26-getter|2|
        return presetTransfersList;
    }
    //</editor-fold>//GEN-END:|26-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: presetTransfersListAction ">//GEN-BEGIN:|26-action|0|26-preAction
    /**
     * Performs an action assigned to the selected list element in the presetTransfersList component.
     */
    void presetTransfersListAction() {//GEN-END:|26-action|0|26-preAction
        // enter pre-action user code here
        String __selectedString = getPresetTransfersList().getString(getPresetTransfersList().getSelectedIndex());//GEN-LINE:|26-action|1|26-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|26-action|2|
    //</editor-fold>//GEN-END:|26-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|30-getter|0|30-preInit
    /**
     * Returns an initiliazed instance of backCommand component.
     * @return the initialized component instance
     */
    Command getBackCommand() {
        if (backCommand == null) {//GEN-END:|30-getter|0|30-preInit
            // write pre-init user code here
            backCommand = new Command("\u041D\u0430\u0437\u0430\u0434", Command.BACK, 0);//GEN-LINE:|30-getter|1|30-postInit
            // write post-init user code here
        }//GEN-BEGIN:|30-getter|2|
        return backCommand;
    }
    //</editor-fold>//GEN-END:|30-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: createCommand ">//GEN-BEGIN:|33-getter|0|33-preInit
    /**
     * Returns an initiliazed instance of createCommand component.
     * @return the initialized component instance
     */
    Command getCreateCommand() {
        if (createCommand == null) {//GEN-END:|33-getter|0|33-preInit
            // write pre-init user code here
            createCommand = new Command("\u0421\u043E\u0437\u0434\u0430\u0442\u044C", Command.SCREEN, 0);//GEN-LINE:|33-getter|1|33-postInit
            // write post-init user code here
        }//GEN-BEGIN:|33-getter|2|
        return createCommand;
    }
    //</editor-fold>//GEN-END:|33-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deleteCommand ">//GEN-BEGIN:|35-getter|0|35-preInit
    /**
     * Returns an initiliazed instance of deleteCommand component.
     * @return the initialized component instance
     */
    Command getDeleteCommand() {
        if (deleteCommand == null) {//GEN-END:|35-getter|0|35-preInit
            // write pre-init user code here
            deleteCommand = new Command("\u0423\u0434\u0430\u043B\u0438\u0442\u044C", Command.SCREEN, 1);//GEN-LINE:|35-getter|1|35-postInit
            // write post-init user code here
        }//GEN-BEGIN:|35-getter|2|
        return deleteCommand;
    }
    //</editor-fold>//GEN-END:|35-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: nextCommand ">//GEN-BEGIN:|46-getter|0|46-preInit
    /**
     * Returns an initiliazed instance of nextCommand component.
     * @return the initialized component instance
     */
    Command getNextCommand() {
        if (nextCommand == null) {//GEN-END:|46-getter|0|46-preInit
            // write pre-init user code here
            nextCommand = new Command("\u0414\u0430\u043B\u0435\u0435", Command.SCREEN, 0);//GEN-LINE:|46-getter|1|46-postInit
            // write post-init user code here
        }//GEN-BEGIN:|46-getter|2|
        return nextCommand;
    }
    //</editor-fold>//GEN-END:|46-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: descriptionTextBox ">//GEN-BEGIN:|49-getter|0|49-preInit
    /**
     * Returns an initiliazed instance of descriptionTextBox component.
     * @return the initialized component instance
     */
    TextBox getDescriptionTextBox() {
        if (descriptionTextBox == null) {//GEN-END:|49-getter|0|49-preInit
            // write pre-init user code here
            descriptionTextBox = new TextBox("\u041E\u043F\u0438\u0441\u0430\u043D\u0438\u0435", null, 100, TextField.ANY);//GEN-BEGIN:|49-getter|1|49-postInit
            descriptionTextBox.addCommand(getBackCommand());
            descriptionTextBox.addCommand(getSaveCommand());
            descriptionTextBox.setCommandListener(this);//GEN-END:|49-getter|1|49-postInit
            // write post-init user code here
        }//GEN-BEGIN:|49-getter|2|
        return descriptionTextBox;
    }
    //</editor-fold>//GEN-END:|49-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: deleteAlert ">//GEN-BEGIN:|56-getter|0|56-preInit
    /**
     * Returns an initiliazed instance of deleteAlert component.
     * @return the initialized component instance
     */
    Alert getDeleteAlert() {
        if (deleteAlert == null) {//GEN-END:|56-getter|0|56-preInit
            // write pre-init user code here
            deleteAlert = new Alert("\u041F\u0440\u0435\u0434\u0443\u043F\u0440\u0435\u0436\u0434\u0435\u043D\u0438\u0435", "\u041E\u043F\u0435\u0440\u0430\u0446\u0438\u044E \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E \u043E\u0442\u043C\u0435\u043D\u0438\u0442\u044C. \u041F\u0440\u043E\u0434\u043E\u043B\u0436\u0438\u0442\u044C?", getWaringImage(), AlertType.WARNING);//GEN-BEGIN:|56-getter|1|56-postInit
            deleteAlert.addCommand(getCancelCommand());
            deleteAlert.addCommand(getOkCommand());
            deleteAlert.setCommandListener(this);
            deleteAlert.setTimeout(Alert.FOREVER);//GEN-END:|56-getter|1|56-postInit
            // write post-init user code here
        }//GEN-BEGIN:|56-getter|2|
        return deleteAlert;
    }
    //</editor-fold>//GEN-END:|56-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: saveCommand ">//GEN-BEGIN:|53-getter|0|53-preInit
    /**
     * Returns an initiliazed instance of saveCommand component.
     * @return the initialized component instance
     */
    Command getSaveCommand() {
        if (saveCommand == null) {//GEN-END:|53-getter|0|53-preInit
            // write pre-init user code here
            saveCommand = new Command("\u0421\u043E\u0445\u0440\u0430\u043D\u0438\u0442\u044C", Command.SCREEN, 0);//GEN-LINE:|53-getter|1|53-postInit
            // write post-init user code here
        }//GEN-BEGIN:|53-getter|2|
        return saveCommand;
    }
    //</editor-fold>//GEN-END:|53-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|59-getter|0|59-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|59-getter|0|59-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|59-getter|1|59-postInit
            // write post-init user code here
        }//GEN-BEGIN:|59-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|59-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: clearAllAlert ">//GEN-BEGIN:|71-getter|0|71-preInit
    /**
     * Returns an initiliazed instance of clearAllAlert component.
     * @return the initialized component instance
     */
    Alert getClearAllAlert() {
        if (clearAllAlert == null) {//GEN-END:|71-getter|0|71-preInit
            // write pre-init user code here
            clearAllAlert = new Alert("\u0412\u043D\u0438\u043C\u0430\u043D\u0438\u0435!", "\u0412\u0441\u0435 \u0434\u0430\u043D\u043D\u044B\u0435 \u0431\u0443\u0434\u0443\u0442 \u0443\u043D\u0438\u0447\u0442\u043E\u0436\u0435\u043D\u044B. \u041E\u043F\u0435\u0440\u0430\u0446\u0438\u044E \u043D\u0435\u0432\u043E\u0437\u043C\u043E\u0436\u043D\u043E \u043E\u0442\u043C\u0435\u043D\u0438\u0442\u044C!", getWaringImage(), AlertType.WARNING);//GEN-BEGIN:|71-getter|1|71-postInit
            clearAllAlert.addCommand(getCancelCommand());
            clearAllAlert.addCommand(getOkCommand());
            clearAllAlert.setCommandListener(this);
            clearAllAlert.setTimeout(Alert.FOREVER);//GEN-END:|71-getter|1|71-postInit
            // write post-init user code here
        }//GEN-BEGIN:|71-getter|2|
        return clearAllAlert;
    }
    //</editor-fold>//GEN-END:|71-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: clearAllForm ">//GEN-BEGIN:|84-getter|0|84-preInit
    /**
     * Returns an initiliazed instance of clearAllForm component.
     * @return the initialized component instance
     */
    Form getClearAllForm() {
        if (clearAllForm == null) {//GEN-END:|84-getter|0|84-preInit
            // write pre-init user code here
            clearAllForm = new Form("\u041E\u0442\u0447\u0438\u0441\u0438\u0442\u044C \u0432\u0441\u0451", new Item[] { getClearAllConfirmationTextField() });//GEN-BEGIN:|84-getter|1|84-postInit
            clearAllForm.addCommand(getCancelCommand());
            clearAllForm.addCommand(getOkCommand());
            clearAllForm.setCommandListener(this);//GEN-END:|84-getter|1|84-postInit
            // write post-init user code here
        }//GEN-BEGIN:|84-getter|2|
        return clearAllForm;
    }
    //</editor-fold>//GEN-END:|84-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: clearAllConfirmationTextField ">//GEN-BEGIN:|90-getter|0|90-preInit
    /**
     * Returns an initiliazed instance of clearAllConfirmationTextField component.
     * @return the initialized component instance
     */
    TextField getClearAllConfirmationTextField() {
        if (clearAllConfirmationTextField == null) {//GEN-END:|90-getter|0|90-preInit
            // write pre-init user code here
            clearAllConfirmationTextField = new TextField("\u0414\u043B\u044F \u043F\u043E\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043D\u0438\u044F \u043E\u0442\u0447\u0438\u0442\u0441\u0442\u043A\u0438 \u0432\u0432\u0435\u0434\u0438\u0442\u0435 1234", null, 4, TextField.NUMERIC | TextField.NON_PREDICTIVE);//GEN-LINE:|90-getter|1|90-postInit
            // write post-init user code here
        }//GEN-BEGIN:|90-getter|2|
        return clearAllConfirmationTextField;
    }
    //</editor-fold>//GEN-END:|90-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: presetTransferNameForm ">//GEN-BEGIN:|91-getter|0|91-preInit
    /**
     * Returns an initiliazed instance of presetTransferNameForm component.
     * @return the initialized component instance
     */
    Form getPresetTransferNameForm() {
        if (presetTransferNameForm == null) {//GEN-END:|91-getter|0|91-preInit
            // write pre-init user code here
            presetTransferNameForm = new Form("\u041E\u0441\u043D\u043E\u0432\u043D\u044B\u0435 \u043F\u0430\u0440\u0430\u043C\u0435\u0442\u0440\u044B", new Item[] { getPresetTransferNameTextField(), getPresetTransferPriorityTextField() });//GEN-BEGIN:|91-getter|1|91-postInit
            presetTransferNameForm.addCommand(getBackCommand());
            presetTransferNameForm.addCommand(getNextCommand());
            presetTransferNameForm.setCommandListener(this);//GEN-END:|91-getter|1|91-postInit
            // write post-init user code here
        }//GEN-BEGIN:|91-getter|2|
        return presetTransferNameForm;
    }
    //</editor-fold>//GEN-END:|91-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: presetTransferNameTextField ">//GEN-BEGIN:|100-getter|0|100-preInit
    /**
     * Returns an initiliazed instance of presetTransferNameTextField component.
     * @return the initialized component instance
     */
    TextField getPresetTransferNameTextField() {
        if (presetTransferNameTextField == null) {//GEN-END:|100-getter|0|100-preInit
            // write pre-init user code here
            presetTransferNameTextField = new TextField("\u0418\u043C\u044F", null, 64, TextField.ANY);//GEN-LINE:|100-getter|1|100-postInit
            // write post-init user code here
        }//GEN-BEGIN:|100-getter|2|
        return presetTransferNameTextField;
    }
    //</editor-fold>//GEN-END:|100-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: presetTransferPriorityTextField ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of presetTransferPriorityTextField component.
     * @return the initialized component instance
     */
    TextField getPresetTransferPriorityTextField() {
        if (presetTransferPriorityTextField == null) {//GEN-END:|101-getter|0|101-preInit
            // write pre-init user code here
            presetTransferPriorityTextField = new TextField("\u041F\u0440\u0438\u043E\u0440\u0438\u0442\u0435\u0442", "0", 2, TextField.NUMERIC);//GEN-LINE:|101-getter|1|101-postInit
            // write post-init user code here
        }//GEN-BEGIN:|101-getter|2|
        return presetTransferPriorityTextField;
    }
    //</editor-fold>//GEN-END:|101-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sourceAccountsChoiceGroup ">//GEN-BEGIN:|102-getter|0|102-preInit
    /**
     * Returns an initiliazed instance of sourceAccountsChoiceGroup component.
     * @return the initialized component instance
     */
    ChoiceGroup getSourceAccountsChoiceGroup() {
        if (sourceAccountsChoiceGroup == null) {//GEN-END:|102-getter|0|102-preInit
            // write pre-init user code here
            sourceAccountsChoiceGroup = new ChoiceGroup("\u041E\u0442\u043F\u0440\u0430\u0432\u0438\u0442\u0435\u043B\u044C", Choice.POPUP);//GEN-LINE:|102-getter|1|102-postInit
            // write post-init user code here
        }//GEN-BEGIN:|102-getter|2|
        return sourceAccountsChoiceGroup;
    }
    //</editor-fold>//GEN-END:|102-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: destinationAccountsChoiceGroup ">//GEN-BEGIN:|103-getter|0|103-preInit
    /**
     * Returns an initiliazed instance of destinationAccountsChoiceGroup component.
     * @return the initialized component instance
     */
    ChoiceGroup getDestinationAccountsChoiceGroup() {
        if (destinationAccountsChoiceGroup == null) {//GEN-END:|103-getter|0|103-preInit
            // write pre-init user code here
            destinationAccountsChoiceGroup = new ChoiceGroup("\u041F\u043E\u043B\u0443\u0447\u0430\u0442\u0435\u043B\u044C", Choice.POPUP);//GEN-LINE:|103-getter|1|103-postInit
            // write post-init user code here
        }//GEN-BEGIN:|103-getter|2|
        return destinationAccountsChoiceGroup;
    }
    //</editor-fold>//GEN-END:|103-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: amountTextField ">//GEN-BEGIN:|104-getter|0|104-preInit
    /**
     * Returns an initiliazed instance of amountTextField component.
     * @return the initialized component instance
     */
    TextField getAmountTextField() {
        if (amountTextField == null) {//GEN-END:|104-getter|0|104-preInit
            // write pre-init user code here
            amountTextField = new TextField("\u0421\u0443\u043C\u043C\u0430", null, 13, TextField.DECIMAL | TextField.NON_PREDICTIVE);//GEN-LINE:|104-getter|1|104-postInit
            // write post-init user code here
        }//GEN-BEGIN:|104-getter|2|
        return amountTextField;
    }
    //</editor-fold>//GEN-END:|104-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: currenciesChoiceGroup ">//GEN-BEGIN:|105-getter|0|105-preInit
    /**
     * Returns an initiliazed instance of currenciesChoiceGroup component.
     * @return the initialized component instance
     */
    ChoiceGroup getCurrenciesChoiceGroup() {
        if (currenciesChoiceGroup == null) {//GEN-END:|105-getter|0|105-preInit
            // write pre-init user code here
            currenciesChoiceGroup = new ChoiceGroup("\u0412\u0430\u043B\u044E\u0442\u0430", Choice.POPUP);//GEN-LINE:|105-getter|1|105-postInit
            // write post-init user code here
        }//GEN-BEGIN:|105-getter|2|
        return currenciesChoiceGroup;
    }
    //</editor-fold>//GEN-END:|105-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: paymentTypeCodeTextField ">//GEN-BEGIN:|106-getter|0|106-preInit
    /**
     * Returns an initiliazed instance of paymentTypeCodeTextField component.
     * @return the initialized component instance
     */
    TextField getPaymentTypeCodeTextField() {
        if (paymentTypeCodeTextField == null) {//GEN-END:|106-getter|0|106-preInit
            // write pre-init user code here
            paymentTypeCodeTextField = new TextField("(\u0415)\u041A\u041D\u041F", null, 32, TextField.ANY | TextField.NON_PREDICTIVE);//GEN-LINE:|106-getter|1|106-postInit
            // write post-init user code here
        }//GEN-BEGIN:|106-getter|2|
        return paymentTypeCodeTextField;
    }
    //</editor-fold>//GEN-END:|106-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: waringImage ">//GEN-BEGIN:|123-getter|0|123-preInit
    /**
     * Returns an initiliazed instance of waringImage component.
     * @return the initialized component instance
     */
    Image getWaringImage() {
        if (waringImage == null) {//GEN-END:|123-getter|0|123-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|123-getter|1|123-@java.io.IOException
                waringImage = Image.createImage("/kz/paysoft/paymobile/midlet/Warning.png");
            } catch (java.io.IOException e) {//GEN-END:|123-getter|1|123-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|123-getter|2|123-postInit
            // write post-init user code here
        }//GEN-BEGIN:|123-getter|3|
        return waringImage;
    }
    //</editor-fold>//GEN-END:|123-getter|3|

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    private Display getDisplay() {
        return Display.getDisplay(midlet);
    }

    void init() throws RecordStoreException, IOException {
        refreshPresetTransfersList();
        initAccoutsChoiceGroups();
        midlet.fillCurrenciesChoiceGroup(getCurrenciesChoiceGroup());
    }

    void initAccoutsChoiceGroups() {
        try {
            midlet.fillSourceAccoutsChoiceGroup(getSourceAccountsChoiceGroup());
            midlet.fillDestinationAccoutsChoiceGroup(getDestinationAccountsChoiceGroup());
        } catch (IOException ex) {
            midlet.showError("S-006\n" + ex.getMessage());
        } catch (RecordStoreException ex) {
            midlet.showError("S-007\n" + ex.getMessage());
        }
    }

    void cleanUp() {
    }

    private boolean canDeletePresetTransfer() {
        return presetTransfersList.getSelectedIndex() == -1 ? false : true;
    }

    private void deletePresetTransfer() throws RecordStoreException {
        PayMobile.presetTransferSearchName = presetTransfersList.getString(presetTransfersList.getSelectedIndex());
        RecordEnumeration re = PayMobile.presetTransfersStore.enumerateRecords(midlet, null, false);
        if (re.hasNextElement()) {
            PayMobile.presetTransfersStore.deleteRecord(re.nextRecordId());
        }
        re.destroy();
    }

    private boolean nameIsUsed() throws RecordStoreNotOpenException {
        if (!creatingNewTransfer && presetTransferNameTextField.getString().equals(presetTransfersList.getString(presetTransfersList.getSelectedIndex()))) {
            return false;
        }
        PayMobile.presetTransferSearchName = presetTransferNameTextField.getString();
        RecordEnumeration re = PayMobile.presetTransfersStore.enumerateRecords(midlet, null, false);
        boolean result = re.hasNextElement();
        re.destroy();
        return result;
    }

    private void savePresetTransfer() throws IOException, RecordStoreException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        String priority = presetTransferPriorityTextField.getString();
        if (priority.equals("")) {
            priority = "0";
        }
        dos.writeByte(Byte.parseByte(priority));
        dos.writeUTF(presetTransferNameTextField.getString());
        dos.writeUTF(sourceAccountsChoiceGroup.getString(sourceAccountsChoiceGroup.getSelectedIndex()));
        dos.writeUTF(destinationAccountsChoiceGroup.getString(destinationAccountsChoiceGroup.getSelectedIndex()));
        dos.writeUTF(amountTextField.getString());
        dos.writeUTF(currenciesChoiceGroup.getString(currenciesChoiceGroup.getSelectedIndex()));
        dos.writeUTF(paymentTypeCodeTextField.getString());
        dos.writeUTF(descriptionTextBox.getString());

        byte data[] = baos.toByteArray();
        if (creatingNewTransfer) {
            PayMobile.presetTransfersStore.addRecord(data, 0, data.length);
        } else {
            PayMobile.presetTransfersStore.setRecord(currentPresetTransferId, data, 0, data.length);
        }
        refreshPresetTransfersList();
    }

    private String getPresetTransferName(byte rec[]) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(rec);
        DataInputStream dis = new DataInputStream(bais);
        dis.readByte();
        return dis.readUTF();
    }

    private void refreshPresetTransfersList() throws RecordStoreException, IOException {
        getPresetTransfersList().deleteAll();
        midlet.operations.getPresetTransfersList().deleteAll();
        RecordEnumeration re = PayMobile.presetTransfersStore.enumerateRecords(null, midlet, false);
        while (re.hasNextElement()) {
            String name = getPresetTransferName(re.nextRecord());
            presetTransfersList.append(name, null);
            midlet.operations.getPresetTransfersList().append(name, null);
        }
        re.destroy();
    }

    private void fillPresetTransfer() throws RecordStoreException, IOException {
        PayMobile.presetTransferSearchName = presetTransfersList.getString(presetTransfersList.getSelectedIndex());
        RecordEnumeration re = PayMobile.presetTransfersStore.enumerateRecords(midlet, null, false);
        currentPresetTransferId = re.nextRecordId();
        re.destroy();

        ByteArrayInputStream bais = new ByteArrayInputStream(PayMobile.presetTransfersStore.getRecord(currentPresetTransferId));
        DataInputStream dis = new DataInputStream(bais);

        getPresetTransferPriorityTextField().setString(String.valueOf(dis.readByte()));
        getPresetTransferNameTextField().setString(dis.readUTF());
        midlet.selecteChoiceGroupElement(getSourceAccountsChoiceGroup(), dis.readUTF());
        midlet.selecteChoiceGroupElement(getDestinationAccountsChoiceGroup(), dis.readUTF());
        getAmountTextField().setString(dis.readUTF());
        midlet.selecteChoiceGroupElement(getCurrenciesChoiceGroup(), dis.readUTF());
        getPaymentTypeCodeTextField().setString(dis.readUTF());
        getDescriptionTextBox().setString(dis.readUTF());
    }

    private void clearPresetTransferFields() {
        presetTransferNameTextField.setString("");
        presetTransferPriorityTextField.setString("0");
        sourceAccountsChoiceGroup.setSelectedIndex(0, true);
        destinationAccountsChoiceGroup.setSelectedIndex(0, true);
        getAmountTextField().setString("");
        getCurrenciesChoiceGroup().setSelectedIndex(0, true);
        getPaymentTypeCodeTextField().setString("");
        getDescriptionTextBox().setString("");
    }
}
