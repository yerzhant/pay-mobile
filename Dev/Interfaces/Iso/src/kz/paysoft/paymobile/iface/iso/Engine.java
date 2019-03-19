/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.01   Yerzhan Tulepov         Hugely revised from PaySys.
 * 26.10.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.iso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.math.BigInteger;

import java.net.Socket;
import java.net.UnknownHostException;

import java.sql.CallableStatement;
import java.sql.SQLException;

import java.util.Date;
import java.util.HashMap;

import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


final class Engine {
    private static final HashMap<Short, Value> FIELD_DEFENITIONS = new HashMap<Short, Value>();
    private static final HashMap<String, Message> MESSAGE_TEMPLATES = new HashMap<String, Message>();
    private static final HashMap<String, CurrencyInfo> SYSTEM_CURRENCY_MAPPING = new HashMap<String, CurrencyInfo>();
    private static final Short PAN = 2;
    private static final Short AMOUNT = 4;
    private static final Short TRANSMITION_DATETIME = 7;
    private static final Short STAN = 11;
    private static final Short TIME = 12;
    private static final Short DATE = 13;
    private static final Short RRN = 37;
    private static final Short RC = 39;
    private static final Short CURRENCY = 49;
    private static String address;
    private static int port;
    private static byte lengthSize;
    static final HashMap<String, Short> RC_MAPPING = new HashMap<String, Short>();
    static final HashMap<String, CurrencyInfo> ISO_CURRENCY_MAPPING = new HashMap<String, CurrencyInfo>();
    static final byte SYSTEM_POINT_POSITION = 2;

    static final Socket send(long id, CallableStatement stmt) throws SQLException, EngineException, UnknownHostException, IOException, LoggerException {
        byte[] message = create(id, stmt);
        Iso.logger.log(Iso.LOG_LEVEL_9, "Connecting [" + id + "].");
        Socket socket = new Socket(address, port);
        Iso.logger.log(Iso.LOG_LEVEL_9, "Connected [" + id + "].");
        int size = message.length;
        if (lengthSize == 2) {
            if (size > 65535) {
                socket.close();
                throw new NumberFormatException("The length of the message is greater than 65535.");
            }
        } else if (lengthSize == 4) {
            socket.getOutputStream().write(size >> 24);
            socket.getOutputStream().write(size >> 16);
        } else {
            socket.close();
            throw new NumberFormatException("Unsupported lengthSize [" + lengthSize + "].");
        }
        socket.getOutputStream().write(size >> 8);
        socket.getOutputStream().write(size);
        socket.getOutputStream().write(message);
        socket.getOutputStream().flush();
        stmt.getBlob("p_data").setBytes(1, message);
        Iso.logger.log(Iso.LOG_LEVEL_1, "Sent [" + id + ":" + stmt.getString("p_code") + ":" + stmt.getString("p_account") + "].");
        Iso.logger.log(Iso.LOG_LEVEL_2, "Sent " + size + " bytes [" + id + "]:", message);
        return socket;
    }

    private static final byte[] create(long id, CallableStatement stmt) throws SQLException, EngineException, UnknownHostException, IOException, LoggerException {
        String code = stmt.getString("p_code");
        Message message = MESSAGE_TEMPLATES.get(code).clone();
        if (message != null) {
            String currency = stmt.getString("p_currency");
            if (currency != null) {
                CurrencyInfo currInfo = SYSTEM_CURRENCY_MAPPING.get(currency);
                long amount = convertAmount(stmt.getLong("p_amount"), SYSTEM_POINT_POSITION, currInfo.isoPointPos);
                message.setField(AMOUNT, Long.toString(amount));
                message.setField(CURRENCY, currInfo.currency);
            }
            String account = stmt.getString("p_account");
            message.setField(PAN, account);
            message.setField(TRANSMITION_DATETIME, String.format("%1$tm%1$td%1$tH%1$tM%1$tS", new Date()));
            message.setField(STAN, Long.toString(id % 1000000));
            message.setField(TIME, stmt.getString("p_date").substring(8));
            message.setField(DATE, stmt.getString("p_date").substring(4, 8));
            message.setField(RRN, String.format("%012d", id));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            message.write(baos);
            byte[] buf = baos.toByteArray();
            baos.close();
            return buf;
        } else {
            throw new EngineException("Undefined message for [" + code + "].");
        }
    }

    static final long convertAmount(long amount, byte from, byte to) throws EngineException {
        if (from == to) {
            return amount;
        } else if (from > to) {
            long k = new BigInteger("10").pow(from - to).longValue();
            long newAmount = amount / k;
            if ((amount - newAmount * k) != 0) {
                throw new EngineException("The amount is unconvertable.");
            }
            return newAmount;
        } else {
            return amount * new BigInteger("10").pow(to - from).longValue();
        }
    }

    static final Value createField(Short number, String value) {
        return FIELD_DEFENITIONS.get(number).clone(value);
    }

    static final Value parseField(Short number, InputStream stream) throws IOException {
        return FIELD_DEFENITIONS.get(number).parse(stream);
    }

