/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.13   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppserver;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;

import java.sql.SQLException;

import kz.paysoft.common.logger.Logger;
import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


final class Engine extends Thread {
    private static final String PROTOCOL_VERSION = "1";
    private static final int GENERIC_NACK = 0x80000000;
    private static final int SUBMIT_SM_RESP = 0x80000004;
    private static final int DELIVER_SM = 5;
    private static final int UNBIND_RESP = 0x80000006;
    private static final int ENQUIRE_LINK = 0x15;
    private static final byte DELIVER_SM_MIN_LENGTH = 33;
    private static final byte FINAL_SUBMIT_SM_RESP_WAIT_TIME = 5; // In seconds.
    private final Socket socket;
    private final DataInputStream smppIn;
    private final DataOutputStream smppOut;
    private final String systemId, password, systemType, serviceType, sourceAddr, validityPeriod;
    private final byte ton, npi, esmClass, protocolId, priorityFlag, dataCoding;
    private boolean bound = false;

    Engine(NamedNodeMap parameters) throws UnknownHostException, IOException, PduException, LoggerException {
        String address = parameters.getNamedItem("address").getNodeValue();
        int port = Integer.parseInt(parameters.getNamedItem("port").getNodeValue());
        socket = new Socket(address, port);
        smppIn = new DataInputStream(socket.getInputStream());
        smppOut = new DataOutputStream(socket.getOutputStream());
        systemId = parseParameter(parameters.getNamedItem("systemId"));
        password = parseParameter(parameters.getNamedItem("password"));
        systemType = parseParameter(parameters.getNamedItem("systemType"));
        serviceType = parseParameter(parameters.getNamedItem("serviceType"));
        sourceAddr = parseParameter(parameters.getNamedItem("sourceAddr"));
        validityPeriod = parseParameter(parameters.getNamedItem("validityPeriod"));
        ton = Byte.parseByte(parameters.getNamedItem("ton").getNodeValue());
        npi = Byte.parseByte(parameters.getNamedItem("npi").getNodeValue());
        esmClass = Byte.parseByte(parameters.getNamedItem("esmClass").getNodeValue());
        protocolId = Byte.parseByte(parameters.getNamedItem("protocolId").getNodeValue());
        priorityFlag = Byte.parseByte(parameters.getNamedItem("priorityFlag").getNodeValue());
        dataCoding = Byte.parseByte(parameters.getNamedItem("dataCoding").getNodeValue());

        BindTransceiver msg = new BindTransceiver(systemId, password, systemType, ton, npi, sourceAddr);
        msg.send(smppOut);
        msg.checkResponse(smppIn);
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Connected to the SMS Center.");
        bound = true;
    }

    private final String parseParameter(Node parameter) {
        return parameter == null ? "" : parameter.getNodeValue();
    }

    final void submitSM(String phoneNumber, String message) throws IOException, LoggerException, PduException {
        new SubmitSm(serviceType, ton, npi, sourceAddr, phoneNumber, esmClass, protocolId, priorityFlag, validityPeriod, dataCoding, message).send(smppOut);
        //msg.checkResponse(smppIn);
    }

    final void cleanUp() throws IOException, LoggerException, PduException, InterruptedException {
        if (smppIn != null && smppOut != null) {
            for (int i = 0; i < FINAL_SUBMIT_SM_RESP_WAIT_TIME; i++) { // Give a time to receive a probable final SUBMIT_SM_RESP.
                sleep(1000);
                System.out.print(".");
            }
            new Unbind().send(smppOut);
            //msg.checkResponse(smppIn);
            while (bound) {
                sleep(1000);
                System.out.print(".");
            }
            interrupt();
            smppOut.close();
            smppIn.close();
            socket.close();
            SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Disconnected from the SMS Center.");
        }
    }

