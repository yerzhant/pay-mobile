/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.02   Yerzhan Tulepov         Copied from kz.paysoft.paymobile.iface.iso.
 * 25.10.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.isoemulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.math.BigInteger;


final class Value {
    final LengthType lengthType;
    final ValueType valueType;
    final short maxLength;
    final String value;

    Value(LengthType lengthType, ValueType valueType, short maxLength, String value) {
        if (value != null && valueType == ValueType.BCD) {
            value = new BigInteger(value).toString();
        }
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException("The value is longer, than the maximum length.");
        }
        if (lengthType == LengthType.LLVAR && maxLength > 99) {
            throw new IllegalArgumentException("The maximum length is greater than 99.");
        } else if (lengthType == LengthType.LLLVAR && maxLength > 999) {
            throw new IllegalArgumentException("The maximum length is greater than 999.");
        } else if (lengthType == LengthType.BBVAR && maxLength > 255) {
            throw new IllegalArgumentException("The maximum length is greater than 255.");
        }
        if (value != null && valueType == ValueType.HEX) {
            if (value.length() % 2 == 1) {
                throw new IllegalArgumentException("The HEX value has odd number of digits.");
            } else if (lengthType == LengthType.FIXED && value.length() != maxLength) {
                throw new IllegalArgumentException("A length of the fixed HEX value does not equal to the maximum length.");
            }
        }
        this.lengthType = lengthType;
        this.valueType = valueType;
        this.maxLength = maxLength;
        this.value = value;
    }

    final Value clone(String val) {
        return new Value(lengthType, valueType, maxLength, val);
    }

    final void write(OutputStream stream) throws IOException {
        if (lengthType != LengthType.FIXED) {
            if (lengthType == LengthType.BBVAR) {
                stream.write(value.length());
            } else {
                String length = String.format("%03d", value.length());
                byte[] buffer = new byte[2];
                buffer[0] = (byte)(length.charAt(0) - 0x30);
                buffer[1] = (byte)(((length.charAt(1) - 0x30) << 4) | (length.charAt(2) - 0x30));
                if (lengthType == LengthType.LLLVAR) {
                    stream.write(buffer[0]);
                }
                stream.write(buffer[1]);
            }
        }
        if (valueType == ValueType.BCD) {
            stream.write(toBCD());
        } else if (valueType == ValueType.ANS) {
            if (lengthType == LengthType.FIXED) {
                stream.write(String.format("%-" + maxLength + "s", value).getBytes());
            } else {
                stream.write(value.getBytes());
            }
        } else {
            stream.write(toHEX());
        }
    }

    private final byte[] toBCD() {
        int i = 0, j = 0;
        int valueLength = value.length();
        byte[] buffer;
        if (lengthType == LengthType.FIXED) {
            i = (maxLength / 2 + maxLength % 2) - (valueLength / 2 + valueLength % 2);
            buffer = new byte[maxLength / 2 + maxLength % 2];
        } else {
            buffer = new byte[valueLength / 2 + valueLength % 2];
        }
        if (valueLength % 2 == 1) {
            buffer[i++] = (byte)(value.charAt(j++) - 0x30);
        }
        while (j < valueLength) {
            buffer[i++] = (byte)(((value.charAt(j++) - 0x30) << 4) | (value.charAt(j++) - 0x30));
        }
        return buffer;
    }

    private final byte[] toHEX() {
        int i = 0, j = 0;
        int valueLength = value.length();
        byte[] buffer = new byte[valueLength / 2];
        while (j < valueLength) {
            buffer[i++] = (byte)((Character.digit(value.charAt(j++), 16) << 4) | Character.digit(value.charAt(j++), 16));
        }
        return buffer;
    }

    final Value parse(InputStream stream) throws IOException {
        short valueLength = 0;
        if (lengthType == LengthType.FIXED) {
            valueLength = maxLength;
        } else if (lengthType == LengthType.BBVAR) {
            valueLength = (short)stream.read();
        } else {
            if (lengthType == LengthType.LLLVAR) {
                valueLength = (short)(stream.read() * 100);
            }
            short x = (short)stream.read();
            valueLength += (x >> 4) * 10 + (x & 0xf);
        }
        String val;
        if (valueType == ValueType.BCD) {
            val = fromBCD(valueLength, stream);
        } else if (valueType == ValueType.ANS) {
            byte[] buffer = new byte[valueLength];
            stream.read(buffer);
            val = new String(buffer);
        } else {
            byte[] buffer = new byte[valueLength / 2];
            stream.read(buffer);
            val = String.format("%0" + valueLength + "X", new BigInteger(buffer));
        }
        System.out.println(val);
        return new Value(lengthType, valueType, maxLength, val);
    }

    private final String fromBCD(short valueLength, InputStream stream) throws IOException {
        StringBuilder buffer = new StringBuilder(valueLength);
        for (short i = 0; i < valueLength / 2 + valueLength % 2; i++) {
            short x = (short)stream.read();
            buffer.append((char)((x >> 4) + 0x30));
            buffer.append((char)((x & 0xf) + 0x30));
        }
        return buffer.toString();
    }

    enum LengthType {
        FIXED,
        LLVAR,
        LLLVAR,
        BBVAR;
    }

    enum ValueType {
        BCD,
        ANS,
        HEX;
    }
}
