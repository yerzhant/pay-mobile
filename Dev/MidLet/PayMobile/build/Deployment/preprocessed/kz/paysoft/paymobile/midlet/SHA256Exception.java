/*
 * Copyright (C) 2010 PaySoft, LLP. All Rights Reserved.
 *
 * Error prefix: none.
 * Last error index: none.
 *
 * 2010.03.27   Yerzhan Tulepov         Moved here from a test project.
 * 2010.03.11   Yerzhan Tulepov         Created.
 */
package kz.paysoft.paymobile.midlet;

/**
 *
 * @author Yerzhan Tulepov
 */
final class SHA256Exception extends Exception {

    /**
     * Constructs an instance of <code>SHA256Exception</code> with the specified detail message.
     * @param msg the detail message.
     */
    SHA256Exception(String msg) {
        super(msg);
    }
}
