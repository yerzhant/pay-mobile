/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.18   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.otauploader;

import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;


final class OTAUploader implements ErrorHandler {
    private static final String START_TEXT = "OTAUploader 1.0.0\nCopyright (C) 2010 PaySoft, LLP. All rights reserved.";
    private static final String USAGE_TEXT = "Usage: OTAUploader <configuration file>";
    private static final String CONFIGURATION_SCHEMA = "kz/paysoft/paymobile/core/otauploader/x.exs";
    private static final byte[] TRANSFORMED_SECRET_KEY =
    { 99, 37, 82, -2, 67, -116, -9, -110, -128, -61, -118, 124, -79, -32, 119, -67, 62, 113, 41, -121, 98, 102, 112, -6 };
    private static final byte[] SECRET_ALGORITHM_PARAMETERS = { 4, 8, 65, -106, 47, 15, -49, -84, -72, 101 };
    private static final byte[] ENCRYPTED_EXPONENT = { -20, 35, -7, -58, 2, 110, 42, -88 };
    private static final byte[] ENCRYPTED_MODULUS =
    { -121, -21, 93, -92, 100, -103, 124, -37, 119, -53, 92, -55, 14, -96, 86, -17, -80, -102, -31, 117, -51, -97, 101, -62, -33, 0, -50, 112, 71,
      122, 104, -69, -47, 74, 110, -63, -103, 41, -6, 42, 35, -13, 125, 120, -29, 68, 33, -21, -1, 26, 53, 118, -65, 114, -72, 79, 43, -126, 43, 94,
      62, -103, -4, -122, -68, 79, 84, -117, -92, -74, -26, -6, 71, -46, -118, -75, -43, -96, 108, 127, -48, -53, 17, -92, -65, 43, 39, 23, -2, 116,
      69, 80, 118, -76, -76, -4, 94, 18, 108, 64, -76, 32, -110, 53, 107, 42, 59, -26, 27, -70, 14, -35, 71, -85, 33, 85, 53, 24, 92, -20, 9, -24,
      -46, 56, 125, 112, -89, 103, -46, -6, 72, 73, 29, 91, -23, -128, -2, -5, 49, -116, 2, -79, 120, -126, -40, 82, -99, 73, -43, -6, 4, -37, -111,
      -66, -19, 77, -15, -13, -97, -19, 17, -93, -33, -41, -44, -113, -71, 42, 36, 72, -116, -105, 88, 2, -94, -16, 48, 104, 17, 59, -77, 46, 97,
      -103, -48, 28, 61, -1, 66, 15, -90, -81, 89, 25, 82, -34, -121, 125, -90, -121, -16, -92, 6, 31, 8, 36, 54, -4, -43, -67, 79, -64, 71, 111, 24,
      -42, -84, -110, 124, 9, -2, 34, 72, 51, -72, -18, -65, -106, 9, -70, -121, -12, 17, -107, 106, 116, 41, -29, -43, 56, 111, -34, 22, 122, -90,
      -50, 52, -83, 62, 66, -20, 109, 104, 14, -79, 99, -77, 97, 83, 75, -115, -65, 2, 42 };
    private static Engine engine;
    static final byte LOG_LEVEL_0 = 0;
    static final byte LOG_LEVEL_1 = 1;
    static final byte LOG_LEVEL_2 = 2;
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
                Document config =
                    Decryptator.getDocument(new OTAUploader(), CONFIGURATION_SCHEMA, args[0], SECRET_ALGORITHM_PARAMETERS, TRANSFORMED_SECRET_KEY,
                                            ENCRYPTED_EXPONENT, ENCRYPTED_MODULUS);

                NamedNodeMap logging = config.getElementsByTagName("logging").item(0).getAttributes();
                String level = logging.getNamedItem("level").getNodeValue();
                String file = logging.getNamedItem("file").getNodeValue();
                String size = logging.getNamedItem("size").getNodeValue();
                String delimiter = logging.getNamedItem("delimiter").getNodeValue();
                logger = new Logger(level, file, size, delimiter);
                logger.log(LOG_LEVEL_0, "OTAUploader started.");

                engine = new Engine(config.getElementsByTagName("server").item(0), config.getElementsByTagName("crypto").item(0));
                engine.run();
            } catch (DecryptatorException e) {
                Logger.logError("Fatal error:", e);
            } catch (LoggerException e) {
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
            } catch (KeyManagementException e) {
                Logger.logError("Fatal error:", e);
            } catch (NoSuchProviderException e) {
                Logger.logError("Fatal error:", e);
            }
        } else {
            System.out.println(USAGE_TEXT);
        }
    }

    private static final void cleanUp() {
        cleaningUp = true;
        try {
            if (engine != null) {
                engine.cleanUp();
            }
            if (logger != null) {
                logger.log(LOG_LEVEL_0, "OTAUploader stopped.");
                logger.close();
            }
        } catch (InterruptedException e) {
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
