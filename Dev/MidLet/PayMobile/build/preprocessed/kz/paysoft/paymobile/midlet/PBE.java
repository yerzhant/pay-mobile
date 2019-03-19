/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: W.
 * Last error index: 004.
 *
 * 2010.03.27   Yerzhan Tulepov         Moved here from a test project.
 * 2010.03.11   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

/**
 * This algorithm is implimented as in PKCS #5 v2.1, except, as 1 iteration is
 * sufficient, that I do not use i, but instead hLen is appended, where L is 0x00a0
 * for SHA-1 and 0x0100 for SHA-256.
 * 
 * @author Yerzhan Tulepov.
 */
final class PBE {

    private static byte message[], bytePassword[];

    static final void cleanUp() {
        PayMobile.clearBuffer(message);
        message = null;
        PayMobile.clearBuffer(bytePassword);
        bytePassword = null;
    }

    static final byte[] pbe(int sai, char password[], byte salt[], int c, boolean clearPassword) throws PBEException, HMACException, SHA256Exception, InterruptedException {
        bytePassword = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            bytePassword[i * 2] = (byte) (password[i] >>> 8);
            bytePassword[i * 2 + 1] = (byte) password[i];
        }
        byte result[] = pbe(sai, bytePassword, salt, c, true);
        bytePassword = null;
        if (clearPassword) {
            PayMobile.clearBuffer(password);
        }
        cleanUp();
        return result;
    }

    static final byte[] pbe(int sai, byte[] password, byte[] salt, int c, boolean clearPassword) throws PBEException, HMACException, SHA256Exception, InterruptedException {
        if (password.length == 0) {
            throw new PBEException("W-001\n"); // The password is empty.
        }
        if (salt.length == 0) {
            throw new PBEException("W-002\n"); // The salt is empty.
        }
        if (c == 0) {
            throw new PBEException("W-003\n"); // The c is zero.
        }

        switch (sai) {
            case Service.SAI_HMAC_SHA_256:
                message = new byte[salt.length + 2];
                for (int i = 0; i < salt.length; i++) {
                    message[i] = salt[i];
                }
                message[salt.length] = 1; // L = 0x0100.
                message = HMAC.hmac(Service.SAI_HMAC_SHA_256, password, message, false, true);
                byte[] kk = new byte[message.length];
                for (int i = 0; i < kk.length; i++) {
                    kk[i] = message[i];
                }
                for (int i = 0; i < c; i++) {
                    message = HMAC.hmac(Service.SAI_HMAC_SHA_256, password, message, false, true);
                    for (int j = 0; j < kk.length; j++) {
                        kk[j] ^= message[j];
                    }
                    if (!PayMobile.requestProcessingThread.isAlive()) {
                        if (clearPassword) {
                            PayMobile.clearBuffer(password);
                        }
                        cleanUp();
                        throw new InterruptedException();
                    }
                }
                if (clearPassword) {
                    PayMobile.clearBuffer(password);
                }
                cleanUp();
                return kk;

            default:
                throw new PBEException("W-004\n"); // Unsupported SAI.

        }
    }
}
