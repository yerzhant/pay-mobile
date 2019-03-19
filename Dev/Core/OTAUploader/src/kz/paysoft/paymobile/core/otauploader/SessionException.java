/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.18   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.otauploader;

final class SessionException extends Exception {
    SessionException(Throwable throwable) {
        super(throwable);
    }

    SessionException(String string) {
        super(string);
    }
}
