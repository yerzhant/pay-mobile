/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.30   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.iso;

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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


final class Iso implements ErrorHandler {
    private static final String START_TEXT = "IsoInterface 1.1.0\nCopyright (C) 2008-2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: Iso <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/iface/iso/x.exs";
    private static final byte[] TRANSFORMED_SECRET_KEY = { 68, 19, -92, 121, -42, -56, 22, -98, 2, -1, 107, 118, 15, -85, -11, -91, 25, -84, -15, 120, -48, -67, 14, -24 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, -104, -124, -44, 32, 28, -107, -102, -82 };
    private static final byte[] ENCRYPTED_EXPONENT = { 87, -96, 126, -30, 123, 6, 114, -51 };
    private static final byte[] ENCRYPTED_MODULUS =
    { 1, 3, 43, 110, -31, -7, 46, -55, -13, 52, 81, 29, 22, -117, -65, -128, -4, 35, -39, 46, -82, -28, 60, 110, -84, 39, 38, 124, -9, -31, -97, -83, 81, -122, 102, 75, -93, -19, 36, 126, 23, 91, -54, -123, -88, 12, 28, 6, 74, -104, 55, -24, 92, -25, -101, -81, -65, -43, -104, 16, 107, -115, -79,
      99, 55, 67, -10, -116, -18, -79, 126, 122, -10, -81, 86, 124, -69, 1, -78, 63, -12, 89, -63, -123, -88, -102, -87, -79, 7, -7, -90, -56, 10, 99, 47, -97, -104, 9, -57, -44, 29, 85, 21, -5, -71, 92, -16, 19, 126, 121, -65, -106, -48, -110, 113, -76, -50, 30, 27, -36, 78, -2, 92, -120, -116,
      -92, -65, -75, -118, 23, 83, 42, -60, -16, -109, 23, -25, -91, 30, 99, 44, 2, 103, -102, -1, -36, 33, -29, 99, 43, -57, -4, -74, 124, 107, 27, -54, 114, 50, 65, -85, -89, 17, -84, 29, 0, -124, -83, -29, 7, 86, -92, 121, -111, 101, 15, -94, -48, -41, 13, -110, 21, -64, 126, 11, 95, 106, 113,
      -55, -79, -7, -46, 8, 56, -11, -25, -85, -92, 17, -15, -29, -22, -24, 32, -21, -37, -63, 40, 67, -52, -22, -105, -24, -79, -116, 30, -48, 60, 59, -121, -81, -124, 20, 118, 79, -8, 75, 60, -15, 71, -118, -97, -20, 6, -40, 90, 111, -112, -61, -119, 115, -86, 38, -113, 113, -104, -80, -18, 40,
      83, -23, 0, -58, 86, 45, -65, 14, -48, -113, -37, 2, 93, 28, 3 };
    private static Socket srControllerSocket;
    private static DataInputStream srControllerIn;
    private static DataOutputStream srControllerOut;
    private static volatile boolean cleaningUp = false;
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_2 = 2;
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
                Document config = Decryptator.getDocument(new Iso(), CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "IsoInterface started.");

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

                Engine.setupConfiguration((Element)config.getElementsByTagName("isoSystem").item(0));

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
                logger.log(LOG_LEVEL_0, "IsoInterface stopped.");
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
