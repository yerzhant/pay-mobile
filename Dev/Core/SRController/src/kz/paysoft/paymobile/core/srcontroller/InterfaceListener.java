/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.25   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.srcontroller;

import java.io.IOException;

import java.net.ServerSocket;

import kz.paysoft.common.logger.Logger;


final class InterfaceListener extends Thread {
    private final ServerSocket socket;

    InterfaceListener(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    public void run() {
        while (true) {
            try {
                new Interface(socket.accept()).start();
            } catch (IOException e) {
                Logger.logError("Interface creation error:", e);
            }
        }
    }
}
