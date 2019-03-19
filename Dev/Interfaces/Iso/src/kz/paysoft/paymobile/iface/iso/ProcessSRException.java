/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.01   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.iso;

final class ProcessSRException extends Exception {
    ProcessSRException(Throwable throwable) {
        super(throwable);
    }

    ProcessSRException(String string) {
        super(string);
    }
}

