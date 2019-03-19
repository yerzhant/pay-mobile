/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: H.
 * Last error index: 003.
 *
 * 2010.03.27   Yerzhan Tulepov         Moved here from a test project.
 * 2010.03.11   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

/**
 * A reduced version of HMAC, as defined in FIPS-198-1, in that the input text is
 * limited to up to 2^31-1-512 bits.
 *
 * @author Yerzhan Tulepov
 */
final class HMAC {
    private static byte key0[], data[], hash[];

    static final void cleanUp() {
        PayMobile.clearBuffer(key0);
        key0 = null;
        PayMobile.clearBuffer(data);
        data = null;
        PayMobile.clearBuffer(hash);
        hash = null;
    }

    static final byte[] hmac(byte sai, byte[] key, byte[] message, boolean clearKey, boolean clearMessage) throws HMACException, SHA256Exception {
        if (key.length == 0) {
            throw new HMACException("H-001\n"); // The key is empty.
        }
        if (message.length == 0) {
            throw new HMACException("H-002\n"); // The message is empty.
        }

        switch (sai) {
            case Service.SAI_HMAC_SHA_256:
                if (key.length > 64) {
                    key0 = SHA256.hash(key, clearKey);
                } else {
                    key0 = key;
                }
                data = new byte[64 + message.length];
                //System.arraycopy(key0, 0, data, 0, key0.length); May provide better perfomance, but from security perspective shouldn't be used.
                for (int i = 0; i < key0.length; i++) {
                    data[i] = key0[i];
                }
                //System.arraycopy(message, 0, data, 64, message.length); May provide better perfomance, but from security perspective shouldn't be used.
                for (int i = 0; i < message.length; i++) {
                    data[i + 64] = message[i];
                }
                xor(data, (byte) 0x36);
                hash = SHA256.hash(data, true);
                data = new byte[64 + 32];
                //System.arraycopy(key0, 0, data, 0, key0.length); May provide better perfomance, but from security perspective shouldn't be used.
                for (int i = 0; i < key0.length; i++) {
                    data[i] = key0[i];
                }
                //System.arraycopy(hash, 0, data, 64, 32); May provide better perfomance, but from security perspective shouldn't be used.
                for (int i = 0; i < 32; i++) {
                    data[i + 64] = hash[i];
                }
                xor(data, (byte) 0x5c);
                PayMobile.clearBuffer(hash);
                hash = SHA256.hash(data, true);
                data = null;
                if (key.length > 64) {
                    PayMobile.clearBuffer(key0);
                    key0 = null;
                }
                if (clearKey) {
                    PayMobile.clearBuffer(key);
                }
                if (clearMessage) {
                    PayMobile.clearBuffer(message);
                }
                return hash;

            default:
                throw new HMACException("H-003\n"); // Unsupported SAI.
        }
    }

    private static final void xor(byte[] data, byte b) {
        for (int i = 0; i < 64; i++) {
            data[i] ^= b;
        }
    }
}
