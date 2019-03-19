/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.29   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.sys;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

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


final class Sys implements ErrorHandler {
    private static final String START_TEXT = "SysInterface 1.0.0\nCopyright (C) 2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: Sys <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/iface/sys/x.exs";
    private static final byte[] TRANSFORMED_SECRET_KEY = { -66, -32, 37, 4, 118, -98, 41, -123, 55, -76, -119, 31, 120, -101, -81, -91, -75, 30, 49, -19, 8, 90, 47, -78 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, 116, -52, 66, 81, -18, -119, -20, -67 };
    private static final byte[] ENCRYPTED_EXPONENT = { -33, -76, -50, 61, 12, 100, 37, 44 };
    private static final byte[] ENCRYPTED_MODULUS =
    { 96, 96, 1, -44, -17, 20, -2, -14, -127, 125, 43, 26, -41, 13, -80, -107, -108, -101, -6, -12, -12, -98, -64, -104, -110, -128, 41, 38, 63, 75, 37, -55, 31, -80, 18, 126, 71, -67, 8, -9, -33, -126, 40, -109, 19, 67, -25, 83, 102, 103, -24, 112, -32, 14, 112, -16, 62, 87, 25, -60, 127, 3, -34,
      -74, -4, -106, -18, -18, -93, 11, -56, -20, -13, 67, 71, -23, -96, 28, 30, 64, -85, 15, 50, 108, 37, -16, 124, 84, 60, -107, 106, -69, 9, 49, 110, 15, 102, 50, -39, -16, -79, -49, 117, -86, -122, -50, -35, 30, 49, -124, -51, 86, -126, 78, 10, 94, 42, -112, -15, 3, -29, -73, 32, 105, 38, -34,
      48, 123, -113, -8, 62, -65, -20, -63, 33, 88, 103, -31, 43, -99, 7, -28, 106, -94, 74, -24, -117, 3, 4, 22, 83, -20, 67, 123, 114, 102, 86, 44, -21, 55, -7, -38, 117, -83, 120, -70, 66, 109, 24, -52, -52, 12, 62, 1, -26, -16, 66, -26, -30, -98, 76, 60, 101, -34, -66, -40, -3, 81, -67, -70,
      -118, -28, 111, -93, -28, -117, 38, -72, -101, -86, -61, -50, 32, -41, 85, 42, 33, -48, 21, -58, -36, 126, 12, -122, -66, -25, 29, -90, 82, 29, 27, -65, 30, 72, -13, -33, -71, 47, -87, 45, 106, 117, -87, -88, 14, 112, -15, -99, -116, 84, 61, -55, 119, 56, 3, -87, 118, 93, -82, 20, 59, 66,
      -76, -62, 27, 95, 12, -47, -98, -116, -116, -109, 66, -65 };
    private static Socket srControllerSocket;
    private static DataInputStream srControllerIn;
    private static DataOutputStream srControllerOut;
    private static volatile boolean cleaningUp = false;
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_9 = 9;
    static Logger logger;
    static Connection connection;
    static String interfaceCode;

    public static void main(String[] args) {
        System.out.println(START_TEXT);
        if (args.length == 1) {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            cleanUp();
                        }
                    });
                Document config = Decryptator.getDocument(new Sys(), CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "SysInterface started.");

                NamedNodeMap database = config.getElementsByTagName("database").item(0).getAttributes();
                interfaceCode = database.getNamedItem("interfaceCode").getNodeValue();
                String dbServer = database.getNamedItem("server").getNodeValue();
                String dbUser = database.getNamedItem("user").getNodeValue();
                char[] dbPassword = Decryptator.getPassword(database.getNamedItem("x").getNodeValue(), database.getNamedItem("a").getNodeValue(), database.getNamedItem("b").getNodeValue());
                connectToDatabase(dbServer, dbUser, dbPassword);

                NamedNodeMap srController = config.getElementsByTagName("srController").item(0).getAttributes();
                String srControllerAddress = srController.getNamedItem("address").getNodeValue();
                int srControllerPort = Integer.parseInt(srController.getNamedItem("port").getNodeValue());
                connectToSrController(srControllerAddress, srControllerPort);

                processNotifications();
            } catch (DecryptatorException e) {
                Logger.logError("Fatal error:", e);
            } catch (LoggerException e) {
                Logger.logError("Fatal error:", e);
            } catch (SQLException e) {
                Logger.logError("Fatal error:", e);
            } catch (UnknownHostException e) {
                Logger.logError("Fatal error:", e);
            } catch (IOException e) {
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

    private static final void connectToSrController(String srcServerAddress, int srcServerPort) throws UnknownHostException, IOException, LoggerException {
        srControllerSocket = new Socket(srcServerAddress, srcServerPort);
        srControllerIn = new DataInputStream(srControllerSocket.getInputStream());
        srControllerOut = new DataOutputStream(srControllerSocket.getOutputStream());
        srControllerOut.writeUTF(interfaceCode);
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
        try {
            cleaningUp = true;
            disconnectFromSrController();
            disconnectFromDatabase();
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "SysInterface stopped.");
                logger.close();
            }
        } catch (SQLException e) {
            Logger.logError("Closing error:", e);
        } catch (LoggerException e) {
            Logger.logError("Closing error:", e);
        } catch (IOException e) {
            Logger.logError("Closing error:", e);
        }
    }

    static final void sendNotification(long id, boolean cleanUp) throws IOException {
        synchronized (srControllerOut) {
            srControllerOut.writeLong(id);
            srControllerOut.writeBoolean(cleanUp);
        }
    }

    private static final void processNotifications() {
        while (true) {
            try {
                new ProcessSR(srControllerIn.readLong(), srControllerIn.readByte(), srControllerIn.readBoolean()).start();
            } catch (IOException e) {
                if (!cleaningUp) {
                    Logger.logError("Processing error:", e);
                }
            } catch (ProcessSRException e) {
                Logger.logError("Processing error:", e);
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
