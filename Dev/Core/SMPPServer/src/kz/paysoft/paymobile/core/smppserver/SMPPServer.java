/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.12   Yerzhan Tulepov         Created.
 */
// S-001

package kz.paysoft.paymobile.core.smppserver;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.pool.OracleDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


final class SMPPServer implements ErrorHandler {
    private static final String START_TEXT = "SMPPServer 1.0.0\nCopyright (C) 2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: SMPPServer <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/core/smppserver/x.exs";
    private static final byte[] ENCRYPTED_MODULUS =
    { -77, -106, -25, -69, -95, 109, 56, 127, -64, 70, 80, -61, -87, 125, 34, 103, -59, -47, -30, 55, 76, 125, -125, 59, 52, 108, 123, -91, 10, -114, 113, -94, 50, 115, 59, 124, 123, -73, 106, 2, 34, 79, 108, -125, -73, -97, 33, 27, -18, -12, 96, -73, -128, -81, -18, -117, 15, 38, -58, 1, 26, 107,
      39, -75, 15, -23, -5, 36, 75, 32, 67, -13, 33, 74, 60, 52, -104, -24, -63, -112, 61, -6, 12, 83, -60, 42, -31, 98, 40, -28, 114, 46, 84, -115, 51, 41, -40, -5, -17, -30, 47, 88, -70, 74, 111, 9, 38, 50, 110, -66, 100, -34, 92, 53, 82, -43, 120, -42, 119, -55, -62, -102, 60, -83, 77, 62, -30,
      -90, 107, -121, 40, -64, 99, -95, 107, 119, 127, 81, 67, -123, -38, -111, -39, 111, -77, 110, -68, 53, 63, -92, -31, 76, -38, -111, -13, 72, 1, -116, 31, -57, -53, 61, 55, -37, 78, -82, 106, -124, 62, -4, 51, 1, 15, 11, -127, -36, -114, 112, 120, 66, -37, -38, 6, 77, 22, 98, -25, 29, -111,
      -1, -33, -118, 107, 99, -102, -23, 37, 38, -124, 107, 47, 116, -44, 21, 12, -18, -44, 32, -15, -55, -50, -103, 109, 107, -54, -28, -26, -90, 69, -37, 6, 55, -49, -2, 46, 26, -58, -97, 48, -4, -42, 109, -62, 67, -23, -96, 64, -66, 102, -13, 122, -84, 8, -49, -46, -69, -118, 74, -44, 17, -64,
      120, 68, -91, -119, 4, -93, -31, 96, -127, 5, -79, -106, -97 };
    private static final byte[] TRANSFORMED_SECRET_KEY = { 86, 94, -95, -80, -33, 2, -5, 121, -62, 85, -65, -29, -127, -9, -31, -93, -94, -28, -101, 24, -89, -58, -111, 75 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, -110, -120, 10, 121, 64, 120, -76, -125 };
    private static final byte[] ENCRYPTED_EXPONENT = { 48, 75, -48, -32, -4, -42, 67, 54 };
    private static Connection connection;
    private static Socket srControllerSocket;
    private static DataInputStream srControllerIn;
    private static DataOutputStream srControllerOut;
    private static Engine engine;
    private static String interfaceCode;
    private static volatile boolean submittingSm = false;
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_2 = 2;
    static final byte LOG_LEVEL_9 = 9;
    static volatile boolean cleaningUp = false;
    static Logger logger;

