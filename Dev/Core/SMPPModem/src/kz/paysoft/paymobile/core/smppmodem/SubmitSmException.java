/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.29   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppmodem;

final class SubmitSmException extends Exception {
    SubmitSmException(String message) {
        super(message);
    }

    SubmitSmException(Throwable cause) {
        super(cause);
    }
}
