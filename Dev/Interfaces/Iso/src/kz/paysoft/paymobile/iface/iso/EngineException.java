/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.01   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.iface.iso;

final class EngineException extends Exception {
    EngineException(Throwable throwable) {
        super(throwable);
    }

    EngineException(String string) {
        super(string);
    }
}

