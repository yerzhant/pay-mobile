/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.25   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.srcontroller;

import java.io.FileInputStream;
import java.io.IOException;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.crypto.SecretKey;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import oracle.jdbc.pool.OracleDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


final class SRController implements ErrorHandler {
    static final boolean DEVELOPMENT = true;

    private static final class Notification {
        final long id;
        final boolean cleanUp;

        Notification(long id, boolean cleanUp) {
            this.id = id;
            this.cleanUp = cleanUp;
        }
    }
    private static final LinkedBlockingQueue<Notification> notifications = new LinkedBlockingQueue<Notification>();
    private static final String START_TEXT = "SRController 1.0.0\nCopyright (C) 2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: SRController <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/core/srcontroller/x.exs";
    private static final String KEY_ID_PREFIX = "key_";
    private static final byte[] TRANSFORMED_SECRET_KEY = { 106, 107, -22, 7, 22, 14, 56, -104, 122, -72, 98, 4, 126, 127, -24, -64, -75, 63, -74, -102, -95, 102, -27, -25 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, -107, -79, -92, 85, 50, 59, -30, -65 };
    private static final byte[] ENCRYPTED_EXPONENT = { -52, -117, -33, -78, -125, 18, -124, 101 };
    private static final byte[] ENCRYPTED_MODULUS =
    { -45, 76, -33, -109, -114, -120, 67, -6, 114, -7, -82, 101, -44, -10, 72, -55, -82, 84, 56, 127, 50, 107, 125, 79, 105, -19, -56, -34, -107, -62, 21, -90, -25, -52, -119, -118, -39, -3, -76, 120, -117, 113, -83, -75, 46, -46, 98, 107, -10, -90, -80, -101, 106, -45, 65, -116, -80, -124, 15,
      -59, -120, -112, 23, -109, -90, -22, 39, -115, 122, 123, 89, 22, -95, -26, -107, -81, 95, -3, -103, -62, 66, 62, -94, 125, -100, -58, 98, 30, -4, 79, -48, -67, 8, -19, -76, -89, -128, -22, -86, 105, 64, 24, -104, -128, -51, 92, -125, -111, 16, -12, -56, 42, 98, 68, -30, -95, 21, -71, 26,
      -102, -4, -3, -18, 46, -56, 35, 61, 100, 125, -110, 122, 70, -44, -49, 116, 42, -76, 97, -28, -53, 17, -8, -95, -128, -107, -53, -128, -58, 20, -51, 72, -49, -70, -93, 68, -68, 24, -101, 54, 14, -112, -108, 88, 62, 71, 60, -13, 125, 77, -39, 116, -7, -21, -19, 63, -116, 4, 56, 105, -119, 7,
      -48, 100, 23, 94, 61, -85, 63, 58, 41, -102, 2, -14, 6, 18, 4, 118, 29, -15, -54, -28, -45, 67, -8, -114, 116, -28, 10, 119, -36, 29, 10, -108, -118, -108, 100, 49, 97, -24, 70, 63, 9, -107, -127, 113, -122, -50, -118, 24, 21, 13, -102, -121, -76, -100, -19, -7, -120, -47, -111, 70, -37, 80,
      85, 77, -17, 120, -108, -105, -48, 40, -10, 74, -94, -5, 31, -99, 79, 73, -33, 74, 45, -76, -107 };
    private static final byte[] JCECSP_DEBUG = { -75, -68, -70, -68, -84, -81, -96, -69, -70, -67, -86, -72 }; // JCECSP_DEBUG
    private static final byte[] NFJAVA_DEBUG = { -79, -71, -75, -66, -87, -66, -96, -69, -70, -67, -86, -72 }; // NFJAVA_DEBUG
    private static final byte[] COM_NCIPHER_PROVIDER_DISABLE = { -100, -112, -110, -47, -111, -100, -106, -113, -105, -102, -115, -47, -113, -115, -112, -119, -106, -101, -102, -115, -47, -101, -106, -116, -98, -99, -109, -102 }; // com.ncipher.provider.disable
    private static final byte[] _DEVELOPMENT_ =
    { -43, -43, -43, -43, -43, -43, -43, -33, -69, -33, -70, -33, -87, -33, -70, -33, -77, -33, -80, -33, -81, -33, -78, -33, -70, -33, -79, -33, -85, -33, -33, -33, -70, -33, -79, -33, -66, -33, -67, -33, -77, -33, -70, -33, -69, -33, -43, -43, -43, -43, -43, -43, -43 };
    static final ConcurrentHashMap<String, Interface> interfaces = new ConcurrentHashMap<String, Interface>();
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_9 = 9;
    static Logger logger;
    static Connection connection;
    static String provider;
    static int timeOut, refreshInterval;

