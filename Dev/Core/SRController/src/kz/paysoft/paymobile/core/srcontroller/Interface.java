/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.25   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.srcontroller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class Interface extends Thread {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String code;

    Interface(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    final void send(long id, byte status, boolean cancel) throws IOException, LoggerException {
        SRController.logger.log(SRController.LOG_LEVEL_9, "Interface stage 1.");
        synchronized (out) {
            SRController.logger.log(SRController.LOG_LEVEL_9, "Interface stage 2.");
            out.writeLong(id);
            out.writeByte(status);
            out.writeBoolean(cancel);
        }
        SRController.logger.log(SRController.LOG_LEVEL_1, "Interface [" + code + "] sent [" + id + "].");
    }

    public void run() {
        try {
            code = in.readUTF();
            SRController.logger.log(SRController.LOG_LEVEL_0, "Interface [" + code + "] connected.");
            SRController.interfaces.put(code, this);
            while (true) {
                long id = in.readLong();
                if (id == -1) {
                    break;
                }
                boolean cleanUp = in.readBoolean();
                SRController.logger.log(SRController.LOG_LEVEL_1, "Interface [" + code + "] received [" + id + "].");
                SRController.enqueue(id, cleanUp);
            }
        } catch (InterruptedException e) {
            Logger.logError("Interface [" + code + "] int error:", e);
        } catch (IOException e) {
            Logger.logError("Interface [" + code + "] io error:", e);
        } catch (LoggerException e) {
            Logger.logError("Interface [" + code + "] log error:", e);
        } finally {
            SRController.interfaces.remove(code, this);
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.logError("Interface [" + code + "] in closing error:", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Logger.logError("Interface [" + code + "] out closing error:", e);
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                    SRController.logger.log(SRController.LOG_LEVEL_0, "Interface [" + code + "] disconnected.");
                } catch (IOException e) {
                    Logger.logError("Interface [" + code + "] socket closing error:", e);
                } catch (LoggerException e) {
                    Logger.logError("Interface [" + code + "] socket closing log error:", e);
                }
            }
        }
    }
}
