/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.net.Socket;
import java.net.UnknownHostException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import oracle.jdbc.pool.OracleDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


final class Xml extends Thread implements ErrorHandler {
    private static final String START_TEXT = "XmlInterface 1.1.0\nCopyright (C) 2008-2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: Xml <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/iface/xml/x.exs";
    private static final byte[] TRANSFORMED_SECRET_KEY = { -66, 94, 74, 1, -39, 76, 37, -75, 52, -106, 22, 61, -13, 64, 111, -84, -42, 114, 88, 57, -94, -73, 49, 85 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, 6, 8, -53, 28, -71, -91, -36, -77 };
    private static final byte[] ENCRYPTED_EXPONENT = { -98, -24, 20, -92, 25, 101, 1, 127 };
    private static final byte[] ENCRYPTED_MODULUS =
    { -123, 48, -125, 55, -56, -37, 65, -36, -55, 126, 124, 75, -101, 91, 35, -63, 54, 122, 45, 82, -104, 7, -38, 13, -117, -43, -11, -80, -16, 75, -32, -128, 50, 72, 122, -92, 17, -92, -22, -19, -58, -7, 0, 19, -71, -48, 105, -8, -84, -16, 2, 29, -61, -113, -37, 29, 51, -31, 72, 23, -83, -23, 116,
      -92, 33, 53, -124, 63, 44, -65, -83, 9, 19, -87, -84, -32, -39, 1, 62, 104, -59, -38, -50, -46, -42, -51, 27, -16, 95, 14, 92, -42, -94, 97, 78, -3, 4, 0, -37, -25, -30, 119, -71, 76, -122, -8, 75, 23, 51, -19, 90, -125, -7, -115, 93, -4, 65, 48, 23, -8, 126, 69, -14, -116, 106, -92, 90, 37,
      97, -83, -98, 77, -111, 21, -56, 50, 36, -78, 28, 11, 66, 70, -16, -1, -11, -77, 58, -79, 72, -88, -55, -65, -5, -26, 55, -109, -33, -126, 2, 75, -42, 52, 4, 26, 98, 18, 7, 106, -122, -26, 76, 57, -107, 48, 51, -107, -84, -85, 75, -99, -58, -6, -32, 40, -84, -48, -75, -71, 7, 60, -104, 106,
      -71, -43, 35, 115, 27, -109, 123, 86, 56, 14, 110, 78, -28, -4, 47, 41, 64, 102, -35, 108, 23, 95, 125, -14, 23, 85, -82, -106, 109, -68, 4, -74, 102, -30, 28, 24, -29, 21, -4, -56, 32, -19, -107, 2, -116, -10, -60, -116, -90, 92, -128, -104, -48, -19, 127, -110, 121, -21, 22, -118, 115, -14,
      -16, -117, 79, 50, 42, 6, 124, 64, -71, -10 };
    private static Socket srControllerSocket;
    private static DataInputStream srControllerIn;
    private static DataOutputStream srControllerOut;
    private static Socket xmlServerSocket;
    private static InputStream xmlServerIn;
    private static OutputStream xmlServerOut;
    private static DocumentBuilder docBuilder;
    private static TransformerFactory transformerFactory;
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
                docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                transformerFactory = TransformerFactory.newInstance();
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            cleanUp();
                        }
                    });
                Xml xml = new Xml();
                Document config = Decryptator.getDocument(xml, CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "XmlInterface started.");

                NamedNodeMap database = config.getElementsByTagName("database").item(0).getAttributes();
                interfaceCode = database.getNamedItem("interfaceCode").getNodeValue();
                String dbServer = database.getNamedItem("server").getNodeValue();
                String dbUser = database.getNamedItem("user").getNodeValue();
                char[] dbPassword = Decryptator.getPassword(database.getNamedItem("x").getNodeValue(), database.getNamedItem("a").getNodeValue(), database.getNamedItem("b").getNodeValue());
                connectToDatabase(dbServer, dbUser, dbPassword);

                NamedNodeMap xmlServer = config.getElementsByTagName("xmlServer").item(0).getAttributes();
                String xmlServerAddress = xmlServer.getNamedItem("address").getNodeValue();
                int xmlServerPort = Integer.parseInt(xmlServer.getNamedItem("port").getNodeValue());
                connectToXmlServer(xmlServerAddress, xmlServerPort);

                NamedNodeMap srController = config.getElementsByTagName("srController").item(0).getAttributes();
                String srControllerAddress = srController.getNamedItem("address").getNodeValue();
                int srControllerPort = Integer.parseInt(srController.getNamedItem("port").getNodeValue());
                connectToSrController(srControllerAddress, srControllerPort);

                xml.start();
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
            } catch (ParserConfigurationException e) {
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

    private static final void connectToXmlServer(String xmlServerAddress, int xmlServerPort) throws UnknownHostException, IOException, LoggerException {
        xmlServerSocket = new Socket(xmlServerAddress, xmlServerPort);
        xmlServerIn = xmlServerSocket.getInputStream();
        xmlServerOut = xmlServerSocket.getOutputStream();
        logger.log(LOG_LEVEL_0, "Connected to the XML Server.");
    }

    private static final void disconnectFromXmlServer() throws IOException, LoggerException {
        if (xmlServerOut != null) {
            xmlServerOut.write(0xff);
            xmlServerOut.write(0xff);
            xmlServerOut.close();
        }
        if (xmlServerIn != null) {
            xmlServerIn.close();
        }
        if (xmlServerSocket != null) {
            xmlServerSocket.close();
            logger.log(LOG_LEVEL_0, "Disconnected from the XML Server.");
        }
    }

    private static final void cleanUp() {
        try {
            cleaningUp = true;
            disconnectFromSrController();
            disconnectFromXmlServer();
            disconnectFromDatabase();
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "XmlInterface stopped.");
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
                new ProcessRequest(srControllerIn.readLong(), srControllerIn.readByte(), srControllerIn.readBoolean()).start();
            } catch (IOException e) {
                if (!cleaningUp) {
                    Logger.logError("Sr processing error:", e);
                }
            } catch (ProcessException e) {
                Logger.logError("Sr processing error:", e);
            }
        }
    }

    static final void sendRequest(long id, CallableStatement stmt) throws SQLException, TransformerConfigurationException, TransformerException, IOException, ProcessException, LoggerException {
        Document doc = docBuilder.newDocument();
        Element req = doc.createElement("request");
        req.setAttribute("id", Long.toString(id));
        String type = stmt.getString("p_code");
        req.setAttribute("type", type);
        req.setAttribute("date", stmt.getString("p_date"));
        String account = stmt.getString("p_account");
        req.setAttribute("account", account);
        String currency = stmt.getString("p_currency");
        if (currency != null) {
            String amount = new BigDecimal(stmt.getLong("p_amount")).divide(new BigDecimal(100)).toPlainString();
            req.setAttribute("amount", amount);
            req.setAttribute("currency", currency);
        }
        String paymentTypeCode = stmt.getString("p_ptc");
        if (paymentTypeCode != null) {
            req.setAttribute("paymentTypeCode", paymentTypeCode);
        }
        String description = stmt.getString("p_description");
        if (description != null) {
            req.setAttribute("description", description);
        }
        int statementType = stmt.getInt("p_st_type");
        if (statementType != -1) {
            req.setAttribute("statementType", Integer.toString(statementType));
        }
        doc.appendChild(req);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        transformerFactory.newTransformer().transform(new DOMSource(doc), new StreamResult(baos));
        int len = baos.size();
        if (len > 65534) {
            baos.close();
            throw new ProcessException("The message is too long [" + len + "].");
        }
        byte[] buf = baos.toByteArray();
        baos.close();
        synchronized (xmlServerOut) {
            xmlServerOut.write(len >> 8);
            xmlServerOut.write(len);
            xmlServerOut.write(buf);
        }
        stmt.getBlob("p_data").setBytes(1, buf);
        logger.log(LOG_LEVEL_1, "Sent [" + id + ":" + type + ":" + account + "].");
        logger.log(LOG_LEVEL_2, "Sent "  + buf.length + " bytes [" + id + "]:", buf);
    }

    public void run() {
        while (true) {
            try {
                byte[] buf = new byte[xmlServerIn.read() << 8 | xmlServerIn.read()];
                xmlServerIn.read(buf);
                ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                new ProcessResponse(docBuilder.parse(bais).getDocumentElement(), buf).start();
                bais.close();
            } catch (IOException e) {
                if (!cleaningUp) {
                    Logger.logError("Xml processing io error:", e);
                }
            } catch (ProcessException e) {
                Logger.logError("Xml processing pr error:", e);
            } catch (SAXException e) {
                Logger.logError("Xml processing sax error:", e);
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
