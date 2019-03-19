/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * 2010.06.03 Yerzhan Tulepov         Created.
 */

package kz.paysoft.paymobile.ui.setup.model;

import java.util.ListResourceBundle;

public class MessageBundle extends ListResourceBundle {
    public static final String OPERATION_IS_DISALLOWED = "OPERATION_IS_DISALLOWED";

    private static final Object[][] sMessageStrings =
        new String[][] { { OPERATION_IS_DISALLOWED, "�������� ���������." }, { "DOC_TYPES_UK_NAME", "����� ������������ ��� ������������." },
                         { "COUNTRIES_UK_NAME", "����� ������������ ��� ������������." }, { "CURRENCIES_UK_CODE", "����� ��� ��� ������������." },
                         { "CURRENCIES_UK_NAME", "����� ������������ ��� ������������." },
                         { "STATUS_DETAILS_UK_CODE", "����� ��� ��� ������������." },
                         { "STATUS_DETAILS_UK_NAME", "����� ������������ ��� ������������." },
                         { "GLOBAL_PARAMS_UK_PARAM", "����� �������� ��� ������������." }, { "INTERFACES_UK_CODE", "����� ��� ��� ������������." },
                         { "INTERFACES_UK_NAME", "����� ������������ ��� ������������." },
                         { "SR_TYPES_UK_NAME", "����� ������������ ��� ������������." },
                         { "SR_TYPE_CODES_UK_SRT_IFC_TYPE", "� ������ ���� ������� ��� ���������� ������ � ������ �� ����������� � �����." },
                         { "SR_TYPE_CODES_UK_IFC_TYPE_CODE", "� ������� ��� ���������� ������ � ������ �� �����������, ����� � �����." },
                         { "CIPHER_SUITES_UK_CODE", "����� ��� ��� ������������." } };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     * @return
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