    public void run() {
        byte[] buf = null;
        while (true) {
            try {
                buf = Pdu.readToBuffer(smppIn);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
                int status, sequence, lenght = dis.readInt();
                switch (dis.readInt()) {
                case GENERIC_NACK:
                    Logger.logError("Received general nack.");
                    status = dis.readInt();
                    sequence = dis.readInt();
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "General nack received [" + sequence + ":" + status + "]");
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data for [" + sequence + "]:", buf);
                    break;

                case SUBMIT_SM_RESP:
                    status = dis.readInt();
                    sequence = dis.readInt();
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Submit response received [" + sequence + ":" + status + ":" + Pdu.getString(dis) + "].");
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequence + "]:", buf);
                    break;

                case DELIVER_SM:
                    processDeliverSM(lenght, dis, buf);
                    break;

                case UNBIND_RESP:
                    status = dis.readInt();
                    sequence = dis.readInt();
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Unbind response received [" + sequence + ":" + status + "].");
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequence + "]:", buf);
                    bound = false;
                    break;

                case ENQUIRE_LINK:
                    dis.readInt();
                    sequence = dis.readInt();
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Enquire link received [" + sequence + "].");
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequence + "]:", buf);
                    new EnquireLinkResp(sequence).send(smppOut);
                    break;

                default:
                    Logger.logError("Received unsupported command.");
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Unsupported command received.");
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data for unsupported command:", buf);
                    break;
                }
            } catch (PduException e) {
                Logger.logError("SMS processing error:", e);
                try {
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "SMS processing error: " + e.getMessage());
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data on error:", buf);
                } catch (LoggerException f) {
                    Logger.logError("Logging SMS processing error:", f);
                }
            } catch (IOException e) {
                if (!SMPPServer.cleaningUp) {
                    Logger.logError("SMS processing error:", e);
                    try {
                        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "SMS processing error: " + e.getMessage());
                        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data on error:", buf);
                    } catch (LoggerException f) {
                        Logger.logError("Logging SMS processing error:", f);
                    }
                }
            } catch (SQLException e) {
                Logger.logError("SMS processing error:", e);
                try {
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "SMS processing error: " + e.getMessage());
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data on error:", buf);
                } catch (LoggerException f) {
                    Logger.logError("Logging SMS processing error:", f);
                }
            } catch (LoggerException e) {
                Logger.logError("SMS processing error:", e);
                try {
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "SMS processing error: " + e.getMessage());
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data on error:", buf);
                } catch (LoggerException f) {
                    Logger.logError("Logging SMS processing error:", f);
                }
            } catch (Exception e) {
                Logger.logError("SMS processing error:", e);
                try {
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "SMS processing error: " + e.getMessage());
                    SMPPServer.logger.log(SMPPServer.LOG_LEVEL_0, "Data on error:", buf);
                } catch (LoggerException f) {
                    Logger.logError("Logging SMS processing error:", f);
                }
            }
        }
    }

    private final void processDeliverSM(int lenght, DataInputStream dis, byte[] buf) throws PduException, LoggerException, IOException, SQLException {
        if (lenght < DELIVER_SM_MIN_LENGTH) {
            throw new PduException("Incorrect PDU length.");
        }
        dis.readInt();
        int sequence = dis.readInt();
        Pdu.getString(dis);
        dis.readByte();
        dis.readByte();
        String phoneNumber = Pdu.getString(dis);
        dis.readByte();
        dis.readByte();
        Pdu.getString(dis);
        dis.readByte();
        dis.readByte();
        dis.readByte();
        dis.readByte();
        dis.readByte();
        dis.readByte();
        dis.readByte();
        dis.readByte();
        dis.readByte();
        byte messageLenght = dis.readByte();
        StringBuffer message = new StringBuffer();
        for (int i = 0; i < messageLenght; i++) {
            message.append((char)dis.readByte());
        }
        String version = message.substring(0, 1);
        if (!PROTOCOL_VERSION.equals(version)) {
            new DeliverSmResp(sequence).send(smppOut);
            throw new PduException("Incorrect version.");
        }
        long srId = Long.parseLong(message.substring(1));
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_1, "Deliver received [" + sequence + ":" + phoneNumber + ":" + message + "].");
        SMPPServer.logger.log(SMPPServer.LOG_LEVEL_2, "Data for [" + sequence + "]:", buf);
        SMPPServer.processConfirmation(srId, phoneNumber, buf, new DeliverSmResp(sequence).send(smppOut));
    }
}
