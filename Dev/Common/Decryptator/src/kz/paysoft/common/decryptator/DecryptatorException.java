/*
 * Copyright (C) 2008-2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.04.12   Yerzhan Tulepov         Migrated from PaySysLib and renamed.
 * 22.10.2008	1.1.0	Yerzhan Tulepov	1.0.0.0->1.1.0.
 * 05.05.2008	1.0.0.0	Yerzhan Tulepov	Created.
 */

package kz.paysoft.common.decryptator;

public final class DecryptatorException extends Exception {
    DecryptatorException(String message) {
        super(message);
    }

    DecryptatorException(Throwable cause) {
        super(cause);
    }
}
