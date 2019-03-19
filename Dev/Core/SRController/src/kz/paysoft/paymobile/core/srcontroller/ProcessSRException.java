/*
     * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
     *
     * 2010.04.26   Yerzhan Tulepov         Created.
     */

package kz.paysoft.paymobile.core.srcontroller;

final class ProcessSRException extends Exception {
    ProcessSRException(Throwable throwable) {
        super(throwable);
    }

    ProcessSRException(String string) {
        super(string);
    }
}

