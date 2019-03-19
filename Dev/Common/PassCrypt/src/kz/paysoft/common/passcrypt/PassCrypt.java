/*
 * Copyright (C) 2009-2010 Yerzhan Tulepov. All rights reserved.
 *
 * PassCrypt - Multipurpose password encryptator.
 *
 * 2010.04.13   Yerzhan Tulepov         Migrated from PaySys (SmartKee) and little bit modified.
 * 05.02.2009	1.1.0		Yerzhan Tulepov		Created.
 */

package kz.paysoft.common.passcrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.math.BigInteger;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


final class PassCrypt {
    private static final String START_TEXT = "PassCrypt 1.1.0\nCopyright (C) 2009-2010 Yerzhan Tulepov. All rights reserved.\nUsage: PassCrypt [echo]";

    private static final byte[] SECRET_KEY_ALGORITHM = { -69, -70, -84, -102, -101, -102 };
    private static final byte[] SECRET_TRANSFORMATION = { -69, -70, -84, -102, -101, -102, -48, -68, -67, -68, -48, -81, -76, -68, -84, -54, -81, -98, -101, -101, -106, -111, -104 };
    private static final byte[] KEY = { -45, -3, -122, 103, -33, 7, -43, -75, -32, -32, -116, 13, 81, 87, -62, 118, 100, -77, 41, 121, -53, 13, 25, -27 };

    public static void main(String[] args) throws Exception {
        System.out.println(START_TEXT);
        byte[] password;
        if (args.length == 1 && args[0].equals("echo")) {
            password = getPasswordFromSystemIn();
        } else {
            password = getPasswordFromConsole();
            if (password.length == 0) {
                System.exit(-1);
            }
        }
        DESedeKeySpec dks = new DESedeKeySpec(KEY);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(bytesToString(SECRET_KEY_ALGORITHM));
        SecretKey sk = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(bytesToString(SECRET_TRANSFORMATION));
        cipher.init(cipher.ENCRYPT_MODE, sk);
        System.out.println("a" + ": " + new BigInteger(cipher.doFinal(password)));
        Arrays.fill(password, (byte)0);
        System.out.println("b" + ": " + new BigInteger(cipher.getParameters().getEncoded()));
    }

    private static final byte[] getPasswordFromSystemIn() throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter password: ");
        String password = stdIn.readLine();
        return toBytes(password.toCharArray());
    }

    private static final byte[] getPasswordFromConsole() throws IOException {
        System.out.print("Enter password: ");
        char[] password = System.console().readPassword();
        System.out.print("Repeat password: ");
        char[] passwordConfirm = System.console().readPassword();
        if (!Arrays.equals(password, passwordConfirm)) {
            Arrays.fill(password, '\u0000');
            Arrays.fill(passwordConfirm, '\u0000');
            System.out.println("Passwords are different.");
            return new byte[0];
        }
        return toBytes(password);
    }

    private static final byte[] toBytes(char[] password) {
        byte[] buf = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            buf[i * 2] = (byte)(password[i] >>> 8);
            buf[i * 2 + 1] = (byte)password[i];
            password[i] = 0;
        }
        return buf;
    }

    private static final String bytesToString(byte[] bytes) {
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte)~bytes[i];
        }
        return new String(buf);
    }
}
