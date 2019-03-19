/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Copied from PaySys and slightly modified.
 * 15.12.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.isoemulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.ServerSocket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kz.paysoft.common.logger.Logger;

import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;


public final class IsoEmulator {
    private static final String START_TEXT = "IsoEmulator 1.0.0\nCopyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.\n";
    private static final String USAGE_TEXT = "Usage: IsoEmulator <port> <showRequest> <responseCode> <info>\n";
    static Logger logger;

    public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, LoggerException {
        System.out.println(START_TEXT);
        if (args.length == 4) {
            logger = new Logger("0", "emulator.log", "102400");
            setupIsoConfig();
            Engine.showRequest = args[1].toUpperCase().equals("YES") ? true : false;
            Session.responseCode = args[2];
            Session.info = args[3];
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                new Session(serverSocket.accept()).start();
            }
        } else {
            System.out.println(USAGE_TEXT);
        }
    }

    private static final void setupIsoConfig() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        docFactory.setValidating(true);
        docFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        docFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", new FileInputStream("Configuration.xsd"));
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        Document configuration = builder.parse("Configuration.xml");
        Element root = configuration.getDocumentElement();
        Engine.setupConfiguration((Element)root.getElementsByTagName("isoSystem").item(0));
    }
}
