/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.13   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kz.paysoft.common.logger.LoggerException;


abstract class Pdu {
    private static final byte[] NULL = null;
    private static int sequence = 1;
    private int commandId, commandStatus;
    static final int RESPONSE_MASK = 0x80000000;
    int commandLength = 16, sequenceNumber;

    Pdu(int commandId) {
        this.commandId = commandId;
        if (sequence == 0x80000000) {
            sequence = 1;
        }
        sequenceNumber = sequence++;
    }

    Pdu(int commandId, int commandStatus) {
        this.commandId = commandId;
        this.commandStatus = commandStatus;
    }

    byte[] send(DataOutputStream out) throws IOException, LoggerException {
        out.writeInt(commandLength);
        out.writeInt(commandId);
        out.writeInt(commandStatus);
        out.writeInt(sequenceNumber);
        return NULL;
    }

    static final byte[] readToBuffer(DataInputStream in) throws IOException {
        byte[] buf = new byte[in.readInt()];
        buf[0] = (byte)(buf.length >> 24);
        buf[1] = (byte)(buf.length >> 16);
        buf[2] = (byte)(buf.length >> 8);
        buf[3] = (byte)buf.length;
        in.readFully(buf, 4, buf.length - 4);
        return buf;
    }

    static final String getString(DataInputStream in) throws IOException, PduException {
        StringBuffer string = new StringBuffer();
        while (in.available() != 0) {
            byte c = in.readByte();
            if (c == 0) {
                return string.toString();
            }
            string.append((char)c);
        }
        throw new PduException("End of string not found.");
    }
}
