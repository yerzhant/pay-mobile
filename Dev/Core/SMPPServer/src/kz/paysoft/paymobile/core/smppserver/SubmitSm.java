/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.14   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kz.paysoft.common.logger.LoggerException;


final class SubmitSm extends Pdu {
    private static final int SUBMIT_SM = 4;
    //    private static final int MIN_RESPONSE_LENGTH = 16;
    private final String serviceType, sourceAddr, destinationAddr, validityPeriod, shortMessage;
    private final byte ton, npi, esmClass, protocolId, priorityFlag, dataCoding;

    SubmitSm(String serviceType, byte ton, byte npi, String sourceAddr, String destinationAddr, byte esmClass, byte protocolId, byte priorityFlag, String validityPeriod, byte dataCoding, String shortMessage) {
        super(SUBMIT_SM);
        this.serviceType = serviceType;
        this.sourceAddr = sourceAddr;
        this.destinationAddr = destinationAddr;
        this.validityPeriod = validityPeriod;
        this.shortMessage = shortMessage;
        this.ton = ton;
        this.npi = npi;
        this.esmClass = esmClass;
        this.protocolId = protocolId;
        this.priorityFlag = priorityFlag;
        this.dataCoding = dataCoding;
        commandLength += serviceType.length() + sourceAddr.length() + destinationAddr.length() + validityPeriod.length() + shortMessage.length() + 17;
    }

    //    final void checkResponse(DataInputStream in) throws IOException, PduException, LoggerException {
    //        byte[] buf = readToBuffer(in);
    //        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
    //        if (dis.readInt() < MIN_RESPONSE_LENGTH) {
    //            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, buf);
    //            throw new PduException("Incorrect PDU length.");
    //        } else if (dis.readInt() != (SUBMIT_SM | RESPONSE_MASK)) {
    //            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, buf);
    //            throw new PduException("Mismatched response.");
    //        } else if (dis.readInt() != 0) {
    //            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, buf);
    //            throw new PduException("Error response code received.");
    //        } else if (dis.readInt() != sequenceNumber) {
    //            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, buf);
    //            throw new PduException("Mismatched sequence.");
    //        }
    //        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, buf);
    //        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Submit response received [" + getString(dis) + "].");
    //    }

    final byte[] send(DataOutputStream out) throws IOException, LoggerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        super.send(dos);
        dos.write(serviceType.getBytes());
        dos.writeByte(0);
        dos.writeByte(ton);
        dos.writeByte(npi);
        dos.write(sourceAddr.getBytes());
        dos.writeByte(0);
        dos.writeByte(ton);
        dos.writeByte(npi);
        dos.write(destinationAddr.getBytes());
        dos.writeByte(0);
        dos.writeByte(esmClass);
        dos.writeByte(protocolId);
        dos.writeByte(priorityFlag);
        dos.writeByte(0);
        dos.write(validityPeriod.getBytes());
        dos.writeByte(0);
        dos.writeByte(0);
        dos.writeByte(0);
        dos.writeByte(dataCoding);
        dos.writeByte(0);
        dos.writeByte(shortMessage.length());
        dos.write(shortMessage.getBytes());
        byte[] buf = baos.toByteArray();
        synchronized (out) {
            out.write(buf);
        }
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Submit sent [" + sequenceNumber + ":" + destinationAddr + ":" + shortMessage + "].");
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequenceNumber + "]:", buf);
        return buf;
    }
}
