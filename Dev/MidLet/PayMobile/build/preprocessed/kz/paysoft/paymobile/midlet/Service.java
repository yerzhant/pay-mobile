/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: V.
 * Last error index: 022.
 *
 * 2010.03.21   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

/**
 *
 * @author Yerzhan Tulepov.
 */
abstract class Service extends Thread {

//#ifdef DefaultConfiguration
    private static final String TRANSPORT_PROTOCOL_NAME = "SSL";
    private static final String TRANSPORT_PROTOCOL_VERSIONS[] = {"3.0"};
//#else
//#     private static final String TRANSPORT_PROTOCOL_NAME = "TLS";
//#     private static final String TRANSPORT_PROTOCOL_VERSIONS[] = {"3.1", "3.2", "3.3"};
//#endif
    private static final String TRANSPORT_PROTOCOL_SUITES[] = {
        "RSA_WITH_RC4_128_SHA",
        "RSA_WITH_3DES_EDE_CBC_SHA",
        "RSA_WITH_AES_128_CBC_SHA",
        "RSA_WITH_AES_256_CBC_SHA",
        "RSA_WITH_AES_128_CBC_SHA256",
        "RSA_WITH_AES_256_CBC_SHA256",
        "DH_DSS_WITH_3DES_EDE_CBC_SHA",
        "DH_RSA_WITH_3DES_EDE_CBC_SHA",
        "DHE_DSS_WITH_3DES_EDE_CBC_SHA",
        "DHE_RSA_WITH_3DES_EDE_CBC_SHA",
        "DH_DSS_WITH_AES_128_CBC_SHA",
        "DH_RSA_WITH_AES_128_CBC_SHA",
        "DHE_DSS_WITH_AES_128_CBC_SHA",
        "DHE_RSA_WITH_AES_128_CBC_SHA",
        "DH_DSS_WITH_AES_256_CBC_SHA",
        "DH_RSA_WITH_AES_256_CBC_SHA",
        "DHE_DSS_WITH_AES_256_CBC_SHA",
        "DHE_RSA_WITH_AES_256_CBC_SHA",
        "DH_DSS_WITH_AES_128_CBC_SHA256",
        "DH_RSA_WITH_AES_128_CBC_SHA256",
        "DHE_DSS_WITH_AES_128_CBC_SHA256",
        "DHE_RSA_WITH_AES_128_CBC_SHA256",
        "DH_DSS_WITH_AES_256_CBC_SHA256",
        "DH_RSA_WITH_AES_256_CBC_SHA256",
        "DHE_DSS_WITH_AES_256_CBC_SHA256",
        "DHE_RSA_WITH_AES_256_CBC_SHA256"
    };
    private static final String DIVERSIFICATION_LABEL = "SKHMS01";
    private static final int MAX_SIGNATURE_COUNTER_VALUE = 65535;
    private static final short PBE_COUNTER = 1024;
    private static final short SAI_HMAC_SHA_256_L = 0x100;
    private static final byte SAI_HMAC_SHA_256_LENGTH = 32;
    private static final byte PROTOCOL_VERSION = 1;
    private static final byte PARAM_FILE_VERSION = 1;
    private static final byte MISC_STORE_SIGNATURE_COUNTER_ID = 0;
    private static final byte MISC_STORE_STATIC_ID = 1;
    private static final byte MISC_STORE_NUMBER_OF_RECORDS = 2;
    private static final byte RC_SUCCESSFULLY = 0;
    private static final byte RC_SYSTEM_ERROR = (byte) 229;
    private static final byte RC_PASSWORD_VALIDATION_ERROR = (byte) 254;
    private static final byte RC_UNSUPPORTED_VERSION = (byte) 255;
    private static final byte TYPE_REQUEST_RANDOM_NUMBER = 2;
    private static final byte TYPE_WAIT_FOR_PROCESSING = 7;
    private static MessageConnection smsConnection;
    private static SecureConnection connection;
    private static String certificateSubject;
    private static String certificateIssuer;
    private static String certificateType;
    private static String certificateVersion;
    private static String certificateSigAlgName;
    private static String certificateSerialNumber;
    private static String serverAddress;
    private static String smppServerPhoneNumber;
    private static long certificateNotBefore;
    private static long certificateNotAfter;
    private static short serverPort, processorId;
    private static byte data[], encryptedMK[], salt[], randomNumber[];
    private static byte signatureAlgorightIdentifier, rootMasterKeyIndex;
    static final String SYSTEM_ERROR = "Ошибка.";
    static final String UNKNOWN_ERROR = "Ошибка не определена";
    static final byte SAI_HMAC_SHA_256 = 1;
    static volatile boolean connected = false;
    static DataInputStream inStream;
    static DataOutputStream outStream;
    static String sourceAccount, destinationAccount, currency, paymentTypeCode, description;
    static long phoneNumber, csiNumber, amount;
    static char password[];

