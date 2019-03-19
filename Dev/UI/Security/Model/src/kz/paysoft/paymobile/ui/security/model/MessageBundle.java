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
        new String[][] { { SUB_BRANCH_EXISTS, "���������� �������� ������." }, { "BRANCHES_UK_NAME", "������ � ����� ������������� ��� ����������." },
                         { "USERS_UK_SYS_NAME", "������������ � ����� ��������� ������ ��� ����������." },
                         { "USERS_UK_FULL_NAME", "������������ � ����� ������ ������ ��� ����������." },
                         { "USERS_BRANCHES_UK_USER_BRANCH", "������������ ��� ��������� � ������ �������." } };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     * @return
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
