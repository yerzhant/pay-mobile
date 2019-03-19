package crypto;

/**
 * This algorithm is implimented as in PKCS #5 v2.1, except, as 1 iteration is
 * sufficient, that I do not use i, but instead hLen is appended, where L is 0x00a0
 * for SHA-1 and 0x0100 for SHA-256.
 * 
 * 11.03.2010   Created.
 *
 * @author Yerzhan Tulepov.
 */
final class PBE {

    private static final int SAI_HMAC_SHA_256 = 1;

    PBE() {
    }

    static byte[] pbe(int sai, byte[] password, byte[] salt, int c) throws PBEException, HMACException, SHA256Exception {
        if (password.length == 0) {
            throw new PBEException("E-S-L"); // The password is empty.
        }
        if (salt.length == 0) {
            throw new PBEException("E-S-L"); // The salt is empty.
        }
        if (c == 0) {
            throw new PBEException("E-S-L"); // The c is zero.
        }

        switch (sai) {
            case SAI_HMAC_SHA_256:
                byte message[] = new byte[salt.length + 2];
                for (int i = 0; i < salt.length; i++) {
                    message[i] = salt[i];
                }
                message[salt.length] = 1;
                byte U[] = HMAC.hmac(SAI_HMAC_SHA_256, password, message, true);
                for (int i = 0; i < c; i++) {
                    U = HMAC.hmac(SAI_HMAC_SHA_256, password, U, true);
                }
                return U;

            default:
                throw new PBEException("E-SRC_CODE-LINE_NUMBER"); // Unsupported SAI.

        }
    }
}
