/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.13   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kz.paysoft.common.logger.LoggerException;


final class BindTransceiver extends Pdu {
    private static final int BIND_TRANCEIVER = 9;
    private static final byte VERSION = 0x34;
    private static final byte MIN_RESPONSE_LENGTH = 17;
    private final String systemId, password, systemType, sourceAddr;
    private final byte ton, npi;

    BindTransceiver(String systemId, String password, String systemType, byte ton, byte npi, String sourceAddr) {
        super(BIND_TRANCEIVER);
        this.systemId = systemId;
        this.password = password;
        this.systemType = systemType;
        this.sourceAddr = sourceAddr;
        this.ton = ton;
        this.npi = npi;
        commandLength += systemId.length() + password.length() + systemType.length() + sourceAddr.length() + 7;
    }

    final void checkResponse(DataInputStream in) throws IOException, PduException, LoggerException {
        byte[] buf = readToBuffer(in);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
        if (dis.readInt() < MIN_RESPONSE_LENGTH) {
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Incorrect PDU length.");
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data for [" + sequenceNumber + "]:", buf);
            throw new PduException("Incorrect PDU length.");
        } else if (dis.readInt() != (BIND_TRANCEIVER | RESPONSE_MASK)) {
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Mismatched response.");
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data for [" + sequenceNumber + "]:", buf);
            throw new PduException("Mismatched response.");
        } else if (dis.readInt() != 0) {
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Error response code received.");
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data for [" + sequenceNumber + "]:", buf);
            throw new PduException("Error response code received.");
        } else if (dis.readInt() != sequenceNumber) {
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Mismatched sequence.");
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data for [" + sequenceNumber + "]:", buf);
            throw new PduException("Mismatched sequence.");
        }
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Bind response received from [" + getString(dis) + "].");
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequenceNumber + "]:", buf);
    }

    final byte[] send(DataOutputStream out) throws IOException, LoggerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        super.send(dos);
        dos.write(systemId.getBytes());
        dos.writeByte(0);
        dos.write(password.getBytes());
        dos.writeByte(0);
        dos.write(systemType.getBytes());
        dos.writeByte(0);
        dos.writeByte(VERSION);
        dos.writeByte(ton);
        dos.writeByte(npi);
        dos.write(sourceAddr.getBytes());
        dos.writeByte(0);
        byte[] buf = baos.toByteArray();
        synchronized (out) {
            out.write(buf);
        }
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Bind sent.");
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequenceNumber + "]:", buf);
        return buf;
    }
}
