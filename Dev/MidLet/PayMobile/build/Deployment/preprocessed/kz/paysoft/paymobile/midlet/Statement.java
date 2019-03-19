/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: M.
 * Last error index: 005.
 *
 * 2010.03.21   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Yerzhan Tulepov.
 */
final class Statement extends Service {

    private static final byte TYPE_STATEMENT = 4;
    static byte statementType;

    public void run() {
        try {
            Operations.showResponseData = true;
            waitForProcessing();
            PayMobile.payMobile.operations.getResponseDataStringItem().setText(inStream.readUTF());
            PayMobile.payMobile.operations.showResponseData();
        } catch (SecurityException ex) {
            PayMobile.payMobile.showError("M-001\n" + ex.getMessage());
        } catch (ConnectionNotFoundException ex) {
            PayMobile.payMobile.showError("M-002\n" + ex.getMessage());
        } catch (RecordStoreException ex) {
            PayMobile.payMobile.showError("M-003\n" + ex.getMessage());
        } catch (IOException ex) {
            if (connected) {
                PayMobile.payMobile.showError("M-004\n" + ex.getMessage());
            }
        } catch (InterruptedException ex) {
        } catch (Exception ex) {
            PayMobile.payMobile.showError("M-005\n" + ex.getMessage());
        } finally {
            PayMobile.payMobile.cancelRequestProcessingForm();
            cleanUpAndDisconnect();
        }
    }

    void sendBody(DataOutputStream toSign) throws IOException {
        toSign.writeUTF(sourceAccount);
        toSign.writeByte(statementType);
        toSign.writeByte(TYPE_STATEMENT);
        outStream.writeByte(TYPE_STATEMENT);
        outStream.writeUTF(sourceAccount);
        outStream.writeByte(statementType);
    }
}
