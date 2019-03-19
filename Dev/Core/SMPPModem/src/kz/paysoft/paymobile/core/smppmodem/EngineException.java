/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.07.23   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.smppmodem;

final class EngineException extends Exception {
    EngineException(String message) {
        super(message);
    }

    EngineException(Throwable cause) {
        super(cause);
    }
}
