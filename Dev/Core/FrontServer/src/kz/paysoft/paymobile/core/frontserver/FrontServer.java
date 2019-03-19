/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.17   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.frontserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Arrays;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import oracle.jdbc.pool.OracleDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


final class FrontServer implements ErrorHandler {
    private static final String START_TEXT = "FrontServer 1.0.0\nCopyright (C) 2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: FrontServer <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/core/frontserver/x.exs";
    private static final byte[] TRANSFORMED_SECRET_KEY = { -90, -94, 22, -29, 69, -12, -42, -98, -39, 0, -118, -63, 83, -2, 102, 101, -88, -93, -113, -72, 7, -13, -27, 68 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, -28, -123, -38, 80, -13, 66, 96, -62 };
    private static final byte[] ENCRYPTED_EXPONENT = { 46, -79, -69, -74, -93, -86, 96, 36 };
    private static final byte[] ENCRYPTED_MODULUS =
    { 98, -11, -47, -8, -23, -81, 50, 16, -113, -93, 118, 127, -78, 58, 15, -64, 32, 37, 84, 1, -64, -26, -77, -22, 11, 46, -17, -3, -1, 33, 124, -57, 69, 120, -1, -89, 125, 52, -82, -59, -80, 112, -101, 48, 90, -25, -110, 112, 121, -43, -38, -125, 88, 69, -47, 43, -62, -124, 10, 66, 100, -55, -24,
      106, -101, 65, -23, -110, 98, -113, 75, -4, 67, 20, 82, -33, 8, -81, 38, 47, -28, 79, -128, -56, -37, -49, 20, 102, 29, 70, -88, -28, -5, 22, 14, -112, 60, -116, 82, 46, -70, 85, -56, 60, 123, 12, 27, -17, -10, -120, 56, 3, 64, -6, 121, -120, 7, -94, -29, -69, -83, 94, 6, 95, -125, -3, 94,
      -25, 26, -9, -104, 103, 99, 12, 16, 73, 76, -22, 116, -115, 75, 62, 85, -106, -51, 9, -89, 123, 1, -115, -68, 64, 46, 62, -97, -121, 58, 77, 49, 44, -16, -104, 18, 21, 127, -59, 119, 11, 112, -5, 55, 84, -4, -5, -21, -50, 81, -75, 22, -13, -68, -48, -60, -102, 107, -33, -123, 37, -55, -61,
      -29, 5, -71, -77, 96, 6, 7, -15, 16, 105, -34, 37, -121, -42, -97, -125, -89, 58, 35, 76, -9, -38, 110, 28, 94, -128, 60, -1, 56, -117, 32, -58, -19, -49, 66, 96, 15, -62, -32, -58, -30, -111, 40, 53, 83, -123, -39, 97, -90, -80, -79, 18, -98, 103, 105, 122, 17, 112, -67, -120, 15, -45, 40,
      123, 17, -102, 2, -16, -42, 61, 12, 108, 37, 41 };
    private static Socket srControllerSocket;
    private static Engine engine;
    private static DataOutputStream srControllerOut;
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_9 = 9;
    static volatile boolean cleaningUp = false;
    static Logger logger;
    static Connection connection;
    static DataInputStream srControllerIn;

    public static void main(String[] args) {
        System.out.println(START_TEXT);
        if (args.length == 1) {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            cleanUp();
                        }
                    });
                Document config = Decryptator.getDocument(new FrontServer(), CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "FrontServer started.");

                NamedNodeMap database = config.getElementsByTagName("database").item(0).getAttributes();
                String server = database.getNamedItem("server").getNodeValue();
                String user = database.getNamedItem("user").getNodeValue();
                char[] password = Decryptator.getPassword(database.getNamedItem("x").getNodeValue(), database.getNamedItem("a").getNodeValue(), database.getNamedItem("b").getNodeValue());
                connectToDatabase(server, user, password);

                NamedNodeMap srController = config.getElementsByTagName("srController").item(0).getAttributes();
                String srControllerAddress = srController.getNamedItem("address").getNodeValue();
                int srControllerPort = Integer.parseInt(srController.getNamedItem("port").getNodeValue());
                connectToSrController(srControllerAddress, srControllerPort);

                engine = new Engine(config.getElementsByTagName("server").item(0), config.getElementsByTagName("crypto").item(0), config.getElementsByTagName("passwordValidation").item(0).getAttributes());
                engine.start();
                engine.processSRControllerNotifications();
            } catch (DecryptatorException e) {
                Logger.logError("Fatal error:", e);
            } catch (LoggerException e) {
                Logger.logError("Fatal error:", e);
            } catch (SQLException e) {
                Logger.logError("Fatal error:", e);
            } catch (UnknownHostException e) {
                Logger.logError("Fatal error:", e);
            } catch (IOException e) {
                if (!cleaningUp) {
                    Logger.logError("Fatal error:", e);
                }
            } catch (NoSuchAlgorithmException e) {
                Logger.logError("Fatal error:", e);
            } catch (KeyStoreException e) {
                Logger.logError("Fatal error:", e);
            } catch (CertificateException e) {
                Logger.logError("Fatal error:", e);
            } catch (UnrecoverableKeyException e) {
                Logger.logError("Fatal error:", e);
            } catch (KeyManagementException e) {
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
        Arrays.fill(password, '\u0000');
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

    private static final void connectToSrController(String srcServerAddress, int srcServerPort) throws LoggerException, UnknownHostException, IOException {
        srControllerSocket = new Socket(srcServerAddress, srcServerPort);
        srControllerIn = new DataInputStream(srControllerSocket.getInputStream());
        srControllerOut = new DataOutputStream(srControllerSocket.getOutputStream());
        logger.log(LOG_LEVEL_0, "Connected to the SRController.");
    }

    private static final void disconnectFromSrController() throws IOException, LoggerException {
        if (srControllerOut != null) {
            srControllerOut.writeLong(-1);
            srControllerOut.close();
        }
        if (srControllerIn != null) {
            srControllerIn.close();
        }
        if (srControllerSocket != null) {
            srControllerSocket.close();
            logger.log(LOG_LEVEL_0, "Disconnected from the SRController.");
        }
    }

    private static final void cleanUp() {
        cleaningUp = true;
        try {
            if (engine != null) {
                engine.cleanUp();
            }
            disconnectFromSrController();
            disconnectFromDatabase();
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "FrontServer stopped.");
                logger.close();
            }
        } catch (InterruptedException e) {
            Logger.logError("Closing error:", e);
        } catch (SQLException e) {
            Logger.logError("Closing error:", e);
        } catch (LoggerException e) {
            Logger.logError("Closing error:", e);
        } catch (IOException e) {
            Logger.logError("Closing error:", e);
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
