/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.18   Yerzhan Tulepov         Created.
 */
//E-005: 001 is not used.

package kz.paysoft.paymobile.core.frontserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


final class Engine extends Thread {
    private static final boolean DEVELOPMENT = true;
    private static final String KEY_ID_PREFIX = "key_";
    private static final short SAI_1_L = 0x100;
    private static final byte[] JAVAX_NET_DEBUG = { -107, -98, -119, -98, -121, -47, -111, -102, -117, -47, -101, -102, -99, -118, -104 }; // javax.net.debug
    private static final byte[] JCECSP_DEBUG = { -75, -68, -70, -68, -84, -81, -96, -69, -70, -67, -86, -72 }; // JCECSP_DEBUG
    private static final byte[] NFJAVA_DEBUG = { -79, -71, -75, -66, -87, -66, -96, -69, -70, -67, -86, -72 }; // NFJAVA_DEBUG
    private static final byte[] COM_NCIPHER_PROVIDER_DISABLE =
    { -100, -112, -110, -47, -111, -100, -106, -113, -105, -102, -115, -47, -113, -115, -112, -119, -106, -101, -102, -115, -47, -101, -106, -116, -98, -99, -109,
      -102 }; // com.ncipher.provider.disable
    private static final byte[] SAI_1_MKHMS01 = { -78, -76, -73, -78, -84, -49, -50 }; // MKHMS01.
    private static final byte[] SAI_1_HMAC_SHA_256 = { -73, -110, -98, -100, -84, -73, -66, -51, -54, -55 }; // HmacSHA256.
    private static final byte[] _DEVELOPMENT_ =
    { -43, -43, -43, -43, -43, -43, -43, -33, -69, -33, -70, -33, -87, -33, -70, -33, -77, -33, -80, -33, -81, -33, -78, -33, -70, -33, -79, -33, -85, -33, -33, -33, -70, -33, -79, -33, -66, -33,
      -67, -33, -77, -33, -70, -33, -69, -33, -43, -43, -43, -43, -43, -43, -43 };
    private static final byte SAI_1 = 1;
    private static final byte SAI_1_LENGTH = 32;
    private final SecretKey rootMasterKey;
    private final SSLServerSocket serverSocket;
    private final Connection connection;
    private final String interfaceCode, provider;
    private final int passwordFactor;
    private final byte passwordMinLength, passwordMaxLength, passwordMinNumbers, passwordMinLetters, passwordMinSpecials;
    final ConcurrentHashMap<Long, Session> sessions = new ConcurrentHashMap<Long, Session>();
    final SecureRandom secureRNG;
    final int timeOut, processorId, keyId, saiForNewClients, closeTime;
    final byte passwordMaxTries;

