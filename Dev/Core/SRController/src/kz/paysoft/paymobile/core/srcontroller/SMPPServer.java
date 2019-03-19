/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.25   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.srcontroller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class SMPPServer extends Thread {
    private final ServerSocket serverSocket;
    private static DataOutputStream out;
    static volatile boolean connected = false;

    SMPPServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    static final void send(long id) throws IOException, LoggerException {
        SRController.logger.log(SRController.LOG_LEVEL_9, "SMPPServer stage 1.");
        synchronized (out) {
            SRController.logger.log(SRController.LOG_LEVEL_9, "SMPPServer stage 2.");
            out.writeLong(id);
        }
        SRController.logger.log(SRController.LOG_LEVEL_1, "SMPPServer sent [" + id + "].");
    }

    public void run() {
        while (true) {
            Socket sock = null;
            DataInputStream in = null;
            try {
                sock = serverSocket.accept();
                SRController.logger.log(SRController.LOG_LEVEL_0, "SMPPServer connected.");
                in = new DataInputStream(sock.getInputStream());
                out = new DataOutputStream(sock.getOutputStream());
                connected = true;
                while (true) {
                    long id = in.readLong();
                    if (id == -1) {
                        break;
                    }
                    SRController.logger.log(SRController.LOG_LEVEL_1, "SMPPServer received [" + id + "].");
                    SRController.enqueue(id, false);
                }
            } catch (InterruptedException e) {
                Logger.logError("SMPPServer int error:", e);
            } catch (IOException e) {
                Logger.logError("SMPPServer io error:", e);
            } catch (LoggerException e) {
                Logger.logError("SMPPServer log error:", e);
            } finally {
                connected = false;
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Logger.logError("SMPPServer in closing error:", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Logger.logError("SMPPServer out closing error:", e);
                    }
                }
                if (sock != null)
                    try {
                        sock.close();
                        SRController.logger.log(SRController.LOG_LEVEL_0, "SMPPServer disconnected.");
                    } catch (IOException e) {
                        Logger.logError("SMPPServer socket closing error:", e);
                    } catch (LoggerException e) {
                        Logger.logError("SMPPServer socket closing log error:", e);
                    }
            }
        }
    }
}
