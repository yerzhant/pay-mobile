/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.29   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppmodem;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;


final class SubmitSm extends Thread {
    private static final String SEND_MESSAGE_COMMAND = "AT+CMGS=";
    private static final char COTROL_Z = 26;
    private final StringBuffer message = new StringBuffer();
    private final String destinationAddress;
    private final int receiveSequence;

    SubmitSm(int receiveSequence, byte[] buffer) throws IOException, SubmitSmException {
        this.receiveSequence = receiveSequence;
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStream in = new DataInputStream(bais);
        getString(in);
        in.readByte();
        in.readByte();
        getString(in);
        in.readByte();
        in.readByte();
        destinationAddress = "\"+" + getString(in) + "\"";
        in.readByte();
        in.readByte();
        in.readByte();
        getString(in);
        getString(in);
        in.readByte();
        in.readByte();
        in.readByte();
        in.readByte();
        int len = in.readUnsignedByte();
        for (int i = 0; i < len; i++) {
            message.append((char)in.readByte());
        }
    }

    public void run() {
        synchronized (SMPPModem.modemOut) {
            try {
                SMPPModem.sendString(SEND_MESSAGE_COMMAND + destinationAddress);
                SMPPModem.sendString(message.toString() + COTROL_Z);
                SMPPModem.logger.log(SMPPModem.LOG_LEVEL_1, "Sent the message [" + receiveSequence + ", " + destinationAddress + "].");
            } catch (LoggerException e) {
                Logger.logError("Submit error:", e);
            }
        }
    }

    private final String getString(DataInputStream in) throws IOException, SubmitSmException {
        StringBuffer string = new StringBuffer();
        while (in.available() != 0) {
            byte c = in.readByte();
            if (c == 0) {
                return string.toString();
            }
            string.append((char)c);
        }
        throw new SubmitSmException("End of string not found.");
    }
}
