/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.18   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.core.frontserver;

final class EngineException extends Exception {
    EngineException(Throwable throwable) {
        super(throwable);
    }

    EngineException(String string) {
        super(string);
    }
}