    public static void main(String[] args) {
        System.out.println(START_TEXT);
        if (args.length == 1) {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            cleanUp();
                        }
                    });

                Document config = Decryptator.getDocument(new SMPPServer(), CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "SMPPServer started.");

                NamedNodeMap database = config.getElementsByTagName("database").item(0).getAttributes();
                interfaceCode = database.getNamedItem("interface").getNodeValue();
                String server = database.getNamedItem("server").getNodeValue();
                String user = database.getNamedItem("user").getNodeValue();
                char[] password = Decryptator.getPassword(database.getNamedItem("x").getNodeValue(), database.getNamedItem("a").getNodeValue(), database.getNamedItem("b").getNodeValue());
                connectToDatabase(server, user, password);

                NamedNodeMap srController = config.getElementsByTagName("srController").item(0).getAttributes();
                String srControllerAddress = srController.getNamedItem("address").getNodeValue();
                int srControllerPort = Integer.parseInt(srController.getNamedItem("port").getNodeValue());
                connectToSrController(srControllerAddress, srControllerPort);

                engine = new Engine(config.getElementsByTagName("smsCenter").item(0).getAttributes());
                engine.start();
                sendNotifications();
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
            } catch (PduException e) {
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
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }
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
        logger.log(LOG_LEVEL_0, "Connected to the SRController.");
    }

    private static final void disconnectFromSrController() throws IOException, LoggerException {
        srControllerOut.writeLong(-1);
        if (srControllerIn != null) {
            srControllerIn.close();
        }
        if (srControllerOut != null) {
            srControllerOut.close();
        }
        if (srControllerSocket != null) {
            srControllerSocket.close();
            logger.log(LOG_LEVEL_0, "Disconnected from the SRController.");
        }
    }

    private static final void cleanUp() {
        System.out.print("Exiting");
        try {
            while (haveActiveSrs()) {
                Thread.sleep(1000);
                System.out.print(".");
            }
            cleaningUp = true;
            while (submittingSm) {
                Thread.sleep(1000);
                System.out.print(".");
            }
            if (engine != null) {
                engine.cleanUp();
            }
            System.out.println();
            disconnectFromSrController();
            disconnectFromDatabase();
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "SMPPServer stopped.");
                logger.close();
            }
        } catch (SQLException e) {
            Logger.logError("Closing error:", e);
        } catch (LoggerException e) {
            Logger.logError("Closing error:", e);
        } catch (IOException e) {
            Logger.logError("Closing error:", e);
        } catch (PduException e) {
            Logger.logError("Closing error:", e);
        } catch (InterruptedException e) {
            Logger.logError("Closing error:", e);
        }
    }

    static final void processConfirmation(long id, String phoneNumber, byte[] request, byte[] response) throws SQLException, IOException, LoggerException {
        Statement stmt = null;
        ResultSet rset = null;
        OraclePreparedStatement pstmt = null;
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Confirmation stage 1.");
        synchronized (connection) {
            try {
                SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Confirmation stage 2.");
                stmt = connection.createStatement();
                rset = stmt.executeQuery("SELECT id$ FROM pay_mobile.srs WHERE id$ = " + id + " AND phone_number = " + phoneNumber + " AND status = 'A' FOR UPDATE");
                if (rset.next()) {
                    rset.close();
                    pstmt = (OraclePreparedStatement)connection.prepareStatement("INSERT INTO pay_mobile.messages(id$, sr_id$, interface, direction, data) VALUES (pay_mobile.messages_seq.NEXTVAL, " + id + ", '" + interfaceCode + "', 'R', EMPTY_BLOB()) RETURNING data INTO ?");
                    pstmt.registerReturnParameter(1, OracleTypes.BLOB);
                    pstmt.executeUpdate();
                    rset = pstmt.getReturnResultSet();
                    rset.next();
                    rset.getBlob(1).setBytes(1, request);
                    rset.close();
                    pstmt.close();
                    pstmt = (OraclePreparedStatement)connection.prepareStatement("INSERT INTO pay_mobile.messages(id$, sr_id$, interface, direction, data) VALUES (pay_mobile.messages_seq.NEXTVAL, " + id + ", '" + interfaceCode + "', 'S', EMPTY_BLOB()) RETURNING data INTO ?");
                    pstmt.registerReturnParameter(1, OracleTypes.BLOB);
                    pstmt.executeUpdate();
                    rset = pstmt.getReturnResultSet();
                    rset.next();
                    rset.getBlob(1).setBytes(1, response);
                    rset.close();
                    pstmt.close();
                    stmt.executeUpdate("INSERT INTO pay_mobile.active_srs(id$) VALUES (" + id + ")");
                    connection.commit();
                    srControllerOut.writeLong(id);
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Confirmation stage 3.");
                } else {
                    rset.close();
                    stmt.executeUpdate("INSERT INTO pay_mobile.system_log(id$, code, description, user$) VALUES (pay_mobile.system_log_seq.NEXTVAL, 'S-001', 'Активный запрос не нейден [" + id + ":" + phoneNumber + "].', USER)");
                    connection.commit();
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }

    private static void sendNotifications() {
        while (!cleaningUp) {
            try {
                long id = srControllerIn.readLong();
                if (cleaningUp) {
                    return;
                }
                submittingSm = true;
                CallableStatement cstmt = null;
                SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Notification stage 1.");
                synchronized (connection) {
                    try {
                        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Notification stage 2.");
                        cstmt = connection.prepareCall("BEGIN pay_mobile.smpp_server_pkg.process_sr_notification(?, ?, ?); END;");
                        cstmt.setLong(1, id);
                        cstmt.registerOutParameter(2, Types.VARCHAR);
                        cstmt.registerOutParameter(3, Types.VARCHAR);
                        cstmt.executeUpdate();
                        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Notification stage 3.");
                        String message = cstmt.getString(3);
                        if (message != null) {
                            engine.submitSM(cstmt.getString(2), message);
                        }
                        connection.commit();
                        cstmt.close();
                        cstmt = null;
                        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_9, "Notification stage 4.");
                    } catch (PduException e) {
                        Logger.logError("Notification processing error:", e);
                    } catch (SQLException e) {
                        Logger.logError("Notification processing sql error:", e);
                    } finally {
                        if (cstmt != null) {
                            try {
                                connection.rollback();
                                cstmt.close();
                            } catch (SQLException e) {
                                Logger.logError("Notification processing closing error:", e);
                            }
                        }
                    }
                }
            } catch (LoggerException e) {
                Logger.logError("Notification processing log error:", e);
            } catch (IOException e) {
                if (!cleaningUp) {
                    Logger.logError("Notification processing io error:", e);
                }
            }
            submittingSm = false;
        }
    }

    private static boolean haveActiveSrs() throws SQLException {
        Statement stmt = null;
        ResultSet rset = null;
        synchronized (connection) {
            try {
                stmt = connection.createStatement();
                rset = stmt.executeQuery("SELECT COUNT(*) FROM pay_mobile.active_srs");
                rset.next();
                if (rset.getLong(1) > 0) {
                    return true;
                } else {
                    return false;
                }
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

