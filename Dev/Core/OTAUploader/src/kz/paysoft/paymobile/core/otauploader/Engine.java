/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.18   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.otauploader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import kz.paysoft.common.decryptator.Decryptator;
import kz.paysoft.common.decryptator.DecryptatorException;
import kz.paysoft.common.logger.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


final class Engine {
    private static final boolean DEVELOPMENT = true;
    private static final byte[] JAVAX_NET_DEBUG =
    { -107, -98, -119, -98, -121, -47, -111, -102, -117, -47, -101, -102, -99, -118, -104 }; // javax.net.debug
    private static final byte[] JCECSP_DEBUG = { -75, -68, -70, -68, -84, -81, -96, -69, -70, -67, -86, -72 }; // JCECSP_DEBUG
    private static final byte[] NFJAVA_DEBUG = { -79, -71, -75, -66, -87, -66, -96, -69, -70, -67, -86, -72 }; // NFJAVA_DEBUG
    private static final byte[] COM_NCIPHER_PROVIDER_DISABLE =
    { -100, -112, -110, -47, -111, -100, -106, -113, -105, -102, -115, -47, -113, -115, -112, -119, -106, -101, -102, -115, -47, -101, -106, -116,
      -98, -99, -109, -102 }; // com.ncipher.provider.disable
    private static final byte[] _DEVELOPMENT_ =
    { -43, -43, -43, -43, -43, -43, -43, -33, -69, -33, -70, -33, -87, -33, -70, -33, -77, -33, -80, -33, -81, -33, -78, -33, -70, -33, -79, -33,
      -85, -33, -33, -33, -70, -33, -79, -33, -66, -33, -67, -33, -77, -33, -70, -33, -69, -33, -43, -43, -43, -43, -43, -43, -43 };
    private final SSLServerSocket serverSocket;
    private final String provider;
    private final int closeTime;

    Engine(Node server, Node crypto) throws KeyStoreException, NoSuchProviderException, DecryptatorException, FileNotFoundException, IOException,
                                            NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        if (DEVELOPMENT) {
            System.err.println(Decryptator.toString(_DEVELOPMENT_));
        }
        closeTime = Integer.parseInt(server.getAttributes().getNamedItem("closeTime").getNodeValue());
        NodeList cipherSuitesList = crypto.getChildNodes();
        ArrayList<String> cipherSuites = new ArrayList<String>();
        for (int i = 0; i < cipherSuitesList.getLength(); i++) {
            Node child = cipherSuitesList.item(i);
            if (child.getNodeName().equals("cipherSuite")) {
                cipherSuites.add(child.getAttributes().getNamedItem("name").getNodeValue());
            }
        }
        NamedNodeMap cryptoParams = crypto.getAttributes();
        provider = cryptoParams.getNamedItem("provider").getNodeValue();
        KeyStore ks;
        if (DEVELOPMENT) {
            ks = KeyStore.getInstance(cryptoParams.getNamedItem("storeType").getNodeValue());
        } else {
            if (System.getProperty(Decryptator.toString(JAVAX_NET_DEBUG)) != null) {
                throw new RuntimeException();
            } else if (System.getProperty(Decryptator.toString(JCECSP_DEBUG)) != null) {
                throw new RuntimeException();
            } else if (System.getProperty(Decryptator.toString(NFJAVA_DEBUG)) != null) {
                throw new RuntimeException();
            } else if (System.getProperty(Decryptator.toString(COM_NCIPHER_PROVIDER_DISABLE)) != null) {
                throw new RuntimeException();
            }
            ks = KeyStore.getInstance(cryptoParams.getNamedItem("storeType").getNodeValue(), provider);
        }
        char[] password =
            Decryptator.getPassword(cryptoParams.getNamedItem("x").getNodeValue(), cryptoParams.getNamedItem("a").getNodeValue(), cryptoParams.getNamedItem("b").getNodeValue());
        ks.load(new FileInputStream(cryptoParams.getNamedItem("store").getNodeValue()), password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);
        Decryptator.clearBuffer(password);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, null);
        SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
        serverSocket = (SSLServerSocket)ssf.createServerSocket(Integer.parseInt(server.getAttributes().getNamedItem("port").getNodeValue()));
        if (cipherSuites.size() != 0) {
            serverSocket.setEnabledCipherSuites(cipherSuites.toArray(new String[0]));
        }
    }

    final void cleanUp() throws IOException, InterruptedException {
        System.out.print("Stopping");
        int i = 0;
        while (Thread.activeCount() > 2) {
            Thread.currentThread().sleep(200);
            if (i % 2 == 0) {
                System.out.print(".");
            }
            i++;
        }
        System.out.println();
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    final void run() {
        int connectionId = 0;
        while (!OTAUploader.cleaningUp) {
            if (connectionId < 0) {
                connectionId = 0;
            }
            try {
                SSLSocket sock = (SSLSocket)serverSocket.accept();
                if (OTAUploader.cleaningUp) {
                    sock.close();
                    while (true) {
                        try {
                            Thread.currentThread().sleep(1000000);
                        } catch (InterruptedException e) {
                            Logger.logError("Stopping error:", e);
                        }
                    }
                }
                new Session(this, sock, connectionId++, closeTime).start();
            } catch (IOException e) {
                if (!OTAUploader.cleaningUp) {
                    Logger.logError("New connection establishment error:", e);
                }
            }
        }
        while (true) {
            try {
                Thread.currentThread().sleep(1000000);
            } catch (InterruptedException e) {
                Logger.logError("Stopping error:", e);
            }
        }
    }
}
