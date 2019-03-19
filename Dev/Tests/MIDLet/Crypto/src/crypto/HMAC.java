package crypto;

/**
 * A reduced version of HMAC, as defined in FIPS-198-1, in that the input text is
 * limited to up to 2^31-1-512 bits.
 * 
 * 11.03.2010   Created.
 *
 * @author Yerzhan Tulepov
 */
final class HMAC {

    private static final int SAI_HMAC_SHA_256 = 1;

    HMAC() {
    }

    static byte[] hmac(int sai, byte[] key, byte[] message, boolean cleanMessage) throws HMACException, SHA256Exception {
        if (key.length == 0) {
            throw new HMACException("E-S-L"); // The key is empty.
        }
        if (message.length == 0) {
            throw new HMACException("E-S-L"); // The message is empty.
        }

        switch (sai) {
            case SAI_HMAC_SHA_256:
                byte key0[];
                if (key.length > 64) {
                    key0 = SHA256.hash(key);
                } else {
                    key0 = key;
                }

                byte data[] = new byte[64 + message.length];
                //System.arraycopy(key0, 0, data, 0, key0.length); May provide better perfomance, but from security perspective shouldn't be used.
                for (int i = 0; i < key0.length; i++) {
                    data[i] = key0[i];
                }
                //System.arraycopy(message, 0, data, 64, message.length); May provide better perfomance, but from security perspective shouldn't be used.
                for (int i = 0; i < message.length; i++) {
                    data[i + 64] = message[i];
                }
                xor(data, (byte) 0x36);
                byte hash[] = SHA256.hash(data);

                // Cleanup
                for (int i = 0; i < data.length; i++) {
                    data[i] = 0;
                }

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

                // Cleanup
                for (int i = 0; i < 32; i++) {
                    hash[i] = 0;
                }

                hash = SHA256.hash(data);

                // Cleanup
                for (int i = 0; i < (64 + 32); i++) {
                    data[i] = 0;
                }

                // Cleanup
                if (key.length > 64) {
                    for (int i = 0; i < 32; i++) {
                        key0[i] = 0;
                    }
                }

                if (cleanMessage) {
                    for (int i = 0; i < message.length; i++) {
                        message[i] = 0;
                    }
                }

                return hash;

            default:
                throw new HMACException("E-SRC_CODE-LINE_NUMBER"); // Unsupported SAI.
        }
    }

    private static void xor(byte[] data, byte b) {
        for (int i = 0; i < 64; i++) {
            data[i] ^= b;
        }
    }
}
