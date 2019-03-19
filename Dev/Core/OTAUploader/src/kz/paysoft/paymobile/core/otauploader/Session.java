/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.18   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.otauploader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import javax.net.ssl.SSLSocket;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class Session extends Thread {
    private static final String METHOD_GET = "GET";
    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private static final String JAD_TEMPLATE = ".*/PayMobile\\.jad";
    private static final String JAR_TEMPLATE = ".*/PayMobile\\.jar";
    private static final String RESPONSE_OK = "HTTP/1.1 200 OK\r\n";
    private static final String RESPONSE_SERVER = "Server: PayMobile OTAUploader/1.0.0\r\n";
    private static final String RESPONSE_CONTENT_LENGTH = "Content-Length: ";
    private static final String RESPONSE_CONTENT_TYPE_JAD = "Content-Type: text/vnd.sun.j2me.app-descriptor; charset=utf-8\r\n";
    private static final String RESPONSE_CONTENT_TYPE_JAR = "Content-Type: application/java-archive\r\n";
    private static final String RESPONSE_CONTENT_TYPE_HTML = "Content-Type: text/html; charset=utf-8\r\n";
    private static final String INDEX_HTML = "index.html";
    private static final String PAY_MOBILE_JAD = "PayMobile.jad";
    private static final String PAY_MOBILE_JAR = "PayMobile.jar";
    private final Engine engine;
    private final SSLSocket socket;
    private final int connectionId;
    private final int closeTime;

    Session(Engine engine, SSLSocket socket, int connectionId, int closeTime) { // Not to delay accepting new connections, the constructor must exit asap.
        this.engine = engine;
        this.socket = socket;
        this.connectionId = connectionId;
        this.closeTime = closeTime;
    }

    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            OTAUploader.logger.log(OTAUploader.LOG_LEVEL_1,
                                   "Connected [" + connectionId + ":" + socket.getInetAddress() + ":" + socket.getSession().getProtocol() + ":" + socket.getSession().getCipherSuite() + "].");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            process(in, out);
        } catch (SessionException e) {
            Logger.logError("Session error:", e);
        } catch (IOException e) {
            Logger.logError("Session error:", e);
        } catch (LoggerException e) {
            Logger.logError("Session error:", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                socket.close();
                OTAUploader.logger.log(OTAUploader.LOG_LEVEL_1, "Disconnected [" + connectionId + "].");
            } catch (LoggerException e) {
                Logger.logError("Session error:", e);
            } catch (IOException e) {
                Logger.logError("Session error:", e);
            }
        }
    }

    private final void process(DataInputStream in, DataOutputStream out) throws IOException, SessionException, LoggerException {
        ArrayList<Byte> request = new ArrayList<Byte>();
        for (; ; ) {
            byte b = in.readByte();
            request.add(b);
            if (b == 13) {
                if (in.readByte() == 10) {
                    b = in.readByte();
                    request.add((byte)10);
                    request.add(b);
                    if (b == 13) {
                        if (in.readByte() == 10) {
                            request.add((byte)10);
                            break;
                        } else {
                            throw new SessionException("HTTP/1.1 violation.");
                        }
                    }
                } else {
                    throw new SessionException("HTTP/1.1 violation.");
                }
            }
        }
        byte[] buf = new byte[request.size()];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = request.get(i);
        }
        OTAUploader.logger.log(OTAUploader.LOG_LEVEL_2, "Received data [" + connectionId + "]:", buf);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf)));
        String req = reader.readLine();
        if (!req.startsWith(METHOD_GET + " ") || !req.endsWith(" " + HTTP_VERSION_1_1)) {
            throw new SessionException("Incorrect request.");
        }

        File file;
        out.writeBytes(RESPONSE_OK);
        out.writeBytes(RESPONSE_SERVER);
        if (req.matches(METHOD_GET + " " + JAD_TEMPLATE + " " + HTTP_VERSION_1_1)) {
            file = new File(PAY_MOBILE_JAD);
            out.writeBytes(RESPONSE_CONTENT_LENGTH + file.length() + "\r\n");
            out.writeBytes(RESPONSE_CONTENT_TYPE_JAD);
        } else if (req.matches(METHOD_GET + " " + JAR_TEMPLATE + " " + HTTP_VERSION_1_1)) {
            file = new File(PAY_MOBILE_JAR);
            out.writeBytes(RESPONSE_CONTENT_LENGTH + file.length() + "\r\n");
            out.writeBytes(RESPONSE_CONTENT_TYPE_JAR);
        } else {
            file = new File(INDEX_HTML);
            out.writeBytes(RESPONSE_CONTENT_LENGTH + file.length() + "\r\n");
            out.writeBytes(RESPONSE_CONTENT_TYPE_HTML);
        }
        out.writeBytes("\r\n");
        out.flush();
        DataInputStream fileIn = new DataInputStream(new FileInputStream(file));
        buf = new byte[(int)file.length()];
        fileIn.readFully(buf);
        out.write(buf);
        out.flush();
        fileIn.close();
        try {
            sleep(closeTime * 1000); // Wait until all data reach the client, as FIN packet may come before the last data's packet.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
