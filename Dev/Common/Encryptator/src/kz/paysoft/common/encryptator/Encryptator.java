/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * Encryptator - creates keys and encrypts files for further usage in projects so that private specification/licensing/etc.
 * 		 information was closed for customers.
 *
 * 2010.04.13   Yezhan Tulepov      Migrated from PaySys (SmartKee) and slightly beautified.
 * 19.04.2008   1.0.0.0		Yerzhan Tulepov         Created.
 */

package kz.paysoft.common.encryptator;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.math.BigInteger;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


final class Encryptator {
    private static final String START_TEXT = "Encryptator 1.2.0\nCopyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.\n";
    private static final String USAGE_TEXT = "Usage: Encryptator <command> <key file | string> [input file] [output file]\nWhere:\n" +
        "<command>:\tc - create keys, e - encrypt a file, i - invert a string.\n<key file>:\tA keys file.\n" +
        "[input file]:\tA file for encryption.\n[output file]:\tAn encrypted file.";
    private static final String SECRET_KEY_ALGORITHM = "DESede";
    private static final int SECRET_KEY_LENGTH = 168;
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";
    private static final int PUBLIC_KEY_LENGTH = 2048;
    private static final String SECRET_TRANSFORMATION = SECRET_KEY_ALGORITHM + "/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM_PROPERTY_KEY = "SecretKeyAlgorithm";
    private static final String SECRET_TRANSFORMATION_PROPERTY_KEY = "SecretTransformation";
    private static final String PUBLIC_KEY_ALGORITHM_PROPERTY_KEY = "PublicKeyAlgorithm";
    private static final String SECRET_ALGORITHM_PARAMETERS_PROPERTY_KEY = "SECRET_ALGORITHM_PARAMETERS";
    private static final String TRANSFORMED_SECRET_KEY_PROPERTY_KEY = "TRANSFORMED_SECRET_KEY";
    private static final String KEY_MODULUS_PROPERTY_KEY = "KeyModulus";
    private static final String PRIVATE_KEY_EXP_PROPERTY_KEY = "PrivateKeyExp";
    private static final String ENC_KEY_MODULUS_PROPERTY_KEY = "ENCRYPTED_MODULUS";
    private static final String ENC_PUBLIC_KEY_EXP_PROPERTY_KEY = "ENCRYPTED_EXPONENT";

    public static void main(String[] args) {
        System.out.println(START_TEXT);
        if (args.length == 2 && args[0].equals("i")) {
            try {
                convertString(args[1]);
            } catch (Exception e) {
                System.out.println("Unable to convert the string.");
                e.printStackTrace();
            }
        } else if (args.length == 2 && args[0].equals("c")) {
            try {
                generateKeys(args[1]);
                System.out.println("Keys has successfully been generated.");
            } catch (Exception e) {
                System.out.println("Unable to generate keys.");
                e.printStackTrace();
            }
        } else if (args.length == 4 && args[0].equals("e")) {
            try {
                encryptFile(args[1], args[2], args[3]);
                System.out.println("The file has successfully been encrypted.");
            } catch (Exception e) {
                System.out.println("Unable to encrypt the file.");
                e.printStackTrace();
            }
        } else {
            System.out.println(USAGE_TEXT);
        }
    }