    static void connect() throws ConnectionNotFoundException, SecurityException, IOException, Exception {
        if (connected) {
            throw new IllegalStateException("V-001\n" + SYSTEM_ERROR);
        }
        smsConnection = (MessageConnection) Connector.open("sms://" + smppServerPhoneNumber, Connector.WRITE);
        connection = (SecureConnection) Connector.open("ssl://" + serverAddress + ":" + serverPort);
        checkSecurity();
        outStream = connection.openDataOutputStream();
        inStream = connection.openDataInputStream();
        connected = true;
    }

    private static void initStaticsFromMiscStore() throws RecordStoreException, IOException {
        RecordEnumeration re = PayMobile.miscellaneousStore.enumerateRecords(null, null, false);
        if (re.numRecords() != 0) {
            if (re.numRecords() != MISC_STORE_NUMBER_OF_RECORDS) {
                re.destroy();
                re = null;
                throw new RecordStoreException("V-016\n" + SYSTEM_ERROR);
            }
            while (re.hasNextElement()) {
                data = re.nextRecord();
                if (data[0] == MISC_STORE_STATIC_ID) {
                    re.destroy();
                    re = null;
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(bais);
                    dis.readByte(); // MISC_STORE_STATIC_ID.
                    signatureAlgorightIdentifier = dis.readByte();
                    rootMasterKeyIndex = dis.readByte();
                    encryptedMK = new byte[SAI_HMAC_SHA_256_LENGTH];
                    dis.readFully(encryptedMK);
                    salt = new byte[SAI_HMAC_SHA_256_LENGTH];
                    dis.readFully(salt);
                    phoneNumber = dis.readLong();
                    csiNumber = dis.readLong();
                    dis.close();
                    dis = null;
                    bais.close();
                    bais = null;
                    randomNumber = new byte[SAI_HMAC_SHA_256_LENGTH];
                    return;
                }
            }
            throw new RecordStoreException("V-023\n" + SYSTEM_ERROR);
        } else {
            re.destroy();
            re = null;
        }
    }

    static void sendRequest(byte type, boolean sendSAI) throws IOException, RecordStoreException {
        initStaticsFromMiscStore();
        outStream.writeByte(PROTOCOL_VERSION);
        outStream.writeByte(type);
        outStream.writeShort(processorId);
        outStream.writeLong(phoneNumber);
        outStream.writeLong(csiNumber);
        if (sendSAI) {
            outStream.writeByte(signatureAlgorightIdentifier);
        }
    }

    static void readIdAndSendSMS() throws IOException {
        TextMessage text = (TextMessage) smsConnection.newMessage(MessageConnection.TEXT_MESSAGE);
        text.setPayloadText("" + PROTOCOL_VERSION + inStream.readLong());
        smsConnection.send(text);
        text = null;
        smsConnection.close();
        smsConnection = null;
    }

    static void processRC() throws IOException, PasswordValidationException, Exception {
        byte rc = inStream.readByte();
        switch (rc) {
            case RC_SUCCESSFULLY:
                break;

            case RC_PASSWORD_VALIDATION_ERROR:
                throw new PasswordValidationException();

            case RC_SYSTEM_ERROR:
                throw new Exception("V-020\n" + SYSTEM_ERROR);

            case RC_UNSUPPORTED_VERSION:
                throw new Exception("V-021\n" + SYSTEM_ERROR);

            default:
                throw new Exception("V-022\n" + UNKNOWN_ERROR + " (" + rc + ").");
        }
    }

