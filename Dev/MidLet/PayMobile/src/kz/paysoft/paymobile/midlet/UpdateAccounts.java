/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: U.
 * Last error index: 006.
 *
 * 2010.03.21   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Yerzhan Tulepov.
 */
final class UpdateAccounts extends Service {

    private static final String ACCOUNTS_ARE_UPDATED = "Счета обновлены.";
    private static final byte TYPE_UPDATE_ACCOUNTS = 6;

    public void run() {
        try {
            Operations.showResponseData = false;
            waitForProcessing();
            updateStores();
            PayMobile.payMobile.operations.initAccountsChoiceGroups();
            PayMobile.payMobile.setup.initAccoutsChoiceGroups();
            if (Operations.returnToSetup) {
                PayMobile.payMobile.switchDisplayable(PayMobile.payMobile.getGeneralAlertAsInfo(ACCOUNTS_ARE_UPDATED), PayMobile.payMobile.setup.getSettingsList());
            } else {
                PayMobile.payMobile.setResponseAsInfo(ACCOUNTS_ARE_UPDATED);
                PayMobile.payMobile.operations.showResponseData();
            }
        } catch (SecurityException ex) {
            PayMobile.payMobile.showError("U-001\n" + ex.getMessage());
        } catch (ConnectionNotFoundException ex) {
            PayMobile.payMobile.showError("U-002\n" + ex.getMessage());
        } catch (RecordStoreException ex) {
            PayMobile.payMobile.showError("U-003\n" + ex.getMessage());
        } catch (IOException ex) {
            if (connected) {
                PayMobile.payMobile.showError("U-004\n" + ex.getMessage());
            }
        } catch (InterruptedException ex) {
        } catch (Exception ex) {
            PayMobile.payMobile.showError("U-005\n" + ex.getMessage());
        } finally {
            PayMobile.payMobile.cancelRequestProcessingForm();
            cleanUpAndDisconnect();
        }
    }

    void sendBody(DataOutputStream toSign) throws IOException {
        toSign.writeByte(TYPE_UPDATE_ACCOUNTS);
        outStream.writeByte(TYPE_UPDATE_ACCOUNTS);
    }

    private void updateStores() throws IOException, RecordStoreException, Exception {
        int quantity = inStream.readUnsignedShort();
        PayMobile.cleanUpAccountsStores();
        for (int i = 0; i < quantity; i++) {
            byte type = inStream.readByte();
            switch (type) {
                case 'S':
                    addSourceAccout(inStream.readUTF());
                    break;

                case 'R':
                    addDestinationAccout(inStream.readUTF());
                    break;

                default:
                    throw new Exception("U-006\n" + SYSTEM_ERROR);
            }
        }
    }

    private static void addSourceAccout(String name) throws IOException, RecordStoreException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(name);
        byte buf[] = baos.toByteArray();
        PayMobile.sourceAccoutsStore.addRecord(buf, 0, buf.length);
    }

    private static void addDestinationAccout(String name) throws IOException, RecordStoreException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(name);
        byte buf[] = baos.toByteArray();
        PayMobile.destinationAccountsStore.addRecord(buf, 0, buf.length);
    }

//#mdebug
    static void debugInit() throws IOException, RecordStoreException {
        addSourceAccout("SName 1");
        addSourceAccout("SName 22");
        addSourceAccout("SName 333");

        addDestinationAccout("DName 1");
        addDestinationAccout("DName 22");
        addDestinationAccout("DName 333");
        addDestinationAccout("DName 4444");
    }
//#enddebug
}
