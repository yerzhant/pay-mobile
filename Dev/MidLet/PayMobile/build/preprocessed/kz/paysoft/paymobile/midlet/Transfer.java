/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: T.
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
final class Transfer extends Service {

    private static final byte TYPE_TRANSFER = 3;
    private static final String REQUEST_WAS_SENT = "Запрос отправлен.";

    public void run() {
        try {
            Operations.showResponseData = false;
            connectAndGetRandomNumber();
            signSendAndFlush();
            processRC();
            readIdAndSendSMS();
            PayMobile.payMobile.setResponseAsInfo(REQUEST_WAS_SENT);
            PayMobile.payMobile.operations.showResponseData();
        } catch (SecurityException ex) {
            PayMobile.payMobile.showError("T-001\n" + ex.getMessage());
        } catch (ConnectionNotFoundException ex) {
            PayMobile.payMobile.showError("T-002\n" + ex.getMessage());
        } catch (RecordStoreException ex) {
            PayMobile.payMobile.showError("T-003\n" + ex.getMessage());
        } catch (IOException ex) {
            if (connected) {
                PayMobile.payMobile.showError("T-004\n" + ex.getMessage());
            }
        } catch (InterruptedException ex) {
        } catch (Exception ex) {
            PayMobile.payMobile.showError("T-005\n" + ex.getMessage());
        } finally {
            PayMobile.payMobile.cancelRequestProcessingForm();
            cleanUpAndDisconnect();
        }
    }

    void sendBody(DataOutputStream toSign) throws IOException {
        toSign.writeLong(amount);
        toSign.writeUTF(currency);
        toSign.writeUTF(paymentTypeCode);
        toSign.writeUTF(description);
        toSign.writeUTF(sourceAccount);
        toSign.writeUTF(destinationAccount);
        toSign.writeByte(TYPE_TRANSFER);
        outStream.writeByte(TYPE_TRANSFER);
        outStream.writeUTF(sourceAccount);
        outStream.writeUTF(destinationAccount);
        outStream.writeLong(amount);
        outStream.writeUTF(currency);
        outStream.writeUTF(paymentTypeCode);
        outStream.writeUTF(description);
    }
}
