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
            { "APP_ACCOUNTS_UK_AKIN", "Запись с такими же Видом, Типом и Номером уже существует." },
            { "APP_ACCOUNTS_UK_AKN", "Запись с такими же Видом и Нименованием уже существует." },
            { "APP_ACCOUNTS_CK_SINGLE_CURR", "Разовые валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
            { "APP_ACCOUNTS_CK_DAY_AMOUNT", "Суточная сумма не должна быть меньше Разовой суммы." },
            { "APP_ACCOUNTS_CK_DAY_CURRENCY", "Суточные валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
            { "APP_ACCOUNTS_CK_MONTH_AMOUNT", "Месячная сумма не должна быть меньше Суточной суммы." },
            { "APP_ACCOUNTS_CK_MONTH_CURRENCY", "Месячные валюта и сумма должны либо оба присутствовать, либо оба отсутствовать." },
            { "APP_ACCOUNTS_CK_MONTH_QUANTITY", "Месячное количество не должно быть меньше Суточного количества." },
            { "APP_ACCOUNTS_CK_FROM_TO", "Время До не должно быть раньше, чем Время С." }
        };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     * @return
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
