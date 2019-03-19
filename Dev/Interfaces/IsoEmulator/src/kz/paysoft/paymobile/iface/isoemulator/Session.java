/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.isoemulator;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

import java.util.Date;


final class Session extends Thread {
    private static final Short PAN = 2;
    private static final Short PC = 3;
    private static final Short AMOUNT = 4;
    private static final Short TRANSMITION_DATETIME = 7;
    private static final Short STAN = 11;
    private static final Short RRN = 37;
    private static final Short APC = 38;
    private static final Short RC = 39;
    private static final Short TERMINAL_ID = 41;
    private static final Short CURRENCY = 49;
    private static final Short ADD_AMOUNTS = 54;
    private final Socket socket;
    static String responseCode;
    static String info;

    Session(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println(new Date() + "| Received a new connection from " + socket.getInetAddress());
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            Message request = Engine.parse(is);
            if (request.type == 0x100 || request.type == 0x200 || request.type == 0x220) {
                Message response = new Message(request.type + 0x10);
                response.setField(PAN, request.getFieldValue(PAN));
                response.setField(PC, request.getFieldValue(PC));
                response.setField(AMOUNT, request.getFieldValue(AMOUNT));
                response.setField(TRANSMITION_DATETIME, request.getFieldValue(TRANSMITION_DATETIME));
                response.setField(STAN, request.getFieldValue(STAN));
                response.setField(RRN, request.getFieldValue(RRN));
                response.setField(APC, "123456");
                response.setField(RC, responseCode);
                response.setField(TERMINAL_ID, request.getFieldValue(TERMINAL_ID));
                response.setField(CURRENCY, request.getFieldValue(CURRENCY));
                if (request.getFieldValue(PC).substring(0, 2).equals("30")) {
                    response.setField(ADD_AMOUNTS, info);
                }
                Engine.write(response, os);
            } else if (request.type == 0x420) {
                Message response = new Message(0x430);
                response.setField(PAN, request.getFieldValue(PAN));
                response.setField(PC, request.getFieldValue(PC));
                response.setField(AMOUNT, request.getFieldValue(AMOUNT));
                response.setField(TRANSMITION_DATETIME, request.getFieldValue(TRANSMITION_DATETIME));
                response.setField(STAN, request.getFieldValue(STAN));
                response.setField(RRN, request.getFieldValue(RRN));
                response.setField(APC, "123456");
                response.setField(RC, responseCode);
                response.setField(TERMINAL_ID, request.getFieldValue(TERMINAL_ID));
                response.setField(CURRENCY, request.getFieldValue(CURRENCY));
                Engine.write(response, os);
            } else {
                throw new Exception("Received unsupported message type: " + String.format("%04X", request.type));
            }
            is.close();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
