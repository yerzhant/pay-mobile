/*
     * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
     *
     * 2010.04.29   Yerzhan Tulepov         Created.
     */

package kz.paysoft.paymobile.iface.xml;

final class ProcessException extends Exception {
    ProcessException(Throwable throwable) {
        super(throwable);
    }

    ProcessException(String string) {
        super(string);
    }
}

