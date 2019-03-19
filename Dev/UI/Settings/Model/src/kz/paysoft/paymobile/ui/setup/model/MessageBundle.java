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
        new String[][] { { OPERATION_IS_DISALLOWED, "Операция запрещена." }, { "DOC_TYPES_UK_NAME", "Такое наименование уже используется." },
                         { "COUNTRIES_UK_NAME", "Такое наименование уже используется." }, { "CURRENCIES_UK_CODE", "Такой код уже используется." },
                         { "CURRENCIES_UK_NAME", "Такое наименование уже используется." },
                         { "STATUS_DETAILS_UK_CODE", "Такой код уже используется." },
                         { "STATUS_DETAILS_UK_NAME", "Такое наименование уже используется." },
                         { "GLOBAL_PARAMS_UK_PARAM", "Такой параметр уже используется." }, { "INTERFACES_UK_CODE", "Такой код уже используется." },
                         { "INTERFACES_UK_NAME", "Такое наименование уже используется." },
                         { "SR_TYPES_UK_NAME", "Такое наименование уже используется." },
                         { "SR_TYPE_CODES_UK_SRT_IFC_TYPE", "В данном Типе запроса уже существует запись с такими же интерфейсом и типом." },
                         { "SR_TYPE_CODES_UK_IFC_TYPE_CODE", "В системе уже определена запись с такими же интерфейсом, типом и кодом." },
                         { "CIPHER_SUITES_UK_CODE", "Такой код уже используется." } };

    /**Return String Identifiers and corresponding Messages in a two-dimensional array.
     * @return
     */
    protected Object[][] getContents() {
        return sMessageStrings;
    }
}
