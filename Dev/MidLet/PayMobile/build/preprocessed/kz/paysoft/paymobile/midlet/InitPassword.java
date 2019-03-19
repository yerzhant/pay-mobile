/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: I.
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
final class InitPassword extends Service {

    private static final String INITIALIZED_SUCCESSFULLY = "Инициализация прошла успешно.";
    private static final byte TYPE_INITIALIZATION = 0;
    private static final byte TYPE_PASSWORD = 1;
    private static boolean initializing = false;
    private static boolean disconnect;

    public void run() {
        disconnect = true;
        try {
            if (!initializing) {
                connect();
                sendRequest(TYPE_INITIALIZATION, false);
                outStream.flush();
                processRC();
            }
            outStream.writeByte(TYPE_PASSWORD);
            byte passwordLength = (byte) password.length;
            outStream.writeByte(passwordLength);
            for (int i = 0; i < passwordLength; i++) {
                outStream.writeChar(password[i]);
            }
            outStream.flush();
            initializing = true;
            processRC();
            storeInitValues();
            PayMobile.payMobile.setResponseAsInfo(INITIALIZED_SUCCESSFULLY);
            PayMobile.passwordInitResult = PayMobile.PASSWORD_ACCEPTED;
            PayMobile.payMobile.passwordInitResult();
        } catch (PasswordValidationException ex) {
            disconnect = false;
            PayMobile.passwordInitResult = PayMobile.PASSWORD_ERROR;
            PayMobile.payMobile.passwordInitResult();
        } catch (SecurityException ex) {
            PayMobile.payMobile.showError("I-001\n" + ex.getMessage());
            PayMobile.passwordInitResult = PayMobile.ANY_OTHER_ERROR;
            PayMobile.payMobile.passwordInitResult();
        } catch (ConnectionNotFoundException ex) {
            PayMobile.payMobile.showError("I-002\n" + ex.getMessage());
            PayMobile.passwordInitResult = PayMobile.ANY_OTHER_ERROR;
            PayMobile.payMobile.passwordInitResult();
        } catch (IOException ex) {
            if (connected) {
                PayMobile.payMobile.showError("I-003\n" + ex.getMessage());
                PayMobile.passwordInitResult = PayMobile.ANY_OTHER_ERROR;
                PayMobile.payMobile.passwordInitResult();
            }
        } catch (RecordStoreException ex) {
            PayMobile.payMobile.showError("I-004\n" + ex.getMessage());
            PayMobile.passwordInitResult = PayMobile.ANY_OTHER_ERROR;
            PayMobile.payMobile.passwordInitResult();
        } catch (Exception ex) {
            PayMobile.payMobile.showError("I-005\n" + ex.getMessage());
            PayMobile.passwordInitResult = PayMobile.ANY_OTHER_ERROR;
            PayMobile.payMobile.passwordInitResult();
        } finally {
            PayMobile.payMobile.cancelRequestProcessingForm();
            if (disconnect) {
                cleanUpAndDisconnect();
            }
        }
    }

    void sendBody(DataOutputStream toSign) throws IOException {
    }
}