    static void connectAndGetRandomNumber() throws SecurityException, ConnectionNotFoundException, IOException, PasswordValidationException, Exception {
        connect();
        sendRequest(TYPE_REQUEST_RANDOM_NUMBER, true);
        outStream.flush();
        processRC();
        inStream.readFully(randomNumber);
    }

    void waitForProcessing() throws SecurityException, ConnectionNotFoundException, IOException, PasswordValidationException, Exception {
        connectAndGetRandomNumber();
        signSendAndFlush();
        processRC();
        readIdAndSendSMS();
        outStream.writeByte(TYPE_WAIT_FOR_PROCESSING);
        processRC();
    }

    void signSendAndFlush() throws RecordStoreException, Exception {
        RecordEnumeration re = PayMobile.miscellaneousStore.enumerateRecords(null, null, false);
        while (re.hasNextElement()) {
            int recordId = re.nextRecordId();
            data = PayMobile.miscellaneousStore.getRecord(recordId);
            if (data[0] == MISC_STORE_SIGNATURE_COUNTER_ID) {
                re.destroy();
                re = null;
                int sigCounter = data[1] << 8 | data[2];
                data = null;
                sigCounter++;
                if (sigCounter <= MAX_SIGNATURE_COUNTER_VALUE) {
                    getMK();
//#mdebug
                    System.out.print("MK: ");
                    printHex(data);
//#enddebug
                    deriveSigningKey(sigCounter);
//#mdebug
                    System.out.print("SK: ");
                    printHex(data);
//#enddebug
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream toSign = new DataOutputStream(baos);
                    toSign.write(randomNumber);
                    toSign.writeShort(sigCounter);
                    sendBody(toSign);
                    toSign.writeLong(phoneNumber);
                    toSign.writeLong(csiNumber);
                    toSign.writeByte(rootMasterKeyIndex);
                    toSign.writeByte(signatureAlgorightIdentifier);
                    toSign.writeShort(processorId);
                    toSign.flush();
                    byte[] signingData = baos.toByteArray();
                    toSign.close();
                    baos.close();
//#mdebug
                    System.out.print("Data: ");
                    printHex(signingData);
//#enddebug
                    data = HMAC.hmac(signatureAlgorightIdentifier, data, signingData, true, true);
                    PayMobile.clearBuffer(signingData);
//#mdebug
                    System.out.print("SG: ");
                    printHex(data);
//#enddebug
                    byte sigCounterRecord[] = new byte[3];
                    sigCounterRecord[0] = MISC_STORE_SIGNATURE_COUNTER_ID;
                    sigCounterRecord[1] = (byte) (sigCounter >>> 8);
                    sigCounterRecord[2] = (byte) sigCounter;
                    PayMobile.miscellaneousStore.setRecord(recordId, sigCounterRecord, 0, sigCounterRecord.length);
                    sigCounterRecord = null;
                    outStream.writeShort(sigCounter);
                    outStream.write(data);
                    PayMobile.clearBuffer(data);
                    data = null;
                    outStream.writeByte(rootMasterKeyIndex);
                    outStream.flush();
                    return;
                } else {
                    throw new Exception("V-018\n" + SYSTEM_ERROR);
                }
            }
        }
        throw new RecordStoreException("V-017\n" + SYSTEM_ERROR);
    }

    private static void getMK() throws PBEException, HMACException, SHA256Exception, InterruptedException {
        data = PBE.pbe(signatureAlgorightIdentifier, password, salt, PBE_COUNTER, true);
        password = null;
        for (int i = 0; i < data.length; i++) {
            data[i] ^= encryptedMK[i];
        }
    }

