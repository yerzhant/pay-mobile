/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Copied from kz.paysoft.paymobile.iface.iso and removed unneeded things.
 * 26.10.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.isoemulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;

import kz.paysoft.common.logger.LoggerException;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


final class Engine {
    private static final HashMap<Short, Value> FIELD_DEFENITIONS = new HashMap<Short, Value>();
    private static byte lengthSize;
    static boolean showRequest;

    static final Value createField(Short number, String value) {
        return FIELD_DEFENITIONS.get(number).clone(value);
    }

    static final Value parseField(Short number, InputStream stream) throws IOException {
        if (showRequest) {
            System.out.format("DE %03d=", number);
        }
        return FIELD_DEFENITIONS.get(number).parse(stream);
    }

    static final void write(Message message, OutputStream stream) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        message.write(byteArray);
        int size = byteArray.size();
        if (lengthSize == 2) {
            if (size > 65535) {
                throw new NumberFormatException("The length of the message is greater than 65535.");
            }
        } else if (lengthSize == 4) {
            stream.write(size >> 24);
            stream.write(size >> 16);
        } else {
            throw new NumberFormatException("Unsupported lengthSize [" + lengthSize + "].");
        }
        stream.write(size >> 8);
        stream.write(size);
        byteArray.writeTo(stream);
        byteArray.close();
    }

    static final Message parse(InputStream stream) throws ParseException, IOException, LoggerException {
        int size;
        if (lengthSize == 2) {
            size = stream.read() << 8 | stream.read();
        } else if (lengthSize == 4) {
            size = stream.read() << 24 | stream.read() << 16 | stream.read() << 8 | stream.read();
        } else {
            throw new NumberFormatException("Unsupported lengthSize [" + lengthSize + "].");
        }
        byte[] buf = new byte[size];
        if (stream.read(buf) != size) {
            throw new ParseException("Stream was broken.");
        }
        if (showRequest) {
            IsoEmulator.logger.log(0, "Received data of " + size + " bytes:", buf);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        int type = bais.read() << 8 | bais.read();
        if (showRequest) {
            System.out.format("   MTI=%04X\n", type);
        }
        Message message = new Message(type);
        message.parse(bais);
        if (bais.available() != 0) {
            bais.close();
            throw new ParseException("The packet's length does not correspond to that of the length field.");
        }
        bais.close();
        return message;
    }

    static final void setupConfiguration(Element config) {
        lengthSize = Byte.parseByte(config.getAttribute("lengthSize"));
        setupFieldDefenitions(config);
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
}
