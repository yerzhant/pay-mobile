/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Copied from kz.paysoft.paymobile.iface.iso.
 * 24.10.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.isoemulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


final class Message {
    final int type;
    short bitMapSize = 64;
    final byte[] bitMap = new byte[16];
    final HashMap<Short, Value> fields = new HashMap<Short, Value>();

    Message(int type) {
        this.type = type;
    }

    protected final Message clone() {
        Message newMessage = new Message(type);
        for (Short i : fields.keySet()) {
            newMessage.setField(i, fields.get(i));
        }
        return newMessage;
    }


    final String getFieldValue(Short number) {
        return fields.get(number).value;
    }

    final void setField(Short number, String value) {
        setField(number, Engine.createField(number, value));
    }

    final void setField(short number, Value value) {
        if (number < 2 || number > 128 || number == 65) {
            throw new IllegalArgumentException("Incorrect field number [" + number + "].");
        }
        byte b = (byte)(number % 8 == 0 ? 1 : (0x80 >> (number % 8 - 1)));
        bitMap[(number - 1) / 8] |= b;
        if (bitMapSize == 64 && number > bitMapSize) {
            bitMapSize = 128;
            bitMap[0] |= 0x80;
        }
        fields.put(number, value);
    }

    final void write(OutputStream stream) throws IOException {
        stream.write(type >> 8);
        stream.write(type);
        stream.write(bitMap, 0, bitMapSize / 8);
        ArrayList<Short> fieldNumbers = new ArrayList<Short>();
        fieldNumbers.addAll(fields.keySet());
        Collections.sort(fieldNumbers);
        for (Short i : fieldNumbers) {
            fields.get(i).write(stream);
        }
    }

    final void parse(InputStream stream) throws ParseException, IOException {
        long bitMap1 = (long)stream.read() << 56 | (long)stream.read() << 48 | (long)stream.read() << 40 | (long)stream.read() << 32 | (long)stream.read() << 24 | stream.read() << 16 | stream.read() << 8 | stream.read();
        long bitMap2 = 0;
        if ((bitMap1 & 0x8000000000000000L) != 0) {
            bitMap2 = (long)stream.read() << 56 | (long)stream.read() << 48 | (long)stream.read() << 40 | (long)stream.read() << 32 | (long)stream.read() << 24 | stream.read() << 16 | stream.read() << 8 | stream.read();
        }
        if ((bitMap2 & 0x8000000000000000L) != 0) {
            throw new ParseException("Third bit map is not supported.");
        }
        long maskBit = 0x4000000000000000L;
        for (short i = 2; i <= 64; i++) {
            if ((bitMap1 & maskBit) != 0) {
                setField(i, Engine.parseField(i, stream));
            }
            maskBit >>>= 1;
        }
        if (bitMap2 != 0) {
            maskBit = 0x4000000000000000L;
            for (short i = 66; i <= 128; i++) {
                if ((bitMap2 & maskBit) != 0) {
                    setField(i, Engine.parseField(i, stream));
                }
                maskBit >>>= 1;
            }
        }
    }
}
