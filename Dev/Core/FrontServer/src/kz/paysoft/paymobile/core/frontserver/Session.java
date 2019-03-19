/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.18   Yerzhan Tulepov         Created.
 */
//S-006

package kz.paysoft.paymobile.core.frontserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.sql.SQLException;

import javax.crypto.ShortBufferException;

import javax.net.ssl.SSLSocket;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class Session extends Thread {
    private static final byte PROTOCOL_VERSION = 1;
    private static final byte RC_SUCCESSFULLY = 0;
    private static final byte RC_SYSTEM_ERROR = (byte)229;
    private static final byte RC_PASSWORD_CHECK_ERROR = (byte)254;
    private static final byte RC_UNSUPPORTED_VERSION = (byte)255;
    private static final byte TYPE_INITIALIZATION = 0;
    private static final byte TYPE_CHECK_NEW_PASSWORD = 1;
    private static final byte TYPE_GENERATE_RANDOM_NUMBER = 2;
    private static final byte TYPE_TRANSFER = 3;
    private static final byte TYPE_STATEMENT = 4;
    private static final byte TYPE_BALANSE = 5;
    private static final byte TYPE_UPDATE_ACCOUNTS = 6;
    private static final byte TYPE_WAIT_FOR_PROCESSING = 7;

    private final Engine engine;
    private final SSLSocket socket;
    private final int connectionId, closeTime;
    private long phoneNumber, csi;
    private char[] password;
    volatile boolean srProcessed = false;

    Session(Engine engine, SSLSocket socket, int connectionId, int closeTime) { // Not to delay accepting new connections, the constructor must exit asap.
        this.engine = engine;
        this.socket = socket;
        this.connectionId = connectionId;
        this.closeTime = closeTime;
    }

    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            String cipherSuite = socket.getSession().getCipherSuite();
            FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Connected [" + connectionId + ":" + socket.getInetAddress() + ":" + socket.getSession().getProtocol() + ":" + cipherSuite + "].");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            byte version = (byte)in.readUnsignedByte();
            if (version != PROTOCOL_VERSION) {
                out.writeByte(RC_UNSUPPORTED_VERSION);
                throw new SessionException("S-001 [" + version + "].");
            } else {
                int type = in.readUnsignedByte();
                int processorId = in.readUnsignedShort();
                if (processorId != engine.processorId) {
                    out.writeByte(RC_SYSTEM_ERROR);
                    throw new SessionException("S-002 [" + processorId + "].");
                }
                phoneNumber = in.readLong();
                csi = in.readLong();
                switch (type) {
                case TYPE_INITIALIZATION:
                    FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Initialization started [" + connectionId + ":" + phoneNumber + "].");
                    engine.checkApplication(connectionId, phoneNumber, csi);
                    out.writeByte(RC_SUCCESSFULLY);
                    out.flush();
                    if (checkPassword(in, out)) {
                        byte[] masterKey = null, salt = null;
                        masterKey = engine.createBuffer(engine.saiForNewClients);
                        salt = engine.createBuffer(engine.saiForNewClients);
                        engine.applicationApplied(connectionId, phoneNumber, csi, cipherSuite, masterKey, salt, password);
                        Decryptator.clearBuffer(password);
                        password = null;
                        out.writeByte(RC_SUCCESSFULLY);
                        out.writeByte(engine.saiForNewClients);
                        out.writeByte(engine.keyId);
                        out.write(masterKey);
                        Decryptator.clearBuffer(masterKey);
                        masterKey = null;
                        out.write(salt);
                        Decryptator.clearBuffer(salt);
                        salt = null;
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Initialization succeeded [" + connectionId + ":" + phoneNumber + "].");
                    } else {
                        out.writeByte(RC_SYSTEM_ERROR);
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Initialization failed [" + connectionId + ":" + phoneNumber + "].");
                    }
                    break;

                case TYPE_GENERATE_RANDOM_NUMBER:
                    FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Operation started [" + connectionId + ":" + phoneNumber + "].");
                    int sai = in.readUnsignedByte();
                    byte[] randomNumber = engine.createBuffer(sai);
                    engine.secureRNG.nextBytes(randomNumber);
                    out.writeByte(RC_SUCCESSFULLY);
                    out.write(randomNumber);
                    out.flush();
                    type = in.readUnsignedByte();
                    String sourceAccount;
                    int signatureCounter;
                    byte[] signature = engine.createBuffer(sai);
                    int rootMasterKeyIndex;
                    long srId;
                    switch (type) {
                    case TYPE_TRANSFER:
                        sourceAccount = in.readUTF();
                        String destinationAccount = in.readUTF();
                        long amount = in.readLong();
                        String currency = in.readUTF();
                        String paymentTypeCode = in.readUTF();
                        String description = in.readUTF();
                        signatureCounter = in.readUnsignedShort();
                        in.readFully(signature);
                        rootMasterKeyIndex = in.readUnsignedByte();
                        String amountString = Long.toString(amount);
                        if (amountString.length() == 1) {
                            amountString = "00" + amountString;
                        } else if (amountString.length() == 2) {
                            amountString = "0" + amountString;
                        }
                        amountString = amountString.substring(0, amountString.length() - 2) + "." + amountString.substring(amountString.length() - 2);
                        srId =
engine.insertTransfer(connectionId, type, phoneNumber, csi, sai, randomNumber, sourceAccount, destinationAccount, amountString, currency, paymentTypeCode, description, signatureCounter, signature,
                      rootMasterKeyIndex, cipherSuite);
                        Decryptator.clearBuffer(randomNumber);
                        Decryptator.clearBuffer(signature);
                        randomNumber = null;
                        signature = null;
                        out.writeByte(RC_SUCCESSFULLY);
                        out.writeLong(srId);
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Transfer accepted [" + connectionId + ":" + phoneNumber + "].");
                        break;

                    case TYPE_STATEMENT:
                        sourceAccount = in.readUTF();
                        int statementType = in.readUnsignedByte();
                        signatureCounter = in.readUnsignedShort();
                        in.readFully(signature);
                        rootMasterKeyIndex = in.readUnsignedByte();
                        srId =
engine.insertStatement(connectionId, type, phoneNumber, csi, sai, randomNumber, sourceAccount, signatureCounter, signature, rootMasterKeyIndex, statementType, cipherSuite);
                        Decryptator.clearBuffer(randomNumber);
                        Decryptator.clearBuffer(signature);
                        randomNumber = null;
                        signature = null;
                        out.writeByte(RC_SUCCESSFULLY);
                        out.writeLong(srId);
                        out.flush();
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Statement prepared [" + connectionId + ":" + phoneNumber + "].");
                        sendResults(srId, in, out);
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Statement response sent [" + connectionId + ":" + phoneNumber + "].");
                        break;

                    case TYPE_BALANSE:
                        sourceAccount = in.readUTF();
                        signatureCounter = in.readUnsignedShort();
                        in.readFully(signature);
                        rootMasterKeyIndex = in.readUnsignedByte();
                        srId = engine.insertBalanse(connectionId, type, phoneNumber, csi, sai, randomNumber, sourceAccount, signatureCounter, signature, rootMasterKeyIndex, cipherSuite);
                        Decryptator.clearBuffer(randomNumber);
                        Decryptator.clearBuffer(signature);
                        randomNumber = null;
                        signature = null;
                        out.writeByte(RC_SUCCESSFULLY);
                        out.writeLong(srId);
                        out.flush();
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Balanse prepared [" + connectionId + ":" + phoneNumber + "].");
                        sendResults(srId, in, out);
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Balanse response sent [" + connectionId + ":" + phoneNumber + "].");
                        break;

                    case TYPE_UPDATE_ACCOUNTS:
                        signatureCounter = in.readUnsignedShort();
                        in.readFully(signature);
                        rootMasterKeyIndex = in.readUnsignedByte();
                        srId = engine.insertUpdateAccounts(connectionId, type, phoneNumber, csi, sai, randomNumber, signatureCounter, signature, rootMasterKeyIndex, cipherSuite);
                        Decryptator.clearBuffer(randomNumber);
                        Decryptator.clearBuffer(signature);
                        randomNumber = null;
                        signature = null;
                        out.writeByte(RC_SUCCESSFULLY);
                        out.writeLong(srId);
                        out.flush();
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Update accounts prepared [" + connectionId + ":" + phoneNumber + "].");
                        sendResults(srId, in, out);
                        FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Update accounts response sent [" + connectionId + ":" + phoneNumber + "].");
                        break;

                    default:
                        out.writeByte(RC_SYSTEM_ERROR);
                        throw new SessionException("S-003 [" + type + "].");
                    }
                    break;

                default:
                    out.writeByte(RC_SYSTEM_ERROR);
                    throw new SessionException("S-004 [" + type + "].");
                }
            }
        } catch (SessionException e) {
            Logger.logError("Session error:", e);
        } catch (EngineException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (NoSuchProviderException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (SQLException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (NoSuchAlgorithmException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (InvalidKeyException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (ShortBufferException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (InterruptedException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } catch (IOException e) {
            Logger.logError("Session error:", e);
        } catch (LoggerException e) {
            try {
                out.writeByte(RC_SYSTEM_ERROR);
            } catch (IOException f) {
                Logger.logError("Session error:", f);
            }
            Logger.logError("Session error:", e);
        } finally {
            try {
                sleep(closeTime * 1000);
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                socket.close();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_1, "Disconnected [" + connectionId + ":" + phoneNumber + "].");
            } catch (LoggerException e) {
                Logger.logError("Session error:", e);
            } catch (IOException e) {
                Logger.logError("Session error:", e);
            } catch (InterruptedException e) {
                Logger.logError("Session error:", e);
            }
        }
    }

    private final boolean checkPassword(DataInputStream in, DataOutputStream out) throws IOException, SessionException {
        for (int i = 0; i < engine.passwordMaxTries; i++) {
            if (i != 0) {
                out.writeByte(RC_PASSWORD_CHECK_ERROR);
            }
            byte type = (byte)in.readUnsignedByte();
            if (type != TYPE_CHECK_NEW_PASSWORD) {
                out.writeByte(RC_SYSTEM_ERROR);
                throw new SessionException("S-005 [" + type + "].");
            }
            byte passwordLength = in.readByte();
            password = new char[passwordLength];
            for (int l = 0; l < passwordLength; l++) {
                password[l] = in.readChar();
            }
            if (engine.isPasswordValid(password)) {
                return true;
            }
        }
        return false;
    }

    private final void sendResults(long id, DataInputStream in, DataOutputStream out) throws InterruptedException, IOException, SessionException, SQLException {
        byte type = (byte)in.readUnsignedByte();
        if (type != TYPE_WAIT_FOR_PROCESSING) {
            out.writeByte(RC_SYSTEM_ERROR);
            throw new SessionException("S-006 [" + type + "].");
        }
        engine.sessions.put(new Long(id), this);
        for (int i = 0; i < engine.timeOut && !srProcessed; i++) {
            sleep(1000);
        }
        if (srProcessed) {
            byte[] data = engine.getData(id);
            if (data.length != 0) {
                out.writeByte(RC_SUCCESSFULLY);
                out.write(data);
            } else {
                out.writeByte(RC_SYSTEM_ERROR);
            }
        } else {
            out.writeByte(RC_SYSTEM_ERROR);
        }
    }
}
