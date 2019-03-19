/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.03   Yerzhan Tulepov         Copied from PaySys and amended to comply with XmlInterface.
 * 15.12.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.xmlemulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;


final class XmlEmulator {
    private static final String START_TEXT = "XmlEmulator 1.1.0\nCopyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.\n";
    private static final String USAGE_TEXT = "Usage: XMLServerEmulator <port> <responseCode> <balanseData> <statementData>\n";

    public static void main(String[] args) throws ParserConfigurationException, IOException {
        System.out.println(START_TEXT);
        if (args.length == 4) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(new Date() + "| Received a new connection from " + socket.getInetAddress());
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();
                    while (true) {
                        int len = is.read() << 8 | is.read();
                        if (len == 0xffff) {
                            System.out.println(new Date() + "| Disconnected.");
                            is.close();
                            os.close();
                            socket.close();
                            break;
                        }
                        byte buf[] = new byte[len];
                        is.read(buf);
                        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                        Document requestDoc = builder.parse(bais);
                        Element e = requestDoc.getDocumentElement();
                        String id = e.getAttribute("id");
                        String type = e.getAttribute("type");
                        String date = e.getAttribute("date");
                        String account = e.getAttribute("account");
                        String amount = e.getAttribute("amount");
                        String currency = e.getAttribute("currency");
                        String paymentTypeCode = e.getAttribute("paymentTypeCode");
                        String description = e.getAttribute("description");
                        String statementType = e.getAttribute("statementType");
                        System.out.println(new Date() + "| Received a message: " + e.getTagName() + ", id = " + id + ", type = " + type + ", date = " + date + ", account = " + account +
                                           ", amount = " + amount + ", currency = " + currency + ", paymentTypeCode = " + paymentTypeCode + ", description = " + description + ", statementType = " +
                                           statementType);
                        Document responseDoc = builder.newDocument();
                        Element response = responseDoc.createElement("response");
                        response.setAttribute("id", id);
                        response.setAttribute("type", type);
                        response.setAttribute("code", args[1]);
                        response.setAttribute("date", date);
                        response.setAttribute("account", account);
                        response.setAttribute("amount", amount);
                        response.setAttribute("currency", currency);
                        if (type.equals("Balanse")) {
                            response.setAttribute("data", args[2]);
                        } else if (type.equals("Statement")) {
                            response.setAttribute("data", args[3].replaceAll("\\\\n", "\n"));
                        }
                        responseDoc.appendChild(response);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        transformerFactory.newTransformer().transform(new DOMSource(responseDoc), new StreamResult(baos));
                        len = baos.size();
                        os.write(len >> 8);
                        os.write(len);
                        os.write(baos.toByteArray());
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println(USAGE_TEXT);
        }
    }
}
