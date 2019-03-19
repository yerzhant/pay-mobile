/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.13   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppserver;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kz.paysoft.common.logger.LoggerException;


final class Unbind extends Pdu {
    private static final int UNBIND = 6;
    //    private static final byte RESPONSE_LENGTH = 16;

    Unbind() {
        super(UNBIND);
    }

    //    final void checkResponse(DataInputStream in) throws IOException, PduException, LoggerException {
    //        byte[] buf = readToBuffer(in);
    //        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
    //        if (dis.readInt() != RESPONSE_LENGTH) {
    //            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, buf);
    //            throw new PduException("Incorrect PDU length.");
    //        } else if (dis.readInt() != (UNBIND | RESPONSE_MASK)) {
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
    //        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Unbind response received.");
    //    }

    final byte[] send(DataOutputStream out) throws IOException, LoggerException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        super.send(dos);
        byte[] buf = baos.toByteArray();
        synchronized (out) {
            out.write(buf);
        }
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Unbind sent [" + sequenceNumber + "]");
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequenceNumber + "]:", buf);
        return buf;
    }
}
