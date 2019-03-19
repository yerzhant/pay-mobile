/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.22   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppmodem;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.TooManyListenersException;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


final class SMPPModem implements ErrorHandler, SerialPortEventListener {
    private static final String START_TEXT = "SMPPModem 1.0.0\nCopyright (C) 2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: SMPPModem <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/core/smppmodem/x.exs";
    private static final String OUT_PREFIX = "< ";
    private static final String IN_PREFIX = "> ";
    private static final String PORT_OWNER = "SMPPModem";
    private static final int PORT_TIMEOUT = 2000;
    private static final byte[] TRANSFORMED_SECRET_KEY = { -52, -60, 55, 81, 118, 84, 4, 70, 56, -46, 127, -108, 101, -63, 43, -79, 115, -46, -98, -87, 109, -127, -29, -55 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, -99, -51, -82, 23, -74, 93, -107, 21 };
    private static final byte[] ENCRYPTED_EXPONENT = { -64, 113, 31, -30, 110, 112, 85, 58 };
    private static final byte[] ENCRYPTED_MODULUS =
    { 26, 116, 41, -39, -74, -79, -109, 53, 75, -56, 124, -116, -47, 37, 123, 60, -91, -2, 102, 64, 123, -102, 45, -124, 88, -4, -123, -127, -98, 69, -71, -118, -59, -125, 101, -46, -71, -88, 122,
      103, -85, -45, 116, 82, 55, -96, 120, 27, 105, -122, -95, -109, 99, 106, -50, 13, -116, -66, -55, -32, -94, -17, 82, -38, -43, -37, -69, 26, -26, -72, 68, 102, -87, 62, 25, 96, 75, 26, 18, -74,
      26, -64, 118, -111, -36, 61, 100, -34, 37, 118, 120, -33, 1, -65, -125, -52, 92, -96, -60, -47, -117, 50, 49, -69, 80, -61, 5, -105, -72, -108, 125, 3, -93, 57, -82, -107, 11, -26, -53, 74,
      -46, 59, -86, 22, 115, 108, 78, 72, 115, -45, 48, 64, 108, 111, 7, 37, -45, 107, 125, -109, 77, 88, -114, -27, 63, 104, 95, 20, 109, -37, -16, -72, -84, 16, -22, -8, -91, 74, -121, -55, -53,
      63, -57, -71, -40, -8, -48, -8, -125, -66, -88, -63, -125, -104, 120, 22, 41, 127, -11, -112, 102, -6, -81, -54, 111, 63, -40, 74, 16, 120, 50, 119, -64, 110, 85, 117, -28, 52, 68, -21, 122,
      69, 8, -4, -40, -14, 43, -80, -73, 116, -31, -57, 68, 80, -121, -56, 25, -105, 44, 93, -120, 74, -23, 93, -120, 54, -69, -33, -114, 121, -79, -56, 121, -91, -90, -114, 52, 110, -56, -31, -79,
      13, -61, -44, 114, -62, 48, 20, -119, -96, 118, 98, 37, -22, 2, -58, 105, -47, 110, -76, 45, 82, 29, -29 };
    private static SerialPort serialPort;
    private static BufferedReader modemIn;
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_2 = 2;
    static volatile boolean cleaningUp = false;
    static Logger logger;
    static PrintWriter modemOut;

    public static void main(String[] args) {
        System.out.println(START_TEXT);
        if (args.length == 1) {
            try {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            cleanUp();
                        }
                    });
                SMPPModem smppModem = new SMPPModem();
                Document config = Decryptator.getDocument(smppModem, CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY, ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);
                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "SMPPModem started.");
                initModem(smppModem, config.getElementsByTagName("modem").item(0));
                Engine.run(config.getElementsByTagName("server").item(0));
            } catch (DecryptatorException e) {
                Logger.logError("Fatal error:", e);
            } catch (LoggerException e) {
                Logger.logError("Fatal error:", e);
            } catch (IOException e) {
                if (!cleaningUp) {
                    Logger.logError("Fatal error:", e);
                }
            } catch (NoSuchPortException e) {
                Logger.logError("Fatal error:", e);
            } catch (PortInUseException e) {
                Logger.logError("Fatal error:", e);
            } catch (UnsupportedCommOperationException e) {
                Logger.logError("Fatal error:", e);
            } catch (TooManyListenersException e) {
                Logger.logError("Fatal error:", e);
            } catch (EngineException e) {
                Logger.logError("Fatal error:", e);
            }
        } else {
            System.out.println(USAGE_TEXT);
        }
        System.exit(0);
    }

    private static final void cleanUp() {
        cleaningUp = true;
        try {
            Engine.cleanUp();
            if (modemIn != null) {
                modemIn.close();
            }
            if (modemOut != null) {
                modemOut.close();
            }
            if (serialPort != null) {
                serialPort.close();
            }
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "SMPPModem stopped.");
                logger.close();
            }
        } catch (LoggerException e) {
            Logger.logError("Closing error:", e);
        } catch (IOException e) {
            Logger.logError("Closing error:", e);
        }
    }

    private static final void initModem(SMPPModem smppModem, Node modem) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, LoggerException, IOException,
                                                                                TooManyListenersException {
        NamedNodeMap attrs = modem.getAttributes();
        serialPort = (SerialPort)CommPortIdentifier.getPortIdentifier(attrs.getNamedItem("port").getNodeValue()).open(PORT_OWNER, PORT_TIMEOUT);
        serialPort.addEventListener(smppModem);
        serialPort.notifyOnDataAvailable(true);
        logger.log(LOG_LEVEL_1, "Port opened.");
        int baudRate = Integer.parseInt(attrs.getNamedItem("baudRate").getNodeValue());
        int dataBits = Integer.parseInt(attrs.getNamedItem("dataBits").getNodeValue());
        int stopBits = Integer.parseInt(attrs.getNamedItem("stopBits").getNodeValue());
        int parity = Integer.parseInt(attrs.getNamedItem("parity").getNodeValue());
        serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
        logger.log(LOG_LEVEL_1, "Port parameters set.");
        modemIn = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        modemOut = new PrintWriter(serialPort.getOutputStream());
        NodeList initStrings = modem.getChildNodes();
        for (int i = 0; i < initStrings.getLength(); i++) {
            Node child = initStrings.item(i);
            if (child.getNodeName().equals("init")) {
                sendString(child.getAttributes().getNamedItem("string").getNodeValue());
            }
        }
        logger.log(LOG_LEVEL_1, "Port initialized.");
    }

    static final void sendString(String string) throws LoggerException {
        modemOut.println(string);
        modemOut.flush();
        logger.log(LOG_LEVEL_2, OUT_PREFIX + string);
    }

    static final String readString() throws LoggerException {
        String string = null;
        try {
            string = modemIn.readLine();
            logger.log(LOG_LEVEL_2, IN_PREFIX + string);
        } catch (IOException e) {
            e = null;
        }
        return string;
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        try {
            while (!cleaningUp && modemIn.ready()) {
                Engine.processModemIn(readString());
            }
        } catch (IOException e) {
            Logger.logError("Read error:", e);
        } catch (LoggerException e) {
            Logger.logError("Read error:", e);
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
