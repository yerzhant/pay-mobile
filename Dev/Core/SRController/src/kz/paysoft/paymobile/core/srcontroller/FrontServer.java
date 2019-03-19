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


final class FrontServer extends Thread {
    private final ServerSocket serverSocket;
    private static DataOutputStream out;

    FrontServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    static final void send(long id) throws IOException, LoggerException {
        SRController.logger.log(SRController.LOG_LEVEL_9, "FrontServer stage 1.");
        synchronized (out) {
            SRController.logger.log(SRController.LOG_LEVEL_9, "FrontServer stage 2.");
            out.writeLong(id);
        }
        SRController.logger.log(SRController.LOG_LEVEL_1, "FrontServer sent [" + id + "].");
    }

    public void run() {
        while (true) {
            Socket sock = null;
            DataInputStream in = null;
            try {
                sock = serverSocket.accept();
                SRController.logger.log(SRController.LOG_LEVEL_0, "FrontServer connected.");
                in = new DataInputStream(sock.getInputStream());
                out = new DataOutputStream(sock.getOutputStream());
                long id = in.readLong();
                if (id != -1) {
                    Logger.logError("FrontServer id error [" + id + "].");
                }
            } catch (IOException e) {
                Logger.logError("FrontServer io error:", e);
            } catch (LoggerException e) {
                Logger.logError("FrontServer log error:", e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Logger.logError("FrontServer in closing error:", e);
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Logger.logError("FrontServer out closing error:", e);
                    }
                }
                if (sock != null) {
                    try {
                        sock.close();
                        SRController.logger.log(SRController.LOG_LEVEL_0, "FrontServer disconnected.");
                    } catch (IOException e) {
                        Logger.logError("FrontServer socket closing error:", e);
                    } catch (LoggerException e) {
                        Logger.logError("FrontServer socket closing log error:", e);
                    }
                }
            }
        }
    }
}