    public static void main(String[] args) {
        System.out.println(START_TEXT);
        if (args.length == 1) {
            if (DEVELOPMENT) {
                System.err.println(Decryptator.toString(_DEVELOPMENT_));
            }
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            cleanUp();
                        }
                    });
                Document config = Decryptator.getDocument(new SRController(), CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "SRController started.");

                NamedNodeMap database = config.getElementsByTagName("database").item(0).getAttributes();
                String dbServer = database.getNamedItem("server").getNodeValue();
                String dbUser = database.getNamedItem("user").getNodeValue();
                char[] dbPassword = Decryptator.getPassword(database.getNamedItem("x").getNodeValue(), database.getNamedItem("a").getNodeValue(), database.getNamedItem("b").getNodeValue());
                connectToDatabase(dbServer, dbUser, dbPassword);

                NamedNodeMap server = config.getElementsByTagName("server").item(0).getAttributes();
                ProcessSR.processorId = Integer.parseInt(server.getNamedItem("processorId").getNodeValue());
                timeOut = Integer.parseInt(server.getNamedItem("timeOut").getNodeValue());
                refreshInterval = Integer.parseInt(server.getNamedItem("refreshInterval").getNodeValue());
                new InterfaceListener(Integer.parseInt(server.getNamedItem("interfacePort").getNodeValue())).start();
                new SMPPServer(Integer.parseInt(server.getNamedItem("smppServerPort").getNodeValue())).start();
                new FrontServer(Integer.parseInt(server.getNamedItem("frontServerPort").getNodeValue())).start();
                new Cleaner().start();

                NamedNodeMap crypto = config.getElementsByTagName("crypto").item(0).getAttributes();
                provider = crypto.getNamedItem("provider").getNodeValue();
                KeyStore keyStore;
                if (DEVELOPMENT) {
                    keyStore = KeyStore.getInstance(crypto.getNamedItem("storeType").getNodeValue());
                } else {
                    if (System.getProperty(Decryptator.toString(JCECSP_DEBUG)) != null) {
                        throw new RuntimeException();
                    } else if (System.getProperty(Decryptator.toString(NFJAVA_DEBUG)) != null) {
                        throw new RuntimeException();
                    } else if (System.getProperty(Decryptator.toString(COM_NCIPHER_PROVIDER_DISABLE)) != null) {
                        throw new RuntimeException();
                    }
                    keyStore = KeyStore.getInstance(crypto.getNamedItem("storeType").getNodeValue(), provider);
                }
                char[] keyStorePsw = Decryptator.getPassword(crypto.getNamedItem("x").getNodeValue(), crypto.getNamedItem("a").getNodeValue(), crypto.getNamedItem("b").getNodeValue());
                keyStore.load(new FileInputStream(crypto.getNamedItem("store").getNodeValue()), keyStorePsw);
                NodeList keys = config.getElementsByTagName("crypto").item(0).getChildNodes();
                for (int i = 0; i < keys.getLength(); i++) {
                    Node child = keys.item(i);
                    if (child.getNodeName().equals("key")) {
                        String keyId = child.getAttributes().getNamedItem("id").getNodeValue();
                        SecretKey key = (SecretKey)keyStore.getKey(KEY_ID_PREFIX + keyId, keyStorePsw);
                        ProcessSR.rootMasterKeys.put(Byte.parseByte(keyId), key);
                    }
                }
                Decryptator.clearBuffer(keyStorePsw);
                processNotifications();
            } catch (DecryptatorException e) {
                Logger.logError("Fatal error:", e);
            } catch (LoggerException e) {
                Logger.logError("Fatal error:", e);
            } catch (SQLException e) {
                Logger.logError("Fatal error:", e);
            } catch (IOException e) {
                Logger.logError("Fatal error:", e);
            } catch (NoSuchAlgorithmException e) {
                Logger.logError("Fatal error:", e);
            } catch (KeyStoreException e) {
                Logger.logError("Fatal error:", e);
            } catch (CertificateException e) {
                Logger.logError("Fatal error:", e);
            } catch (UnrecoverableKeyException e) {
                Logger.logError("Fatal error:", e);
            } catch (NoSuchProviderException e) {
                Logger.logError("Fatal error:", e);
            }
        } else {
            System.out.println(USAGE_TEXT);
        }
    }

    private static final void connectToDatabase(String server, String user, char[] password) throws SQLException, LoggerException {
        OracleDataSource ods = new OracleDataSource();
        ods.setURL("jdbc:oracle:thin:@//" + server);
        ods.setUser(user);
        ods.setPassword(new String(password));
        Decryptator.clearBuffer(password);
        connection = ods.getConnection();
        connection.setAutoCommit(false);
        logger.log(LOG_LEVEL_0, "Connected to the database.");
    }

    private static final void disconnectFromDatabase() throws SQLException, LoggerException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.close();
            logger.log(LOG_LEVEL_0, "Disconnected from the database.");
        }
    }

    private static final void cleanUp() {
        try {
            disconnectFromDatabase();
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "SRController stopped.");
                logger.close();
            }
        } catch (SQLException e) {
            Logger.logError("Closing error:", e);
        } catch (LoggerException e) {
            Logger.logError("Closing error:", e);
        }
    }

    static final void enqueue(long id, boolean cleanUp) throws InterruptedException {
        notifications.put(new Notification(id, cleanUp));
    }

    private static void processNotifications() {
        while (true) {
            try {
                Notification ntf = notifications.take();
                if (ntf.cleanUp) {
                    new CleanUpSR(ntf.id).start();
                } else {
                    new ProcessSR(ntf.id).start();
                }
            } catch (InterruptedException e) {
                Logger.logError("Notification processing error:", e);
            }
        }
    }

    public void warning(SAXParseException exception) {
        System.out.println("Configuration warning.");
        System.exit(-1);
    }

    public void error(SAXParseException exception) {
        System.out.println("Configuration error.");
        System.exit(-1);
    }

    public void fatalError(SAXParseException exception) {
        System.out.println("Configuration fatal error.");
        System.exit(-1);
    }
}
