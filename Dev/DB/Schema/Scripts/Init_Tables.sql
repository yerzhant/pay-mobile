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

INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'AUD', '������������� ������');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'CAD', '��������� ������');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'CHF', '����������� �����');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'EUR', '����');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'GBP', '���������� ����');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'JPY', '�������� ����');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'KGS', '���������� ���');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'KZT', '������������� �����');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'RUR', '���������� �����');
INSERT INTO currencies(id$, user$, code, name) VALUES (currencies_seq.NEXTVAL, &init_tables_user, 'USD', '������������ ������');
COMMIT;

INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user,   0, 'Successfully', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user,   1, 'Service activated', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 218, 'Operation not permited', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 219, 'Insufficient funds', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 220, 'Time out', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 221, '�������� ���� �� ���������', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 222, '������� ������� ������������', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 223, '������� ������������', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 224, '�������������� �����', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 225, 'Invalid password', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 226, '�������� ����� �������� ������', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 227, '��������� ����������� �����', 'Y');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 228, '�������� ������', 'N');
INSERT INTO status_details(id$, user$, code, name, send_sms) VALUES (status_details_seq.NEXTVAL, &init_tables_user, 229, 'System error', 'Y');
COMMIT;

INSERT INTO global_params(id$, user$, param, value) VALUES (global_params_seq.NEXTVAL, &init_tables_user, 'PSW_TRIES_LIMIT', '3');
COMMIT;

INSERT INTO interfaces(id$, user$, format, code, name) VALUES (interfaces_seq.NEXTVAL, &init_tables_user, 'S', 'FRONT', '����� ������');
INSERT INTO interfaces(id$, user$, format, code, name) VALUES (interfaces_seq.NEXTVAL, &init_tables_user, 'S', 'SMPP', 'SMPP ������');
INSERT INTO interfaces(id$, user$, format, code, name) VALUES (interfaces_seq.NEXTVAL, &init_tables_user, 'S', 'SYS', '���������');
COMMIT;

SPOOL OFF
