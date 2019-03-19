package kz.paysoft.paymobile.ui.pmclient.model;

import java.util.ListResourceBundle;

public class MessageBundle extends ListResourceBundle {
    public static final String OPERATION_NOT_PERMITTED = "OPERATION_NOT_PERMITTED";
    public static final String CONTRACT_ALREADY_APPROVED = "CONTRACT_ALREADY_APPROVED";
    public static final String CONTRACT_ALREADY_CLOSED = "CONTRACT_ALREADY_CLOSED";

    private static final Object[][] sMessageStrings =
        new String[][] { { OPERATION_NOT_PERMITTED, "Операция запрещена." }, { "FX_RATES_UK_CURRENCY", "Курсы данной валюты уже установлены." },
                         { "APP_ACCOUNTS_UK_AKIN", "Запись с такими же Видом, Типом и Номером уже существует." },
                         { "APP_ACCOUNTS_UK_AKN", "Запись с такими же Видом и Нименованием уже существует." },
                         { "APP_ACCOUNTS_CK_SINGLE_CURR", "Разовые валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
                         { "APP_ACCOUNTS_CK_DAY_AMOUNT", "Суточная сумма не должна быть меньше Разовой суммы." },
                         { "APP_ACCOUNTS_CK_DAY_CURRENCY", "Суточные валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
                         { "APP_ACCOUNTS_CK_MONTH_AMOUNT", "Месячная сумма не должна быть меньше Суточной суммы." },
                         { "APP_ACCOUNTS_CK_MONTH_CURRENCY", "Месячные валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
                         { "APP_ACCOUNTS_CK_MONTH_QUANTITY", "Месячное количество не должно быть меньше Суточного количества." },
                         { "APP_ACCOUNTS_CK_FROM_TO", "Время До не должно быть раньше, чем Время С." },
                         { "CLIENTS_UK_COUNTRY_DOC", "Клиент с такими Страной, Типом и № документа уже заведен." },
                         { "ACCOUNTS_UK_CKIN", "Запись с такими же Видом, Типом и Номером уже существует." },
                         { "ACCOUNTS_UK_CKN", "Запись с такими же Видом и Нименованием уже существует." },
                         { "ACCOUNTS_CK_SINGLE_CURRENCY", "Разовые валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
                         { "ACCOUNTS_CK_DAY_AMOUNT", "Суточная сумма не должна быть меньше Разовой суммы." },
                         { "ACCOUNTS_CK_DAY_CURRENCY", "Суточные валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
                         { "ACCOUNTS_CK_MONTH_AMOUNT", "Месячная сумма не должна быть меньше Суточной суммы." },
                         { "ACCOUNTS_CK_MONTH_CURRENCY", "Месячные валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
                         { "ACCOUNTS_CK_MONTH_QUANTITY", "Месячное количество не должно быть меньше Суточного количества." },
                         { "ACCOUNTS_CK_FROM_TO", "Время До не должно быть раньше, чем Время С." },
                         { CONTRACT_ALREADY_APPROVED, "Договор уже одобрен." }, { CONTRACT_ALREADY_CLOSED, "Договора уже закрыт." } };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
