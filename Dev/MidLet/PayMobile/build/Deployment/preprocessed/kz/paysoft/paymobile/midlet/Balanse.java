/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: B.
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
final class Balanse extends Service {

    private static final byte TYPE_BALANSE = 5;

    public void run() {
        try {
            Operations.showResponseData = true;
            waitForProcessing();
            PayMobile.payMobile.operations.getResponseDataStringItem().setText(inStream.readUTF());
            PayMobile.payMobile.operations.showResponseData();
        } catch (SecurityException ex) {
            PayMobile.payMobile.showError("B-001\n" + ex.getMessage());
        } catch (ConnectionNotFoundException ex) {
            PayMobile.payMobile.showError("B-002\n" + ex.getMessage());
        } catch (RecordStoreException ex) {
            PayMobile.payMobile.showError("B-003\n" + ex.getMessage());
        } catch (IOException ex) {
            if (connected) {
                PayMobile.payMobile.showError("B-004\n" + ex.getMessage());
            }
        } catch (InterruptedException ex) {
        } catch (Exception ex) {
            PayMobile.payMobile.showError("B-005\n" + ex.getMessage());
        } finally {
            PayMobile.payMobile.cancelRequestProcessingForm();
            cleanUpAndDisconnect();
        }
    }

    void sendBody(DataOutputStream toSign) throws IOException {
        toSign.writeUTF(sourceAccount);
        toSign.writeByte(TYPE_BALANSE);
        outStream.writeByte(TYPE_BALANSE);
        outStream.writeUTF(sourceAccount);
    }
}
