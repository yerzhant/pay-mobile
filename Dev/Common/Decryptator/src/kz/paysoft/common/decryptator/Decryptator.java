/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * Decrypts configuration files.
 *
 * 2010.04.12   Yerzhan Tulepov         Migrated from PaySysLib and renamed.
 * 22.10.2008	1.1.0	Yerzhan Tulepov	1.0.0.0->1.1.0.
 * 26.04.2008	1.0.0.0	Yerzhan Tulepov	Created.
 */

package kz.paysoft.common.decryptator;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.math.BigInteger;

import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.xml.sax.ErrorHandler;


public final class Decryptator {
    private static final int[] FLIP_BITS = { 7, 9, 10, 18, 26, 29, 35, 49, 67, 77, 92, 112, 144, 151, 177, 183, 190 };
    private static final byte[] DES_EDE = { -69, -70, -84, -102, -101, -102 }; // Inverted bytes of "DESede"
    private static final byte[] RSA = { -83, -84, -66 }; // Inverted bytes of "RSA"
    private static final byte[] DES_EDE_CBC_PKCS_5_PADDING = { -69, -70, -84, -102, -101, -102, -48, -68, -67, -68, -48, -81, -76, -68, -84, -54, -81, -98, -101, -101, -106, -111, -104 }; // Inverted bytes of "DESede/CBC/PKCS5Padding"
    private static final int RSA_KEY_LENGTH = 2048;

    public static final char[] getPassword(String key, String a, String b) throws DecryptatorException {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(new BigInteger(key).toByteArray());
            SecretKeyFactory skf = SecretKeyFactory.getInstance(toString(DES_EDE));
            SecretKey sk = skf.generateSecret(dks);
            AlgorithmParameters ap = AlgorithmParameters.getInstance(toString(DES_EDE));
            ap.init(new BigInteger(b).toByteArray());
            Cipher c = Cipher.getInstance(toString(DES_EDE_CBC_PKCS_5_PADDING));
            c.init(c.DECRYPT_MODE, sk, ap);
            return toChars(c.doFinal(new BigInteger(a).toByteArray()));
        } catch (Exception e) {
            throw new DecryptatorException(e);
        }
    }

    public static final Document getDocument(ErrorHandler errorHandler, String encryptedSchema, String configuration, byte[] algorithmParameters, byte[] transformedSecretKey, byte[] encryptedExponent, byte[] encryptedModulus) throws DecryptatorException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        decrypt(new BufferedInputStream(ClassLoader.getSystemResourceAsStream(encryptedSchema)), out, algorithmParameters, transformedSecretKey, encryptedExponent, encryptedModulus);
        ByteArrayInputStream clearSchema = new ByteArrayInputStream(out.toByteArray());
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        docFactory.setValidating(true);
        docFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        docFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", clearSchema);
        try {
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            builder.setErrorHandler(errorHandler);
            Document doc = builder.parse(new File(configuration));
            return doc;
        } catch (Exception e) {
            throw new DecryptatorException(e);
        }
    }

    private static final void decrypt(InputStream is, OutputStream os, byte[] algorithmParameters, byte[] transformedSecretKey, byte[] encryptedExponent, byte[] encryptedModulus) throws DecryptatorException {
        BigInteger originalSecretKey = new BigInteger(transformedSecretKey);
        for (int i = 0; i < FLIP_BITS.length; i++) {
            originalSecretKey = originalSecretKey.flipBit(FLIP_BITS[i]);
        }
        try {
            DESedeKeySpec dks = new DESedeKeySpec(originalSecretKey.toByteArray());
            SecretKeyFactory skf = SecretKeyFactory.getInstance(toString(DES_EDE));
            SecretKey sk = skf.generateSecret(dks);
            AlgorithmParameters ap = AlgorithmParameters.getInstance(toString(DES_EDE));
            ap.init(algorithmParameters);
            Cipher c = Cipher.getInstance(toString(DES_EDE_CBC_PKCS_5_PADDING));
            c.init(c.DECRYPT_MODE, sk, ap);
            BigInteger cm = new BigInteger(c.doFinal(encryptedModulus));
            BigInteger ce = new BigInteger(c.doFinal(encryptedExponent));
            RSAPublicKeySpec pks = new RSAPublicKeySpec(cm, ce);
            KeyFactory kf = KeyFactory.getInstance(toString(RSA));
            PublicKey pk = kf.generatePublic(pks);
            Cipher pc = Cipher.getInstance(toString(RSA));
            pc.init(pc.DECRYPT_MODE, pk);
            byte[] buf = new byte[RSA_KEY_LENGTH / 8];
            int i = is.read(buf);
            while (i != -1) {
                os.write(pc.doFinal(buf, 0, i));
                i = is.read(buf);
            }
        } catch (Exception e) {
            throw new DecryptatorException(e);
        }
    }

    private static final char[] toChars(byte[] password) {
        char[] buf = new char[password.length / 2];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char)((char)password[i * 2] << 8 | password[i * 2 + 1]);
            password[i * 2] = 0;
            password[i * 2 + 1] = 0;
        }
        return buf;
    }

    public static final String toString(byte[] bytes) {
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < buf.length; i++)
            buf[i] = (byte)~bytes[i];
        return new String(buf);
    }

    public static final void clearBuffer(char[] buf) {
        for (int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }
    }

    public static final void clearBuffer(byte[] buf) {
        for (int i = 0; i < buf.length; i++) {
            buf[i] = 0;
        }
    }

}
