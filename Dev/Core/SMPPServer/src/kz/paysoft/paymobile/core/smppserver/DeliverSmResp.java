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


final class DeliverSmResp extends Pdu {
    private static final int DELIVER_SM_RESP = 0x80000005;

    DeliverSmResp(int sequence) {
        super(DELIVER_SM_RESP, 0);
        sequenceNumber = sequence;
        commandLength++;
    }

    final byte[] send(DataOutputStream out) throws IOException, LoggerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        super.send(dos);
        dos.writeByte(0);
        byte[] buf = baos.toByteArray();
        synchronized (out) {
            out.write(buf);
        }
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Deliver response sent [" + sequenceNumber + "].");
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequenceNumber + "]:", buf);
        return buf;
    }
}