    static final Message receive(long id, InputStream stream, ProcessSR processSR) throws ParseException, IOException, LoggerException, EngineException {
        int size;
        if (lengthSize == 2) {
            size = stream.read() << 8 | stream.read();
        } else if (lengthSize == 4) {
            size = stream.read() << 24 | stream.read() << 16 | stream.read() << 8 | stream.read();
        } else {
            throw new NumberFormatException("Unsupported lengthSize [" + lengthSize + "].");
        }
        processSR.data = new byte[size];
        if (stream.read(processSR.data) != size) {
            throw new ParseException("Stream was broken.");
        }
        Iso.logger.log(Iso.LOG_LEVEL_2, "Received " + size + " bytes [" + id + "]:", processSR.data);
        ByteArrayInputStream bais = new ByteArrayInputStream(processSR.data);
        int type = bais.read() << 8 | bais.read();
        Message message = new Message(type);
        message.parse(bais);
        if (bais.available() != 0) {
            bais.close();
            throw new ParseException("The packet's length does not correspond to that of the length field.");
        }
        bais.close();
        long rrn = Long.parseLong(message.getFieldValue(RRN));
        if (rrn != id) {
            throw new EngineException("The response doesn't match to the request [" + rrn + "].");
        }
        Iso.logger.log(Iso.LOG_LEVEL_1, "Received [" + id + ":" + message.getFieldValue(PAN) + ":" + message.getFieldValue(RC) + "].");
        return message;
    }

    static final void setupConfiguration(Element config) {
        NamedNodeMap attrs = config.getAttributes();
        address = attrs.getNamedItem("address").getNodeValue();
        port = Integer.parseInt(attrs.getNamedItem("port").getNodeValue());
        lengthSize = Byte.parseByte(attrs.getNamedItem("lengthSize").getNodeValue());
        setupFieldDefenitions(config);
        setupMessageTemplates(config);
        setupRcMapping(config);
        setupCurrencyMapping(config);
        ProcessSR.setupProcessSR((Element)config.getElementsByTagName("balanse").item(0));
    }

    private static final void setupFieldDefenitions(Element config) {
        NodeList fieldDefs = config.getElementsByTagName("fieldDef");
        for (short f = 0; f < fieldDefs.getLength(); f++) {
            NamedNodeMap attributes = fieldDefs.item(f).getAttributes();
            Short number = Short.parseShort(attributes.getNamedItem("number").getNodeValue());
            String lenType = attributes.getNamedItem("lengthType").getNodeValue();
            String valType = attributes.getNamedItem("valueType").getNodeValue();
            short maxLength = Short.parseShort(attributes.getNamedItem("maxLength").getNodeValue());
            Value.LengthType lengthType;
            if (lenType.equals("FIXED")) {
                lengthType = Value.LengthType.FIXED;
            } else if (lenType.equals("LLVAR")) {
                lengthType = Value.LengthType.LLVAR;
            } else if (lenType.equals("LLLVAR")) {
                lengthType = Value.LengthType.LLLVAR;
            } else if (lenType.equals("BBVAR")) {
                lengthType = Value.LengthType.BBVAR;
            } else {
                throw new IllegalArgumentException("Unsupported lengthType [" + lenType + "].");
            }
            Value.ValueType valueType;
            if (valType.equals("BCD")) {
                valueType = Value.ValueType.BCD;
            } else if (valType.equals("ANS")) {
                valueType = Value.ValueType.ANS;
            } else if (valType.equals("HEX")) {
                valueType = Value.ValueType.HEX;
            } else {
                throw new IllegalArgumentException("Unsupported valueType [" + valType + "].");
            }
            FIELD_DEFENITIONS.put(number, new Value(lengthType, valueType, maxLength, null));
        }
    }

    private static final void setupMessageTemplates(Element config) {
        NodeList templates = config.getElementsByTagName("message");
        for (int t = 0; t < templates.getLength(); t++) {
            Element template = (Element)templates.item(t);
            int type = Integer.parseInt(template.getAttributes().getNamedItem("type").getNodeValue(), 16);
            Message message = new Message(type);
            NodeList fields = template.getElementsByTagName("field");
            for (byte f = 0; f < fields.getLength(); f++) {
                NamedNodeMap attributes = fields.item(f).getAttributes();
                Short number = Short.parseShort(attributes.getNamedItem("number").getNodeValue());
                String value = attributes.getNamedItem("value").getNodeValue();
                message.setField(number, FIELD_DEFENITIONS.get(number).clone(value));
            }
            MESSAGE_TEMPLATES.put(template.getAttribute("code"), message);
        }
    }

    private static void setupRcMapping(Element config) {
        NodeList mapping = config.getElementsByTagName("rcMapping");
        for (int m = 0; m < mapping.getLength(); m++) {
            NamedNodeMap attrs = mapping.item(m).getAttributes();
            String iso = attrs.getNamedItem("iso").getNodeValue();
            Short system = Short.parseShort(attrs.getNamedItem("system").getNodeValue());
            RC_MAPPING.put(iso, system);
        }
    }

    private static void setupCurrencyMapping(Element config) {
        NodeList mapping = config.getElementsByTagName("currencyMapping");
        for (int m = 0; m < mapping.getLength(); m++) {
            NamedNodeMap attrs = mapping.item(m).getAttributes();
            String system = attrs.getNamedItem("system").getNodeValue();
            String iso = attrs.getNamedItem("iso").getNodeValue();
            byte isoPointPos = Byte.parseByte(attrs.getNamedItem("isoPointPos").getNodeValue());
            SYSTEM_CURRENCY_MAPPING.put(system, new CurrencyInfo(iso, isoPointPos));
            ISO_CURRENCY_MAPPING.put(iso, new CurrencyInfo(system, isoPointPos));
        }
    }

    static final class CurrencyInfo {
        final String currency;
        final byte isoPointPos;

        CurrencyInfo(String currency, byte isoPointPos) {
            this.currency = currency;
            this.isoPointPos = isoPointPos;
        }
    }
}