    private static void deriveSigningKey(int sigCounter) throws IOException, HMACException, SHA256Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(DIVERSIFICATION_LABEL.getBytes());
        dos.write(randomNumber);
        dos.writeShort(sigCounter);
        dos.writeLong(phoneNumber);
        dos.writeLong(csiNumber);
        dos.writeByte(rootMasterKeyIndex);
        dos.writeByte(signatureAlgorightIdentifier);
        dos.writeShort(processorId);
        dos.writeShort(SAI_HMAC_SHA_256_L);
        dos.flush();
        byte[] buf = baos.toByteArray();
//#mdebug
        System.out.print("SK Data: ");
        printHex(buf);
//#enddebug
        data = HMAC.hmac(signatureAlgorightIdentifier, data, buf, true, true);
        PayMobile.clearBuffer(buf);
        buf = null;
        dos.close();
        dos = null;
        baos.close();
        baos = null;
    }

    static void init() throws IOException, RecordStoreException {
        InputStream in = PayMobile.payMobile.getClass().getResourceAsStream("p.dat");
        DataInputStream dis = new DataInputStream(in);
        if (dis.readByte() != PARAM_FILE_VERSION) {
            dis.close();
            dis = null;
            //in.close(); Nokia's Series 40 5th Edition raises IOException.
            in = null;
            throw new IOException("V-003\n" + SYSTEM_ERROR);
        }
        smppServerPhoneNumber = dis.readUTF();
        processorId = dis.readShort();
        serverAddress = dis.readUTF();
        serverPort = dis.readShort();
        certificateIssuer = dis.readUTF();
        certificateSubject = dis.readUTF();
        certificateType = dis.readUTF();
        certificateVersion = dis.readUTF();
        certificateSigAlgName = dis.readUTF();
        certificateSerialNumber = dis.readUTF();
        certificateNotBefore = dis.readLong();
        certificateNotAfter = dis.readLong();
        dis.close();
        dis = null;
        //in.close(); Nokia's Series 40 5th Edition raises IOException.
        in = null;
    }

    private static void checkSecurity() throws Exception {
        SecurityInfo info = connection.getSecurityInfo();
        if (!info.getProtocolName().equals(TRANSPORT_PROTOCOL_NAME)) {
            throw new Exception("V-004\n" + info.getProtocolName() + "\n" + SYSTEM_ERROR);
        }
        boolean errorFlag = true;
        for (int i = 0; i < TRANSPORT_PROTOCOL_VERSIONS.length; i++) {
            if (info.getProtocolVersion().equals(TRANSPORT_PROTOCOL_VERSIONS[i])) {
                errorFlag = false;
                break;
            }
        }
        if (errorFlag) {
            throw new Exception("V-005\n" + info.getProtocolVersion() + "\n" + SYSTEM_ERROR);
        }
        String cipherSuite = info.getCipherSuite();
        for (int i = 0; i < TRANSPORT_PROTOCOL_SUITES.length; i++) {
            if (cipherSuite.substring(4, cipherSuite.length()).equals(TRANSPORT_PROTOCOL_SUITES[i])) {
                errorFlag = false;
                break;
            }
        }
        if (errorFlag) {
            throw new Exception("V-006\n" + cipherSuite + "\n" + SYSTEM_ERROR);
        }
        Certificate cert = info.getServerCertificate();
//        if (!cert.getIssuer().equals(certificateIssuer)) {
//            throw new Exception("V-007\n" + cert.getIssuer() + "\n" + SYSTEM_ERROR);
//        }
//        if (cert.getNotAfter() != certificateNotAfter) {
//            throw new Exception("V-008\n" + cert.getNotAfter() + "\n" + SYSTEM_ERROR);
//        }
//        if (cert.getNotBefore() != certificateNotBefore) {
//            throw new Exception("V-009\n" + cert.getNotBefore() + "\n" + SYSTEM_ERROR);
//        }
//        if (!cert.getSerialNumber().equals(certificateSerialNumber)) {
//            throw new Exception("V-010\n" + cert.getSerialNumber() + "\n" + SYSTEM_ERROR);
//        }
//        if (!cert.getSigAlgName().equalsIgnoreCase(certificateSigAlgName)) {
//            throw new Exception("V-011\n" + cert.getSigAlgName() + "\n" + SYSTEM_ERROR);
//        }
        if (!cert.getSubject().equals(certificateSubject)) {
            throw new Exception("V-012\n" + cert.getSubject() + "\n" + SYSTEM_ERROR);
        }
//        if (!cert.getType().equals(certificateType)) {
//            throw new Exception("V-013\n" + cert.getType() + "\n" + SYSTEM_ERROR);
//        }
//        if (!cert.getVersion().equals(certificateVersion)) {
//            throw new Exception("V-014\n" + cert.getVersion() + "\n" + SYSTEM_ERROR);
//        }
//        if (new Date().getTime() < certificateNotBefore || new Date().getTime() > certificateNotAfter) {
//            throw new Exception("V-015\n" + SYSTEM_ERROR);
//        }
    }

    static void cleanUp() {
        PayMobile.clearBuffer(data);
        data = null;
        PayMobile.clearBuffer(password);
        password = null;
        PayMobile.clearBuffer(encryptedMK);
        encryptedMK = null;
        PayMobile.clearBuffer(salt);
        salt = null;
        PayMobile.clearBuffer(randomNumber);
        randomNumber = null;
    }

    static void cleanUpAndDisconnect() {
        connected = false;
        cleanUp();
        try {
            if (inStream != null) {
                //inStream.close(); // SonyEricsson's emulator hangs on cancelling  request.
                inStream = null;
            }
            if (outStream != null) {
                outStream.close();
                outStream = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
            if (smsConnection != null) {
                smsConnection.close();
                smsConnection = null;
            }
        } catch (IOException ex) {
            PayMobile.payMobile.appendErrorString(ex.getMessage());
        }
    }

    static void storeInitValues() throws IOException, RecordStoreException, Exception {
        signatureAlgorightIdentifier = inStream.readByte();
        switch (signatureAlgorightIdentifier) {
            case SAI_HMAC_SHA_256:
                data = new byte[3];
                data[0] = MISC_STORE_SIGNATURE_COUNTER_ID;
                PayMobile.miscellaneousStore.addRecord(data, 0, data.length);
                data = null;
                data = new byte[SAI_HMAC_SHA_256_LENGTH];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeByte(MISC_STORE_STATIC_ID);
                dos.writeByte(signatureAlgorightIdentifier);
                dos.writeByte(inStream.readByte());                     //RMKI.
                inStream.readFully(data, 0, SAI_HMAC_SHA_256_LENGTH);
                dos.write(data);                                        // MK.
                inStream.readFully(data, 0, SAI_HMAC_SHA_256_LENGTH);
                dos.write(data);                                        // Salt.
                dos.writeLong(phoneNumber);
                dos.writeLong(csiNumber);
                dos.flush();
                PayMobile.clearBuffer(data);
                data = baos.toByteArray();
                PayMobile.miscellaneousStore.addRecord(data, 0, data.length);
                data = null;
                dos.close();
                dos = null;
                baos.close();
                baos = null;
                break;

            default:
                throw new Exception("V-002\n" + SYSTEM_ERROR);
        }
    }

    abstract void sendBody(DataOutputStream toSign) throws IOException;
//#mdebug

//    static void debugInit() throws IOException, RecordStoreException {
//        data = new byte[3];
//        data[0] = MISC_STORE_SIGNATURE_COUNTER_ID;
//        PayMobile.miscellaneousStore.addRecord(data, 0, data.length);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        DataOutputStream dos = new DataOutputStream(baos);
//        dos.writeByte(MISC_STORE_STATIC_ID);
//        dos.writeByte(1);
//        dos.writeByte(0);
//        byte anMK[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
//        dos.write(anMK);
//        byte aSalt[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
//        dos.write(aSalt);
//        dos.writeLong(123456789);
//        dos.writeLong(777);
//        dos.flush();
//        data = baos.toByteArray();
//        PayMobile.miscellaneousStore.addRecord(data, 0, data.length);
//        data = null;
//        dos.close();
//        baos.close();
//    }
    static final void printHex(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            } else if (hex.length() > 2) {
                hex = hex.substring(hex.length() - 2);
            }
            System.out.print(hex);
        }
        System.out.println();
    }
//#enddebug
}