    private static void convertString(String string) {
        byte[] buf = string.getBytes();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte)~buf[i];
        }
        System.out.println(toByteArrayString(buf));
    }

    private static final void generateKeys(String fileName) throws Exception {
        Properties props = new Properties();

        byte[] skaBytes = SECRET_KEY_ALGORITHM.getBytes();
        for (int i = 0; i < skaBytes.length; i++) {
            skaBytes[i] = (byte)~skaBytes[i];
        }
        props.setProperty(SECRET_KEY_ALGORITHM_PROPERTY_KEY, toByteArrayString(skaBytes));

        byte[] stBytes = SECRET_TRANSFORMATION.getBytes();
        for (int i = 0; i < stBytes.length; i++) {
            stBytes[i] = (byte)~stBytes[i];
        }
        props.setProperty(SECRET_TRANSFORMATION_PROPERTY_KEY, toByteArrayString(stBytes));

        byte[] pkaBytes = PUBLIC_KEY_ALGORITHM.getBytes();
        for (int i = 0; i < pkaBytes.length; i++) {
            pkaBytes[i] = (byte)~pkaBytes[i];
        }
        props.setProperty(PUBLIC_KEY_ALGORITHM_PROPERTY_KEY, toByteArrayString(pkaBytes));

        KeyGenerator keyGenerator = KeyGenerator.getInstance(SECRET_KEY_ALGORITHM);
        keyGenerator.init(SECRET_KEY_LENGTH);
        SecretKey secretKey = keyGenerator.generateKey();
        BigInteger transformedSecretKey = new BigInteger(secretKey.getEncoded());
        transformedSecretKey = transformedSecretKey.flipBit(7);
        transformedSecretKey = transformedSecretKey.flipBit(9);
        transformedSecretKey = transformedSecretKey.flipBit(10);
        transformedSecretKey = transformedSecretKey.flipBit(18);
        transformedSecretKey = transformedSecretKey.flipBit(26);
        transformedSecretKey = transformedSecretKey.flipBit(29);
        transformedSecretKey = transformedSecretKey.flipBit(35);
        transformedSecretKey = transformedSecretKey.flipBit(49);
        transformedSecretKey = transformedSecretKey.flipBit(67);
        transformedSecretKey = transformedSecretKey.flipBit(77);
        transformedSecretKey = transformedSecretKey.flipBit(92);
        transformedSecretKey = transformedSecretKey.flipBit(112);
        transformedSecretKey = transformedSecretKey.flipBit(144);
        transformedSecretKey = transformedSecretKey.flipBit(151);
        transformedSecretKey = transformedSecretKey.flipBit(177);
        transformedSecretKey = transformedSecretKey.flipBit(183);
        transformedSecretKey = transformedSecretKey.flipBit(190);
        props.setProperty(TRANSFORMED_SECRET_KEY_PROPERTY_KEY, toByteArrayString(transformedSecretKey.toByteArray()) + ";");

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(PUBLIC_KEY_ALGORITHM);
        keyPairGenerator.initialize(PUBLIC_KEY_LENGTH);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        KeyFactory keyFactory = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
        RSAPrivateKeySpec sks = keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);
        props.setProperty(KEY_MODULUS_PROPERTY_KEY, sks.getModulus().toString(16));
        props.setProperty(PRIVATE_KEY_EXP_PROPERTY_KEY, sks.getPrivateExponent().toString(16));

        Cipher cipher = Cipher.getInstance(SECRET_TRANSFORMATION);
        cipher.init(cipher.ENCRYPT_MODE, secretKey);
        RSAPublicKeySpec pks = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
        props.setProperty(ENC_KEY_MODULUS_PROPERTY_KEY, toByteArrayString(cipher.doFinal(pks.getModulus().toByteArray())) + ";");
        props.setProperty(ENC_PUBLIC_KEY_EXP_PROPERTY_KEY, toByteArrayString(cipher.doFinal(pks.getPublicExponent().toByteArray())) + ";");
        props.setProperty(SECRET_ALGORITHM_PARAMETERS_PROPERTY_KEY, toByteArrayString(cipher.getParameters().getEncoded()) + ";");

        FileOutputStream fos = new FileOutputStream(fileName);
        props.store(fos, "");
        fos.close();
    }

    private static final String toByteArrayString(byte[] bytes) {
        StringBuffer s = new StringBuffer("{");
        for (int i = 0; i < bytes.length; i++) {
            s.append(" " + Byte.toString(bytes[i]) + ",");
        }
        return s.substring(0, s.length() - 1) + " }";
    }

    private static final void encryptFile(String keysFile, String inFile, String outFile) throws Exception {
        FileInputStream kfis = new FileInputStream(keysFile);
        Properties props = new Properties();
        props.load(kfis);
        kfis.close();

        FileInputStream fis = new FileInputStream(inFile);
        FileOutputStream fos = new FileOutputStream(outFile);

        RSAPrivateKeySpec rpks = new RSAPrivateKeySpec(new BigInteger(props.getProperty(KEY_MODULUS_PROPERTY_KEY), 16), new BigInteger(props.getProperty(PRIVATE_KEY_EXP_PROPERTY_KEY), 16));
        KeyFactory keyFactory = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(rpks);

        Cipher cipher = Cipher.getInstance(PUBLIC_KEY_ALGORITHM);
        cipher.init(cipher.ENCRYPT_MODE, privateKey);

        byte[] buf = new byte[PUBLIC_KEY_LENGTH / 8 - 11];
        /*
         * If not to set 11, then, and I don't know why, the following exception will be thrown:
         * javax.crypto.IllegalBlockSizeException: Data must not be longer than 245 bytes
         *       at com.sun.crypto.provider.RSACipher.a(DashoA13*..)
         *       at com.sun.crypto.provider.RSACipher.engineDoFinal(DashoA13*..)
         *       at javax.crypto.Cipher.doFinal(DashoA13*..)
         *       at kz.paysoft.common.encryptator.Encryptator.encryptFile(Encryptator.java:167)
         *       at kz.paysoft.common.encryptator.Encryptator.main(Encryptator.java:64)
         */
        int i = fis.read(buf);
        while (i != -1) {
            fos.write(cipher.doFinal(buf, 0, i));
            i = fis.read(buf);
        }

        fis.close();
        fos.close();
        /*
		FileInputStream is = new FileInputStream("out.txt");
		FileOutputStream os = new FileOutputStream("clear.txt");
		byte[] apb = { 4, 8, 65, 86, 76, -36, 40, 99, -106, -5 };
		byte[] dkb = { -90, -92, 112, 31, -50, -83, 118, -39, -101, 90, -14, -125, 68, -33, -24, 18, 94, 66, -95, 123, 115, 99, -42, 51 };
		byte[] eb = { -73, 82, 18, 29, -50, -100, -47, 126 };
		byte[] mb =
{ 52, 75, 90, -34, -126, -28, -124, 11, -122, 0, -111, -83, -50, 54, -15, 74, 46, 8, 11, 55, 32, -3, -14, -98, 101, -8, 6,
  119, -11, -24, -81, 88, -2, -1, 95, 123, 65, -95, 63, -72, 97, 81, -100, -87, -21, -125, 19, -62, 102, -10, -48, 14, -92,
  -47, -11, -73, 123, -67, -101, 47, 104, -112, -18, -23, -124, 121, -68, -121, 4, 40, 54, 90 };
		BigInteger osk = new BigInteger(dkb);
		osk = osk.flipBit(7);
		osk = osk.flipBit(9);
		osk = osk.flipBit(10);
		osk = osk.flipBit(18);
		osk = osk.flipBit(26);
		osk = osk.flipBit(29);
		osk = osk.flipBit(35);
		osk = osk.flipBit(49);
		osk = osk.flipBit(67);
		osk = osk.flipBit(77);
		osk = osk.flipBit(92);
		osk = osk.flipBit(112);
		osk = osk.flipBit(144);
		osk = osk.flipBit(151);
		osk = osk.flipBit(177);
		osk = osk.flipBit(183);
		osk = osk.flipBit(190);
		DESedeKeySpec dks = new DESedeKeySpec(osk.toByteArray());
		SecretKeyFactory skf = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
		SecretKey sk = skf.generateSecret(dks);
		AlgorithmParameters ap = AlgorithmParameters.getInstance(SECRET_KEY_ALGORITHM);
		ap.init(apb);
		Cipher c = Cipher.getInstance(SECRET_TRANSFORMATION);
		c.init(c.DECRYPT_MODE, sk, ap);
		BigInteger cm = new BigInteger(c.doFinal(mb));
		BigInteger ce = new BigInteger(c.doFinal(eb));
		RSAPublicKeySpec pks = new RSAPublicKeySpec(cm, ce);
		PublicKey pk = keyFactory.generatePublic(pks);
		Cipher pc = Cipher.getInstance(PUBLIC_KEY_ALGORITHM);
		pc.init(pc.DECRYPT_MODE, pk);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bb = new byte[PUBLIC_KEY_LENGTH / 8];
		i = is.read(bb);
		while (i != -1) {
			byte[] tbb = pc.doFinal(bb, 0, i);
			baos.write(tbb);
			os.write(tbb);
			i = is.read(bb);
		}

		System.out.print(baos.toString());

		is.close();
		os.close();
		*/
    }
}
