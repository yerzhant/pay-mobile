/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.13   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppserver;

final class PduException extends Exception {
    PduException(String message) {
        super(message);
    }

    PduException(Throwable cause) {
        super(cause);
    }
}
