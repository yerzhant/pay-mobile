package kz.paysoft.paymobile.ui.pmclient.model;

import java.util.ListResourceBundle;

public class MessageBundle extends ListResourceBundle {
    public static final String OPERATION_NOT_PERMITTED = "OPERATION_NOT_PERMITTED";
    public static final String CONTRACT_ALREADY_APPROVED = "CONTRACT_ALREADY_APPROVED";
    public static final String CONTRACT_ALREADY_CLOSED = "CONTRACT_ALREADY_CLOSED";

    private static final Object[][] sMessageStrings =
        new String[][] { { OPERATION_NOT_PERMITTED, "�������� ���������." }, { "FX_RATES_UK_CURRENCY", "����� ������ ������ ��� �����������." },
                         { "APP_ACCOUNTS_UK_AKIN", "������ � ������ �� �����, ����� � ������� ��� ����������." },
                         { "APP_ACCOUNTS_UK_AKN", "������ � ������ �� ����� � ������������ ��� ����������." },
                         { "APP_ACCOUNTS_CK_SINGLE_CURR", "������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
                         { "APP_ACCOUNTS_CK_DAY_AMOUNT", "�������� ����� �� ������ ���� ������ ������� �����." },
                         { "APP_ACCOUNTS_CK_DAY_CURRENCY", "�������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
                         { "APP_ACCOUNTS_CK_MONTH_AMOUNT", "�������� ����� �� ������ ���� ������ �������� �����." },
                         { "APP_ACCOUNTS_CK_MONTH_CURRENCY", "�������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
                         { "APP_ACCOUNTS_CK_MONTH_QUANTITY", "�������� ���������� �� ������ ���� ������ ��������� ����������." },
                         { "APP_ACCOUNTS_CK_FROM_TO", "����� �� �� ������ ���� ������, ��� ����� �." },
                         { "CLIENTS_UK_COUNTRY_DOC", "������ � ������ �������, ����� � � ��������� ��� �������." },
                         { "ACCOUNTS_UK_CKIN", "������ � ������ �� �����, ����� � ������� ��� ����������." },
                         { "ACCOUNTS_UK_CKN", "������ � ������ �� ����� � ������������ ��� ����������." },
                         { "ACCOUNTS_CK_SINGLE_CURRENCY", "������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
                         { "ACCOUNTS_CK_DAY_AMOUNT", "�������� ����� �� ������ ���� ������ ������� �����." },
                         { "ACCOUNTS_CK_DAY_CURRENCY", "�������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
                         { "ACCOUNTS_CK_MONTH_AMOUNT", "�������� ����� �� ������ ���� ������ �������� �����." },
                         { "ACCOUNTS_CK_MONTH_CURRENCY", "�������� ������ � ����� ������ ���� ��� ��������������, ���� ��� �������������." },
                         { "ACCOUNTS_CK_MONTH_QUANTITY", "�������� ���������� �� ������ ���� ������ ��������� ����������." },
                         { "ACCOUNTS_CK_FROM_TO", "����� �� �� ������ ���� ������, ��� ����� �." },
                         { CONTRACT_ALREADY_APPROVED, "������� ��� �������." }, { CONTRACT_ALREADY_CLOSED, "�������� ��� ������." } };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