    Engine(Node server, Node crypto, NamedNodeMap passwordParams) throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, IOException, CertificateException,
                                                                         UnrecoverableKeyException, DecryptatorException, KeyManagementException, NoSuchProviderException {
        if (DEVELOPMENT) {
            System.err.println(Decryptator.toString(_DEVELOPMENT_));
        }
        connection = FrontServer.connection;
        interfaceCode = server.getAttributes().getNamedItem("interface").getNodeValue();
        timeOut = Integer.parseInt(server.getAttributes().getNamedItem("timeOut").getNodeValue());
        processorId = Integer.parseInt(server.getAttributes().getNamedItem("processorId").getNodeValue());
        closeTime = Integer.parseInt(server.getAttributes().getNamedItem("closeTime").getNodeValue());
        saiForNewClients = Integer.parseInt(crypto.getAttributes().getNamedItem("sai").getNodeValue());
        keyId = Integer.parseInt(crypto.getAttributes().getNamedItem("keyId").getNodeValue());
        passwordFactor = Integer.parseInt(passwordParams.getNamedItem("factor").getNodeValue());
        passwordMaxTries = Byte.parseByte(passwordParams.getNamedItem("maxTries").getNodeValue());
        passwordMinLength = Byte.parseByte(passwordParams.getNamedItem("minLength").getNodeValue());
        passwordMaxLength = Byte.parseByte(passwordParams.getNamedItem("maxLength").getNodeValue());
        passwordMinNumbers = Byte.parseByte(passwordParams.getNamedItem("minNumbers").getNodeValue());
        passwordMinLetters = Byte.parseByte(passwordParams.getNamedItem("minLetters").getNodeValue());
        passwordMinSpecials = Byte.parseByte(passwordParams.getNamedItem("minSpecials").getNodeValue());
        NodeList cipherSuitesList = crypto.getChildNodes();
        ArrayList<String> cipherSuites = new ArrayList<String>();
        for (int i = 0; i < cipherSuitesList.getLength(); i++) {
            Node child = cipherSuitesList.item(i);
            if (child.getNodeName().equals("cipherSuite")) {
                cipherSuites.add(child.getAttributes().getNamedItem("name").getNodeValue());
            }
        }
        NamedNodeMap cryptoParams = crypto.getAttributes();
        provider = cryptoParams.getNamedItem("provider").getNodeValue();
        KeyStore ks;
        if (DEVELOPMENT) {
            secureRNG = new SecureRandom();
            ks = KeyStore.getInstance(cryptoParams.getNamedItem("storeType").getNodeValue());
        } else {
            if (System.getProperty(Decryptator.toString(JAVAX_NET_DEBUG)) != null) {
                throw new RuntimeException();
            } else if (System.getProperty(Decryptator.toString(JCECSP_DEBUG)) != null) {
                throw new RuntimeException();
            } else if (System.getProperty(Decryptator.toString(NFJAVA_DEBUG)) != null) {
                throw new RuntimeException();
            } else if (System.getProperty(Decryptator.toString(COM_NCIPHER_PROVIDER_DISABLE)) != null) {
                throw new RuntimeException();
            }
            secureRNG = SecureRandom.getInstance(cryptoParams.getNamedItem("srAlgorithm").getNodeValue(), provider);
            ks = KeyStore.getInstance(cryptoParams.getNamedItem("storeType").getNodeValue(), provider);
        }
        char[] password = Decryptator.getPassword(cryptoParams.getNamedItem("x").getNodeValue(), cryptoParams.getNamedItem("a").getNodeValue(), cryptoParams.getNamedItem("b").getNodeValue());
        ks.load(new FileInputStream(cryptoParams.getNamedItem("store").getNodeValue()), password);
        rootMasterKey = (SecretKey)ks.getKey(KEY_ID_PREFIX + keyId, password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);
        Decryptator.clearBuffer(password);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, secureRNG);
        SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
        serverSocket = (SSLServerSocket)ssf.createServerSocket(Integer.parseInt(server.getAttributes().getNamedItem("port").getNodeValue()));
        if (cipherSuites.size() != 0) {
            serverSocket.setEnabledCipherSuites(cipherSuites.toArray(new String[0]));
        }
    }

    final void cleanUp() throws IOException, InterruptedException {
        System.out.print("Exiting");
        int i = 0;
        while (Thread.activeCount() > 3) {
            sleep(200);
            if (i % 2 == 0) {
                System.out.print(".");
            }
            i++;
        }
        System.out.println();
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    final void checkApplication(int connectionId, long phoneNumber, long csi) throws SQLException, LoggerException {
        Statement stmt = null;
        ResultSet rset = null;
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 1 [" + connectionId + ":" + phoneNumber + "].");
        synchronized (connection) {
            try {
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 2 [" + connectionId + ":" + phoneNumber + "].");
                stmt = connection.createStatement();
                rset = stmt.executeQuery("SELECT id$ FROM pay_mobile.applications WHERE id$ = " + csi + " AND phone_number = " + phoneNumber + " AND status = 'W' FOR UPDATE");
                if (rset.next()) {
                    stmt.executeUpdate("UPDATE pay_mobile.applications SET status = 'I' WHERE id$ = " + csi + " AND phone_number = " + phoneNumber + " AND status = 'W'");
                    connection.commit();
                } else {
                    stmt.executeUpdate("INSERT INTO pay_mobile.system_log(id$, code, description, user$) VALUES (pay_mobile.system_log_seq.NEXTVAL, 'E-001', 'Ошибка инициализации [" + phoneNumber +
                                       ":" + csi + "].', USER)");
                    connection.commit();
                    //throw new EngineException("E-001.");
                }
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 3 [" + connectionId + ":" + phoneNumber + "].");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }

    final void applicationApplied(int connectionId, long phoneNumber, long csi, String cipherSuite, byte[] masterKey, byte[] salt, char[] password) throws EngineException, NoSuchAlgorithmException,
                                                                                                                                                           InvalidKeyException, IOException,
                                                                                                                                                           ShortBufferException, SQLException,
                                                                                                                                                           NoSuchProviderException, LoggerException {
        Statement stmt = null;
        ResultSet rset = null;
        byte[] appliedAt;
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 4 [" + connectionId + ":" + phoneNumber + "].");
        synchronized (connection) {
            try {
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 5 [" + connectionId + ":" + phoneNumber + "].");
                stmt = connection.createStatement();
                rset =
stmt.executeQuery("SELECT TO_CHAR(applied_at, 'SSMIHH24DDMMYY') FROM pay_mobile.applications WHERE id$ = " + csi + " AND phone_number = " + phoneNumber + " AND status = 'I' FOR UPDATE");
                if (rset.next()) {
                    appliedAt = rset.getString(1).getBytes();
                    stmt.executeUpdate("UPDATE pay_mobile.applications SET status = 'P', cipher_suite = '" + cipherSuite + "' WHERE id$ = " + csi + " AND phone_number = " + phoneNumber +
                                       " AND status = 'I'");
                    connection.commit();
                } else {
                    stmt.executeUpdate("INSERT INTO pay_mobile.system_log(id$, code, description, user$) VALUES (pay_mobile.system_log_seq.NEXTVAL, 'E-002', 'Ошибка инициализации [" + phoneNumber +
                                       ":" + csi + "].', USER)");
                    connection.commit();
                    throw new EngineException("E-002.");
                }
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 6 [" + connectionId + ":" + phoneNumber + "].");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
        generateCryptoValues(connectionId, masterKey, salt, password, phoneNumber, csi, appliedAt);
    }

    final void processSRControllerNotifications() throws IOException {
        while (true) {
            long id = FrontServer.srControllerIn.readLong();
            Session session = sessions.remove(new Long(id));
            if (session != null) {
                session.srProcessed = true;
            } else {
                Logger.logError("E-005: Id not found [" + id + "].");
            }
        }
    }

    final long insertTransfer(int connectionId, int type, long phoneNumber, long csi, int sai, byte[] randomNumber, String source, String destination, String amount, String currency,
                              String paymentTypeCode, String description, int signatureCounter, byte[] signature, int rmki, String cipherSuite) throws SQLException, LoggerException {
        OraclePreparedStatement stmt = null;
        ResultSet rset = null;
        long id;
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Transfer stage 1 [" + connectionId + ":" + phoneNumber + "].");
        synchronized (connection) {
            try {
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Transfer stage 2 [" + connectionId + ":" + phoneNumber + "].");
                stmt =
(OraclePreparedStatement)connection.prepareStatement("INSERT INTO pay_mobile.srs(id$, phone_number, csi, cipher_suite, init_interface, type_code, rmki, sai, sc, src_name, dst_name, amount, currency, ptc, description, rn, sn) VALUES (pay_mobile.srs_seq.NEXTVAL, " +
                                                     phoneNumber + ", " + csi + ", '" + cipherSuite + "', '" + interfaceCode + "', '" + type + "', " + rmki + ", " + sai + ", " + signatureCounter +
                                                     ", '" + source + "', '" + destination + "', " + amount + ", '" + currency + "', '" + paymentTypeCode + "', '" + description +
                                                     "', EMPTY_BLOB(), EMPTY_BLOB()) RETURNING id$, rn, sn INTO ?, ?, ?");
                stmt.registerReturnParameter(1, OracleTypes.NUMBER);
                stmt.registerReturnParameter(2, OracleTypes.BLOB);
                stmt.registerReturnParameter(3, OracleTypes.BLOB);
                stmt.executeUpdate();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Transfer stage 3 [" + connectionId + ":" + phoneNumber + "].");
                rset = stmt.getReturnResultSet();
                rset.next();
                id = rset.getLong(1);
                rset.getBlob(2).setBytes(1, randomNumber);
                rset.getBlob(3).setBytes(1, signature);
                connection.commit();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Transfer stage 4 [" + connectionId + ":" + phoneNumber + "].");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            return id;
        }
    }

    final long insertStatement(int connectionId, int type, long phoneNumber, long csi, int sai, byte[] randomNumber, String source, int signatureCounter, byte[] signature, int rmki, int stType,
                               String cipherSuite) throws SQLException, LoggerException {
        OraclePreparedStatement stmt = null;
        ResultSet rset = null;
        long id;
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Statement stage 1 [" + connectionId + ":" + phoneNumber + "].");
        synchronized (connection) {
            try {
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Statement stage 2 [" + connectionId + ":" + phoneNumber + "].");
                stmt =
(OraclePreparedStatement)connection.prepareStatement("INSERT INTO pay_mobile.srs(id$, phone_number, csi, cipher_suite, init_interface, type_code, rmki, sai, sc, src_name, st_type, rn, sn) VALUES (pay_mobile.srs_seq.NEXTVAL, " +
                                                     phoneNumber + ", " + csi + ", '" + cipherSuite + "', '" + interfaceCode + "', '" + type + "', " + rmki + ", " + sai + ", " + signatureCounter +
                                                     ", '" + source + "', " + stType + ", EMPTY_BLOB(), EMPTY_BLOB()) RETURNING id$, rn, sn INTO ?, ?, ?");
                stmt.registerReturnParameter(1, OracleTypes.NUMBER);
                stmt.registerReturnParameter(2, OracleTypes.BLOB);
                stmt.registerReturnParameter(3, OracleTypes.BLOB);
                stmt.executeUpdate();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Statement stage 3 [" + connectionId + ":" + phoneNumber + "].");
                rset = stmt.getReturnResultSet();
                rset.next();
                id = rset.getLong(1);
                rset.getBlob(2).setBytes(1, randomNumber);
                rset.getBlob(3).setBytes(1, signature);
                connection.commit();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Statement stage 4 [" + connectionId + ":" + phoneNumber + "].");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            return id;
        }
    }

    final long insertBalanse(int connectionId, int type, long phoneNumber, long csi, int sai, byte[] randomNumber, String source, int signatureCounter, byte[] signature, int rmki,
                             String cipherSuite) throws SQLException, LoggerException {
        OraclePreparedStatement stmt = null;
        ResultSet rset = null;
        long id;
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Balanse stage 1 [" + connectionId + ":" + phoneNumber + "].");
        synchronized (connection) {
            try {
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Balanse stage 2 [" + connectionId + ":" + phoneNumber + "].");
                stmt =
(OraclePreparedStatement)connection.prepareStatement("INSERT INTO pay_mobile.srs(id$, phone_number, csi, cipher_suite, init_interface, type_code, rmki, sai, sc, src_name, rn, sn) VALUES (pay_mobile.srs_seq.NEXTVAL, " +
                                                     phoneNumber + ", " + csi + ", '" + cipherSuite + "', '" + interfaceCode + "', '" + type + "', " + rmki + ", " + sai + ", " + signatureCounter +
                                                     ", '" + source + "', EMPTY_BLOB(), EMPTY_BLOB()) RETURNING id$, rn, sn INTO ?, ?, ?");
                stmt.registerReturnParameter(1, OracleTypes.NUMBER);
                stmt.registerReturnParameter(2, OracleTypes.BLOB);
                stmt.registerReturnParameter(3, OracleTypes.BLOB);
                stmt.executeUpdate();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Balanse stage 3 [" + connectionId + ":" + phoneNumber + "].");
                rset = stmt.getReturnResultSet();
                rset.next();
                id = rset.getLong(1);
                rset.getBlob(2).setBytes(1, randomNumber);
                rset.getBlob(3).setBytes(1, signature);
                connection.commit();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Balanse stage 4 [" + connectionId + ":" + phoneNumber + "].");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            return id;
        }
    }

    final long insertUpdateAccounts(int connectionId, int type, long phoneNumber, long csi, int sai, byte[] randomNumber, int signatureCounter, byte[] signature, int rmki,
                                    String cipherSuite) throws SQLException, LoggerException {
        OraclePreparedStatement stmt = null;
        ResultSet rset = null;
        long id;
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Update accounts stage 1 [" + connectionId + ":" + phoneNumber + "].");
        synchronized (connection) {
            try {
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Update accounts stage 2 [" + connectionId + ":" + phoneNumber + "].");
                stmt =
(OraclePreparedStatement)connection.prepareStatement("INSERT INTO pay_mobile.srs(id$, phone_number, csi, cipher_suite, init_interface, type_code, rmki, sai, sc, rn, sn) VALUES (pay_mobile.srs_seq.NEXTVAL, " +
                                                     phoneNumber + ", " + csi + ", '" + cipherSuite + "', '" + interfaceCode + "', '" + type + "', " + rmki + ", " + sai + ", " + signatureCounter +
                                                     ", EMPTY_BLOB(), EMPTY_BLOB()) RETURNING id$, rn, sn INTO ?, ?, ?");
                stmt.registerReturnParameter(1, OracleTypes.NUMBER);
                stmt.registerReturnParameter(2, OracleTypes.BLOB);
                stmt.registerReturnParameter(3, OracleTypes.BLOB);
                stmt.executeUpdate();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Update accounts stage 3 [" + connectionId + ":" + phoneNumber + "].");
                rset = stmt.getReturnResultSet();
                rset.next();
                id = rset.getLong(1);
                rset.getBlob(2).setBytes(1, randomNumber);
                rset.getBlob(3).setBytes(1, signature);
                connection.commit();
                FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Update accounts stage 4 [" + connectionId + ":" + phoneNumber + "].");
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            return id;
        }
    }

    final byte[] getData(long id) throws SQLException {
        Statement stmt = null;
        ResultSet rset = null;
        synchronized (connection) {
            try {
                stmt = connection.createStatement();
                rset = stmt.executeQuery("SELECT data FROM pay_mobile.srs WHERE status = 'P' AND id$ = " + id);
                if (rset.next()) {
                    return rset.getBlob(1).getBytes(1, (int)rset.getBlob(1).length());
                } else {
                    return new byte[0];
                }
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }

    public void run() {
        int connectionId = 0;
        while (!FrontServer.cleaningUp) {
            if (connectionId < 0) {
                connectionId = 0;
            }
            try {
                SSLSocket sock = (SSLSocket)serverSocket.accept();
                if (FrontServer.cleaningUp) {
                    sock.close();
                    while (true) {
                        try {
                            sleep(1000000);
                        } catch (InterruptedException e) {
                            Logger.logError("Stopping error:", e);
                        }
                    }
                }
                new Session(this, sock, connectionId++, closeTime).start();
            } catch (IOException e) {
                if (!FrontServer.cleaningUp) {
                    Logger.logError("New connection establishment error:", e);
                }
            }
        }
        while (true) {
            try {
                sleep(1000000);
            } catch (InterruptedException e) {
                Logger.logError("Stopping error:", e);
            }
        }
    }

    final boolean isPasswordValid(char[] password) {
        if (password.length < passwordMinLength || password.length > passwordMaxLength) {
            Decryptator.clearBuffer(password);
            return false;
        }
        byte numbers = 0, letters = 0, specials = 0;
        for (int i = 0; i < password.length; i++) {
            if (Character.isDigit(password[i])) {
                numbers++;
            } else if (Character.isLetter(password[i])) {
                letters++;
            } else {
                specials++;
            }
        }
        if (numbers < passwordMinNumbers || letters < passwordMinLetters || specials < passwordMinSpecials) {
            Decryptator.clearBuffer(password);
            return false;
        }
        return true;
    }

    private final void generateCryptoValues(int connectionId, byte[] masterKey, byte[] salt, char[] password, long phoneNumber, long csi, byte[] appliedAt) throws EngineException,
                                                                                                                                                                   NoSuchAlgorithmException,
                                                                                                                                                                   InvalidKeyException, IOException,
                                                                                                                                                                   ShortBufferException,
                                                                                                                                                                   NoSuchProviderException,
                                                                                                                                                                   LoggerException {
        secureRNG.nextBytes(salt);
        FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 7 [" + connectionId + ":" + phoneNumber + "].");
        switch (saiForNewClients) {
        case SAI_1:
            Mac prf = Mac.getInstance(Decryptator.toString(SAI_1_HMAC_SHA_256), "SunJCE");
            prf.init(new SecretKeySpec(toByte(password), ""));
            Decryptator.clearBuffer(password);
            byte[] data = new byte[SAI_1_LENGTH + 2];
            copy(salt, data, false);
            data[SAI_1_LENGTH] = 1; // Last two bytes of data are 0x0100, which is an L for SAI = 1.
            byte[] u = prf.doFinal(data);
            Decryptator.clearBuffer(data);
            data = null;
            byte[] kk = new byte[u.length];
            copy(u, kk, false);
            for (int c = 0; c < passwordFactor; c++) {
                u = prf.doFinal(u);
                copy(u, kk, true);
            }
            Decryptator.clearBuffer(u);
            u = null;
            prf = null;
            FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 8 [" + connectionId + ":" + phoneNumber + "].");
            if (DEVELOPMENT) {
                prf = Mac.getInstance(Decryptator.toString(SAI_1_HMAC_SHA_256));
            } else {
                prf = Mac.getInstance(Decryptator.toString(SAI_1_HMAC_SHA_256), provider);
            }
            prf.init(rootMasterKey);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.write(Decryptator.toString(SAI_1_MKHMS01).getBytes());
            out.writeLong(phoneNumber);
            out.writeLong(csi);
            out.write(appliedAt);
            out.writeByte(keyId);
            out.writeShort(processorId);
            out.writeShort(SAI_1_L);
            data = baos.toByteArray();
            out.close();
            out = null;
            baos.close();
            baos = null;
            prf.update(data);
            Decryptator.clearBuffer(data);
            data = null;
            prf.doFinal(masterKey, 0);
            prf = null;
            copy(kk, masterKey, true);
            Decryptator.clearBuffer(kk);
            kk = null;
            FrontServer.logger.log(FrontServer.LOG_LEVEL_9, "Initialization stage 9 [" + connectionId + ":" + phoneNumber + "].");
            break;

        default:
            throw new EngineException("E-004.");
        }
    }

    private final void copy(byte[] from, byte[] to, boolean withXor) {
        for (int i = 0; i < from.length; i++) {
            if (withXor) {
                to[i] ^= from[i];
            } else {
                to[i] = from[i];
            }
        }
    }

    private final byte[] toByte(char[] buf) {
        byte[] byteBuf = new byte[buf.length * 2];
        for (int i = 0; i < buf.length; i++) {
            byteBuf[i * 2] = (byte)(buf[i] >>> 8);
            byteBuf[i * 2 + 1] = (byte)buf[i];
        }
        return byteBuf;
    }

    final byte[] createBuffer(int sai) throws EngineException {
        switch (sai) {
        case SAI_1:
            return new byte[SAI_1_LENGTH];

        default:
            throw new EngineException("E-003 [" + sai + "].");
        }
    }
}
