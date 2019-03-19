/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.14   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.appentry.view.bean;

public final class EnrollBean {
    private boolean applicationCreated = false;

    public EnrollBean() {
    }

    /**
     * @return
     */
    public final boolean isApplicationCreated() {
        if (applicationCreated) {
            return true;
        } else {
            applicationCreated = true;
            return false;
        }
    }
}
