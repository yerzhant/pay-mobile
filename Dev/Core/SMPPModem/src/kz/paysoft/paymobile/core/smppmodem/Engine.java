/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.22   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppmodem;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Node;


final class Engine {
    private static final String SYSTEM_ID = "SMPPModem";
    private static final String DELIVER_NOTIFICATION = "\\+CMTI: (\"SM\"|\"ME\"|\"MT\"|\"TA\"),\\d*";
    private static final String READ_MESSAGE_COMMAND = "AT+CMGR=";
    private static final String READ_NOTIFICATION = "\\+CMGR: \"\\D*\",\"\\+\\d*\",.*,.*";
    private static final int RESPONSE_MASK = 0x80000000;
    private static final int SUBMIT_SM = 4;
    private static final int DELIVER_SM = 5;
    private static final int UNBIND = 6;
    private static final int BIND_TRANSCEIVER = 9;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static int receiveSequence, sendSequence;
    private static byte[] buffer;

    static final void run(Node server) throws IOException, LoggerException, EngineException {
        serverSocket = new ServerSocket(Integer.parseInt(server.getAttributes().getNamedItem("port").getNodeValue()));
        while (true) {
            socket = serverSocket.accept();
            SMPPModem.logger.log(SMPPModem.LOG_LEVEL_0, "SMPPServer connected.");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            sendSequence = 1;
            if (getMessage() != BIND_TRANSCEIVER) {
                throw new EngineException("Incorrect SMPP initialization.");
            }
            sendBindResponse();

            boolean bound = true;
            while (bound && !SMPPModem.cleaningUp) {
                int command = getMessage();
                switch (command) {
                case UNBIND:
                    sendUnbindResponse();
                    bound = false;
                    break;

                case SUBMIT_SM:
                    try {
                        new SubmitSm(receiveSequence, buffer).start();
                    } catch (SubmitSmException e) {
                        Logger.logError("Submit init error:", e);
                    }
                    break;

                case DELIVER_SM | RESPONSE_MASK:
                    break;

                default:
                    throw new EngineException("Unsupported command: " + command);
                }
            }
            out.close();
            in.close();
            socket.close();
            SMPPModem.logger.log(SMPPModem.LOG_LEVEL_0, "SMPPServer disconnected.");
        }
    }

    static final void processModemIn(String string) throws LoggerException, IOException {
        if (string.matches(DELIVER_NOTIFICATION)) {
            String index = string.substring(string.indexOf(",") + 1);
            synchronized (SMPPModem.modemOut) {
                SMPPModem.sendString(READ_MESSAGE_COMMAND + index);
            }
        } else if (string.matches(READ_NOTIFICATION)) {
            String originatingAddress = string.substring(string.indexOf(",") + 3, string.indexOf(",", string.indexOf(",") + 2) - 1);
            String message = SMPPModem.readString();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(DELIVER_SM);
            dos.writeInt(0);
            dos.writeInt(sendSequence++);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.write(originatingAddress.getBytes());
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(0);
            dos.writeByte(message.length());
            dos.write(message.getBytes());
            synchronized (out) {
                out.writeInt(4 + baos.size());
                out.write(baos.toByteArray());
            }
            baos.close();
            dos.close();
            if (sendSequence == 0x80000000) {
                sendSequence = 1;
            }
        }
    }

    private static int getMessage() throws IOException, LoggerException {
        int len = in.readInt();
        int command = in.readInt();
        int status = in.readInt();
        receiveSequence = in.readInt();
        SMPPModem.logger.log(SMPPModem.LOG_LEVEL_2, String.format("Received the message [" + receiveSequence + "]: %08X %08X %08X %08X", len, command, status, receiveSequence));
        buffer = new byte[len - 16];
        in.readFully(buffer);
        SMPPModem.logger.log(SMPPModem.LOG_LEVEL_2, "Received data [" + receiveSequence + "]:", buffer);
        return command;
    }

    private static void sendBindResponse() throws IOException {
        out.writeInt(16 + SYSTEM_ID.length() + 1);
        out.writeInt(BIND_TRANSCEIVER | RESPONSE_MASK);
        out.writeInt(0);
        out.writeInt(receiveSequence);
        out.write(SYSTEM_ID.getBytes());
        out.writeByte(0);
    }

    private static void sendUnbindResponse() throws IOException {
        out.writeInt(16);
        out.writeInt(UNBIND | RESPONSE_MASK);
        out.writeInt(0);
        out.writeInt(receiveSequence);
    }

    static final void cleanUp() throws IOException {
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
        if (socket != null) {
            socket.close();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
