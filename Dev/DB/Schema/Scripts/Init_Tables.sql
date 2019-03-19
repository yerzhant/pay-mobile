/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Initialize all the tables.
 *
 * 2010.04.08 Yerzhan Tulepov         Created.
 */

SPOOL Logs\4_Init_Tables.log

DEFINE init_tables_user = "'PAY_MOBILE'"

INSERT INTO users(id$, user$, sys_name, full_name) VALUES (users_seq.NEXTVAL, &init_tables_user, &init_tables_user, 'Pay Mobile');

INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'AUD', 'Австралийский доллар');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'CAD', 'Канадский доллар');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'CHF', 'Швейцарский франк');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'EUR', 'Евро');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'GBP', 'Английский фунт');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'JPY', 'Японская Йена');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'KGS', 'Киргизский сом');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'KZT', 'Казахстанский тенге');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'RUR', 'Российский рубль');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'USD', 'Американский доллар');
COMMIT;

INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user,   0, 'Successfully', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user,   1, 'Service activated', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 218, 'Operation not permited', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 219, 'Insufficient funds', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 220, 'Time out', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 221, 'Обменный курс не определен', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 222, 'Понижен уровень безопасности', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 223, 'Договор заблокирован', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 224, 'Несопоставимый ответ', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 225, 'Invalid password', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 226, 'Исчерпан лимит проверок пароля', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 227, 'Превышено ограничение счета', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 228, 'Неверный формат', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 229, 'System error', 'Y');
COMMIT;

INSERT INTO global_params(id$, user$, param, value) VALUES (global_params_seq.NEXTVAL, &init_tables_user, 'PSW_TRIES_LIMIT', '3');
COMMIT;

INSERT INTO interfaces(id$, user$, format, code, name) VALUES (interfaces_seq.NEXTVAL, &init_tables_user, 'S', 'FRONT', 'Фронт Сервер');
INSERT INTO interfaces(id$, user$, format, code, name) VALUES (interfaces_seq.NEXTVAL, &init_tables_user, 'S', 'SMPP', 'SMPP Сервер');
INSERT INTO interfaces(id$, user$, format, code, name) VALUES (interfaces_seq.NEXTVAL, &init_tables_user, 'S', 'SYS', 'Системный');
COMMIT;

SPOOL OFF
