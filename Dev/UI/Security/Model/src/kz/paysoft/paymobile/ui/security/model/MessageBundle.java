/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.30 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.security.model;

import java.util.ListResourceBundle;

public class MessageBundle extends ListResourceBundle {
    public static final String SUB_BRANCH_EXISTS = "SUB_BRANCH_EXISTS";

    private static final Object[][] sMessageStrings =
        new String[][] { { SUB_BRANCH_EXISTS, "—уществует дочерний филиал." }, { "BRANCHES_UK_NAME", "‘илиал с таким наименованием уже существует." },
                         { "USERS_UK_SYS_NAME", "ѕользователь с таким системным именем уже существует." },
                         { "USERS_UK_FULL_NAME", "ѕользователь с таким полным именем уже существует." },
                         { "USERS_BRANCHES_UK_USER_BRANCH", "ѕользователь уже определен в данном филиале." } };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     * @return
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
