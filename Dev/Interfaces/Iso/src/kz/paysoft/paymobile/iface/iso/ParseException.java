/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.01   Yerzhan Tulepov         Copied from PaySys.
 * 27.10.2008	1.1.0	Yerzhan Tulepov		Created.
 */

package kz.paysoft.paymobile.iface.iso;

final class ParseException extends Exception {
    ParseException(String message) {
        super(message);
    }

    ParseException(Throwable cause) {
        super(cause);
    }
}
