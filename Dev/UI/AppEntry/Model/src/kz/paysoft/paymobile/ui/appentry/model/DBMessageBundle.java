/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.05.17   Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.appentry.model;

import java.util.ListResourceBundle;

public final class DBMessageBundle extends ListResourceBundle {
    private static final Object[][] sMessageStrings =
        new String[][] {
            { "APP_ACCOUNTS_UK_AKIN", "������ � ������ �� �����, ����� � ������� ��� ����������." },
            { "APP_ACCOUNTS_UK_AKN", "������ � ������ �� ����� � ������������ ��� ����������." },
            { "APP_ACCOUNTS_CK_SINGLE_CURR", "������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
            { "APP_ACCOUNTS_CK_DAY_AMOUNT", "�������� ����� �� ������ ���� ������ ������� �����." },
            { "APP_ACCOUNTS_CK_DAY_CURRENCY", "�������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
            { "APP_ACCOUNTS_CK_MONTH_AMOUNT", "�������� ����� �� ������ ���� ������ �������� �����." },
            { "APP_ACCOUNTS_CK_MONTH_CURRENCY", "�������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
            { "APP_ACCOUNTS_CK_MONTH_QUANTITY", "�������� ���������� �� ������ ���� ������ ��������� ����������." },
            { "APP_ACCOUNTS_CK_FROM_TO", "����� �� �� ������ ���� ������, ��� ����� �." }
        };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     * @return
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
