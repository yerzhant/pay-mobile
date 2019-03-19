/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Creates tables.
 *
 * 2010.04.07 Yerzhan Tulepov         Created.
 */

SPOOL Logs\2_Tables.log

DEFINE acc_type          = "VARCHAR2(70)"
DEFINE acc_name_type     = "VARCHAR2(250)"
DEFINE amount_type       = "NUMBER(12, 2)"
DEFINE ifc_type          = "VARCHAR2(32)"
DEFINE mc_type           = "VARCHAR2(250)"
DEFINE pk_maxvalue       = "999999999999"
DEFINE pk_type           = "NUMBER(12)"
DEFINE cipher_suite_type = "VARCHAR2(128)"
DEFINE user_type         = "VARCHAR2(64)"

--                                          *                     *                      *                                          *
CREATE TABLE branches(
 id$                                        &pk_type                                     CONSTRAINT branches_nn_id$                 NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT branches_nn_status$             NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT branches_nn_date$               NOT NULL,
 user$                                      &user_type                                   CONSTRAINT branches_nn_user$               NOT NULL,
 name                                       VARCHAR2(250)                                CONSTRAINT branches_nn_name                NOT NULL,
 parent_id$                                 &pk_type,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT branches_pk_id$                 PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX branches_ux_id$ ON branches(id$) TABLESPACE dic_i),
 CONSTRAINT branches_uk_name                UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX branches_ux_name ON branches(name, hu$) TABLESPACE dic_i),
 CONSTRAINT branches_fk_cid$                FOREIGN KEY(cid$)                            REFERENCES branches(id$),
 CONSTRAINT branches_fk_parent_id$          FOREIGN KEY(parent_id$)                      REFERENCES branches(id$),
 CONSTRAINT branches_ck_status$             CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE doc_types(
 id$                                        &pk_type                                     CONSTRAINT doc_types_nn_id$                NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT doc_types_nn_status$            NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT doc_types_nn_date$              NOT NULL,
 user$                                      &user_type                                   CONSTRAINT doc_types_nn_user$              NOT NULL,
 name                                       VARCHAR2(250)                                CONSTRAINT doc_types_nn_name               NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT doc_types_pk_id$                PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX doc_types_ux_id$ ON doc_types(id$) TABLESPACE dic_i),
 CONSTRAINT doc_types_uk_name               UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX doc_types_ux_name ON doc_types(name, hu$) TABLESPACE dic_i),
 CONSTRAINT doc_types_fk_cid$               FOREIGN KEY(cid$)                            REFERENCES doc_types(id$),
 CONSTRAINT doc_types_ck_status$            CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE countries(
 id$                                        &pk_type                                     CONSTRAINT countries_nn_id$                NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT countries_nn_status$            NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT countries_nn_date$              NOT NULL,
 user$                                      &user_type                                   CONSTRAINT countries_nn_user$              NOT NULL,
 name                                       VARCHAR2(250)                                CONSTRAINT countries_nn_name               NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT countries_pk_id$                PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX countries_ux_id$ ON countries(id$) TABLESPACE dic_i),
 CONSTRAINT countries_uk_name               UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX countries_ux_name ON countries(name, hu$) TABLESPACE dic_i),
 CONSTRAINT countries_fk_cid$               FOREIGN KEY(cid$)                            REFERENCES countries(id$),
 CONSTRAINT countries_ck_status$            CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE currencies(
 id$                                        &pk_type                                     CONSTRAINT currencies_nn_id$               NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT currencies_nn_status$           NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT currencies_nn_date$             NOT NULL,
 user$                                      &user_type                                   CONSTRAINT currencies_nn_user$             NOT NULL,
 code                                       VARCHAR2(3)                                  CONSTRAINT currencies_nn_code              NOT NULL,
 name                                       VARCHAR2(250)                                CONSTRAINT currencies_nn_name              NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT currencies_pk_id$               PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX currencies_ux_id$ ON currencies(id$) TABLESPACE dic_i),
 CONSTRAINT currencies_uk_code              UNIQUE(code, hu$)
																						USING INDEX (CREATE UNIQUE INDEX currencies_ux_code ON currencies(code, hu$) TABLESPACE dic_i),
 CONSTRAINT currencies_uk_name              UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX currencies_ux_name ON currencies(name, hu$) TABLESPACE dic_i),
 CONSTRAINT currencies_fk_cid$              FOREIGN KEY(cid$)                            REFERENCES currencies(id$),
 CONSTRAINT currencies_ck_status$           CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE users(
 id$                                        &pk_type                                     CONSTRAINT users_nn_id$                    NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT users_nn_status$                NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT users_nn_date$                  NOT NULL,
 user$                                      &user_type                                   CONSTRAINT users_nn_user$                  NOT NULL,
 sys_name                                   &user_type                                   CONSTRAINT users_nn_sys_name               NOT NULL,
 full_name                                  VARCHAR2(250)                                CONSTRAINT users_nn_full_name              NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT users_pk_id$                    PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX users_ux_id$ ON users(id$) TABLESPACE dic_i),
 CONSTRAINT users_uk_sys_name               UNIQUE(sys_name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX users_ux_sys_name ON users(sys_name, hu$) TABLESPACE dic_i),
 CONSTRAINT users_uk_full_name              UNIQUE(full_name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX users_ux_full_name ON users(full_name, hu$) TABLESPACE dic_i),
 CONSTRAINT users_fk_cid$                   FOREIGN KEY(cid$)                            REFERENCES users(id$),
 CONSTRAINT users_ck_status$                CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE users_branches(
 id$                                        &pk_type                                     CONSTRAINT users_branches_nn_id$           NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT users_branches_nn_status$       NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT users_branches_nn_date$         NOT NULL,
 user$                                      &user_type                                   CONSTRAINT users_branches_nn_user$         NOT NULL,
 user_id$                                   &pk_type                                     CONSTRAINT users_branches_nn_user_id$      NOT NULL,
 branch_id$                                 &pk_type                                     CONSTRAINT users_branches_nn_branch_id$    NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT users_branches_pk_id$           PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX users_branches_ux_id$ ON users_branches(id$) TABLESPACE dic_i),
 CONSTRAINT users_branches_uk_user_branch   UNIQUE(user_id$, branch_id$, hu$)
																						USING INDEX (CREATE UNIQUE INDEX users_branches_ux_user_branch ON users_branches(user_id$, branch_id$, hu$) TABLESPACE dic_i),
 CONSTRAINT users_branches_fk_cid$          FOREIGN KEY(cid$)                            REFERENCES users_branches(id$),
 CONSTRAINT users_branches_fk_user_id$      FOREIGN KEY(user_id$)                        REFERENCES users(id$),
 CONSTRAINT users_branches_fk_branch_id$    FOREIGN KEY(branch_id$)                      REFERENCES branches(id$),
 CONSTRAINT users_branches_ck_status$       CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE status_details(
 id$                                        &pk_type                                     CONSTRAINT status_details_nn_id$           NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT status_details_nn_status$       NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT status_details_nn_date$         NOT NULL,
 user$                                      &user_type                                   CONSTRAINT status_details_nn_user$         NOT NULL,
 code                                       NUMBER(3)                                    CONSTRAINT status_details_nn_code          NOT NULL,
 name                                       VARCHAR2(250)                                CONSTRAINT status_details_nn_name          NOT NULL,
 send_sms                                   VARCHAR2(1)           DEFAULT 'N'            CONSTRAINT status_details_nn_send_sms      NOT NULL,
 sms_text                                   VARCHAR2(160),
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT status_details_pk_id$           PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX status_details_ux_id$ ON status_details(id$) TABLESPACE dic_i),
 CONSTRAINT status_details_uk_code          UNIQUE(code, hu$)
																						USING INDEX (CREATE UNIQUE INDEX status_details_ux_code ON status_details(code, hu$) TABLESPACE dic_i),
 CONSTRAINT status_details_uk_name          UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX status_details_ux_name ON status_details(name, hu$) TABLESPACE dic_i),
 CONSTRAINT status_details_fk_cid$          FOREIGN KEY(cid$)                            REFERENCES status_details(id$),
 CONSTRAINT status_details_ck_status$       CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT status_details_ck_code          CHECK(code BETWEEN 0 AND 229),
 CONSTRAINT status_details_ck_send_sms      CHECK(send_sms IN ('Y', 'N'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE global_params(
 id$                                        &pk_type                                     CONSTRAINT global_params_nn_id$            NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT global_params_nn_status$        NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT global_params_nn_date$          NOT NULL,
 user$                                      &user_type                                   CONSTRAINT global_params_nn_user$          NOT NULL,
 param                                      VARCHAR2(128)                                CONSTRAINT global_params_nn_param          NOT NULL,
 value                                      VARCHAR2(250)                                CONSTRAINT global_params_nn_value          NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT global_params_pk_id$            PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX global_params_ux_id$ ON global_params(id$) TABLESPACE dic_i),
 CONSTRAINT global_params_uk_param          UNIQUE(param, hu$)
																						USING INDEX (CREATE UNIQUE INDEX global_params_ux_param ON global_params(param, hu$) TABLESPACE dic_i),
 CONSTRAINT global_params_fk_cid$           FOREIGN KEY(cid$)                            REFERENCES global_params(id$),
 CONSTRAINT global_params_ck_status$        CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE interfaces(
 id$                                        &pk_type                                     CONSTRAINT interfaces_nn_id$               NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT interfaces_nn_status$           NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT interfaces_nn_date$             NOT NULL,
 user$                                      &user_type                                   CONSTRAINT interfaces_nn_user$             NOT NULL,
 format                                     VARCHAR2(1)                                  CONSTRAINT interfaces_nn_format            NOT NULL,
 code                                       &ifc_type                                    CONSTRAINT interfaces_nn_code              NOT NULL,
 name                                       VARCHAR2(128)                                CONSTRAINT interfaces_nn_name              NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT interfaces_pk_id$               PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX interfaces_ux_id$ ON interfaces(id$) TABLESPACE dic_i),
 CONSTRAINT interfaces_uk_code              UNIQUE(code, hu$)
																						USING INDEX (CREATE UNIQUE INDEX interfaces_ux_code ON interfaces(code, hu$) TABLESPACE dic_i),
 CONSTRAINT interfaces_uk_name              UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX interfaces_ux_name ON interfaces(name, hu$) TABLESPACE dic_i),
 CONSTRAINT interfaces_fk_cid$              FOREIGN KEY(cid$)                            REFERENCES interfaces(id$),
 CONSTRAINT interfaces_ck_status$           CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT interfaces_ck_format            CHECK(format IN('S', 'X', 'I'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE sr_types(
 id$                                        &pk_type                                     CONSTRAINT sr_types_nn_id$                 NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT sr_types_nn_status$             NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT sr_types_nn_date$               NOT NULL,
 user$                                      &user_type                                   CONSTRAINT sr_types_nn_user$               NOT NULL,
 name                                       VARCHAR2(250)                                CONSTRAINT sr_types_nn_name                NOT NULL,
 is_2_phase                                 VARCHAR2(1)           DEFAULT 'N'            CONSTRAINT sr_types_nn_is_2_phase          NOT NULL,
 is_cancellable                             VARCHAR2(1)           DEFAULT 'N'            CONSTRAINT sr_types_nn_is_cancellable      NOT NULL,
 check_fields                               VARCHAR2(64),
 src_interface                              &ifc_type,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT sr_types_pk_id$                 PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX sr_types_ux_id$ ON sr_types(id$) TABLESPACE dic_i),
 CONSTRAINT sr_types_uk_name                UNIQUE(name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX sr_types_ux_name ON sr_types(name, hu$) TABLESPACE dic_i),
 CONSTRAINT sr_types_fk_cid$                FOREIGN KEY(cid$)                            REFERENCES sr_types(id$),
 CONSTRAINT sr_types_ck_status$             CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT sr_types_ck_is_2_phase          CHECK(is_2_phase IN ('Y', 'N')),
 CONSTRAINT sr_types_ck_is_cancellable      CHECK(is_cancellable IN ('Y', 'N'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE sr_type_codes(
 id$                                        &pk_type                                     CONSTRAINT sr_type_codes_nn_id$            NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT sr_type_codes_nn_status$        NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT sr_type_codes_nn_date$          NOT NULL,
 user$                                      &user_type                                   CONSTRAINT sr_type_codes_nn_user$          NOT NULL,
 sr_type_id$                                &pk_type                                     CONSTRAINT sr_type_codes_nn_sr_type_id$    NOT NULL,
 interface                                  &ifc_type                                    CONSTRAINT sr_type_codes_nn_interface      NOT NULL,
 type                                       VARCHAR2(1)                                  CONSTRAINT sr_type_codes_nn_type           NOT NULL,
 code                                       &mc_type                                     CONSTRAINT sr_type_codes_nn_code           NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT sr_type_codes_pk_id$            PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX sr_type_codes_ux_id$ ON sr_type_codes(id$) TABLESPACE dic_i),
 CONSTRAINT sr_type_codes_uk_srt_ifc_type   UNIQUE(sr_type_id$, interface, type, hu$)
																						USING INDEX (CREATE UNIQUE INDEX sr_type_codes_ux_srt_ifc_type ON sr_type_codes(sr_type_id$, interface, type, hu$) TABLESPACE dic_i),
 CONSTRAINT sr_type_codes_uk_ifc_type_code  UNIQUE(interface, type, code, hu$)
																						USING INDEX (CREATE UNIQUE INDEX sr_type_codes_ux_ifc_type_code ON sr_type_codes(interface, type, code, hu$) TABLESPACE dic_i),
 CONSTRAINT sr_type_codes_fk_cid$           FOREIGN KEY(cid$)                            REFERENCES sr_type_codes(id$),
 CONSTRAINT sr_type_codes_fk_sr_type_id$    FOREIGN KEY(sr_type_id$)                     REFERENCES sr_types(id$),
 CONSTRAINT sr_type_codes_ck_status$        CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT sr_type_codes_ck_type           CHECK(type IN ('I', 'S', 'D', 'C'))
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE fx_rates(
 id$                                        &pk_type                                     CONSTRAINT fx_rates_nn_id$                 NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT fx_rates_nn_status$             NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT fx_rates_nn_date$               NOT NULL,
 user$                                      &user_type                                   CONSTRAINT fx_rates_nn_user$               NOT NULL,
 currency                                   VARCHAR2(3)                                  CONSTRAINT fx_rates_nn_currency            NOT NULL,
 buy                                        NUMBER(18, 8)                                CONSTRAINT fx_rates_nn_buy                 NOT NULL,
 sell                                       NUMBER(18, 8)                                CONSTRAINT fx_rates_nn_sell                NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT fx_rates_pk_id$                 PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX fx_rates_ux_id$ ON fx_rates(id$) TABLESPACE misc_i),
 CONSTRAINT fx_rates_uk_currency            UNIQUE(currency, hu$)
																						USING INDEX (CREATE UNIQUE INDEX fx_rates_ux_currency ON fx_rates(currency, hu$) TABLESPACE misc_i),
 CONSTRAINT fx_rates_fk_cid$                FOREIGN KEY(cid$)                            REFERENCES fx_rates(id$),
 CONSTRAINT fx_rates_ck_status$             CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT fx_rates_ck_buy                 CHECK(buy > 0),
 CONSTRAINT fx_rates_ck_sell                CHECK(sell > 0)
)TABLESPACE misc_d;

--                                          *                     *                      *                                          *
CREATE TABLE cipher_suites(
 id$                                        &pk_type                                     CONSTRAINT cipher_suites_nn_id$            NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT cipher_suites_nn_status$        NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT cipher_suites_nn_date$          NOT NULL,
 user$                                      &user_type                                   CONSTRAINT cipher_suites_nn_user$          NOT NULL,
 code                                       &cipher_suite_type                           CONSTRAINT cipher_suites_nn_code           NOT NULL,
 a_level                                    NUMBER(3)                                    CONSTRAINT cipher_suites_nn_a_level        NOT NULL,
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT cipher_suites_pk_id$            PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX cipher_suites_ux_id$ ON cipher_suites(id$) TABLESPACE dic_i),
 CONSTRAINT cipher_suites_ck_status$        CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT cipher_suites_uk_code           UNIQUE(code, hu$)
																						USING INDEX (CREATE UNIQUE INDEX cipher_suites_ux_code ON cipher_suites(code, hu$) TABLESPACE dic_i)
)TABLESPACE dic_d;

--                                          *                     *                      *                                          *
CREATE TABLE applications(
 id$                                        &pk_type                                     CONSTRAINT applications_nn_id$             NOT NULL,
 status                                     VARCHAR2(1)           DEFAULT 'W'            CONSTRAINT applications_nn_status          NOT NULL,
 applied_at                                 DATE                  DEFAULT SYSDATE        CONSTRAINT applications_nn_applied_at      NOT NULL,
 country_id$                                &pk_type                                     CONSTRAINT applications_nn_country_id$     NOT NULL,
 doc_type_id$                               &pk_type                                     CONSTRAINT applications_nn_doc_type_id$    NOT NULL,
 doc_id                                     VARCHAR2(64)                                 CONSTRAINT applications_nn_doc_id          NOT NULL,
 phone_number                               NUMBER(15)                                   CONSTRAINT applications_nn_phone_number    NOT NULL,
 branch_id$                                 &pk_type,
 cipher_suite                               &cipher_suite_type,
 manager                                    &user_type,
 manager_date                               DATE,
 last_name                                  VARCHAR2(128),
 first_name                                 VARCHAR2(128),
 birth_date                                 DATE,
 doc_date                                   DATE,
 doc_issuer                                 VARCHAR2(250),
 address                                    VARCHAR2(4000),
 middle_name                                VARCHAR2(128),
 tax_payer_id                               VARCHAR2(64),
 decline_reason                             VARCHAR2(1024),
 CONSTRAINT applications_pk_id$             PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX applications_ux_id$ ON applications(id$) TABLESPACE app_i),
 CONSTRAINT applications_fk_country_id$     FOREIGN KEY(country_id$)                     REFERENCES countries(id$),
 CONSTRAINT applications_fk_doc_type_id$    FOREIGN KEY(doc_type_id$)                    REFERENCES doc_types(id$),
 CONSTRAINT applications_fk_branch_id$      FOREIGN KEY(branch_id$)                      REFERENCES branches(id$),
 CONSTRAINT applications_ck_status          CHECK(status IN('W', 'I', 'P', 'A', 'D')),
 CONSTRAINT applications_ck_phone_number    CHECK(phone_number > 0)
)TABLESPACE app_d;

--                                          *                     *                      *                                          *
CREATE TABLE app_accounts(
 id$                                        &pk_type                                     CONSTRAINT app_accounts_nn_id$             NOT NULL,
 application_id$                            &pk_type                                     CONSTRAINT app_accounts_nn_app_id$         NOT NULL,
 kind                                       VARCHAR2(1)                                  CONSTRAINT app_accounts_nn_kind            NOT NULL,
 interface                                  &ifc_type                                    CONSTRAINT app_accounts_nn_interface       NOT NULL,
 a_number                                   &acc_type                                    CONSTRAINT app_accounts_nn_a_number        NOT NULL,
 name                                       &acc_name_type                               CONSTRAINT app_accounts_nn_name            NOT NULL,
 priority                                   NUMBER(3),
 single_amount                              &amount_type,
 single_currency                            VARCHAR2(3),
 day_amount                                 &amount_type,
 day_currency                               VARCHAR2(3),
 day_quantity                               NUMBER(5),
 month_amount                               &amount_type,
 month_currency                             VARCHAR2(3),
 month_quantity                             NUMBER(5),
 from_hour                                  NUMBER(2),
 from_min                                   NUMBER(2),
 to_hour                                    NUMBER(2),
 to_min                                     NUMBER(2),
 CONSTRAINT app_accounts_pk_id$             PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX app_accounts_ux_id$ ON app_accounts(id$) TABLESPACE app_i),
 CONSTRAINT app_accounts_uk_akin            UNIQUE(application_id$, kind, interface, a_number)
																						USING INDEX (CREATE UNIQUE INDEX app_accounts_ux_akin ON app_accounts(application_id$, kind, interface, a_number) TABLESPACE app_i),
 CONSTRAINT app_accounts_uk_akn             UNIQUE(application_id$, kind, name)
																						USING INDEX (CREATE UNIQUE INDEX app_accounts_ux_akn ON app_accounts(application_id$, kind, name) TABLESPACE app_i),
 CONSTRAINT app_accounts_fk_app_id$         FOREIGN KEY(application_id$)                 REFERENCES applications(id$),
 CONSTRAINT app_accounts_ck_kind            CHECK(kind IN('S', 'R')),
 CONSTRAINT app_accounts_ck_single_amount   CHECK(single_amount >= 0),
 CONSTRAINT app_accounts_ck_single_curr     CHECK((single_amount IS NOT NULL AND single_currency IS NOT NULL) OR (single_amount IS NULL AND single_currency IS NULL)),
 CONSTRAINT app_accounts_ck_day_amount      CHECK(day_amount > 0 AND day_amount >= single_amount),
 CONSTRAINT app_accounts_ck_day_currency    CHECK((day_amount IS NOT NULL AND day_currency IS NOT NULL) OR (day_amount IS NULL AND day_currency IS NULL)),
 CONSTRAINT app_accounts_ck_day_quantity    CHECK(day_quantity BETWEEN 1 AND 65535),
 CONSTRAINT app_accounts_ck_month_amount    CHECK(month_amount > 0 AND month_amount >= day_amount),
 CONSTRAINT app_accounts_ck_month_currency  CHECK((month_amount IS NOT NULL AND month_currency IS NOT NULL) OR (month_amount IS NULL AND month_currency IS NULL)),
 CONSTRAINT app_accounts_ck_month_quantity  CHECK((month_quantity BETWEEN 1 AND 65535) AND month_quantity >= day_quantity),
 CONSTRAINT app_accounts_ck_from_hour       CHECK(from_hour >= 0 AND from_hour < 24),
 CONSTRAINT app_accounts_ck_from_min        CHECK(from_min >= 0 AND from_min < 60),
 CONSTRAINT app_accounts_ck_to_hour         CHECK(to_hour >= 0 AND to_hour < 24),
 CONSTRAINT app_accounts_ck_to_min          CHECK(to_min >= 0 AND to_min < 60),
 CONSTRAINT app_accounts_ck_from_to         CHECK(to_hour > from_hour OR (to_hour = from_hour AND to_min >= from_min))
)TABLESPACE app_d;

--                                          *                     *                      *                                          *
CREATE TABLE clients(
 id$                                        &pk_type                                     CONSTRAINT clients_nn_id$                  NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT clients_nn_status$              NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT clients_nn_date$                NOT NULL,
 user$                                      &user_type                                   CONSTRAINT clients_nn_user$                NOT NULL,
 country_id$                                &pk_type                                     CONSTRAINT clients_nn_country_id$          NOT NULL,
 doc_type_id$                               &pk_type                                     CONSTRAINT clients_nn_doc_type_id$         NOT NULL,
 doc_id                                     VARCHAR2(64)                                 CONSTRAINT clients_nn_doc_id               NOT NULL,
 from_date                                  DATE                  DEFAULT SYSDATE        CONSTRAINT clients_nn_from_date            NOT NULL,
 branch_id$                                 &pk_type,
 last_name                                  VARCHAR2(128),
 first_name                                 VARCHAR2(128),
 birth_date                                 DATE,
 doc_date                                   DATE,
 doc_issuer                                 VARCHAR2(250),
 address                                    VARCHAR2(4000),
 middle_name                                VARCHAR2(128),
 tax_payer_id                               VARCHAR2(64),
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT clients_pk_id$                  PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX clients_ux_id$ ON clients(id$) TABLESPACE contr_i),
 CONSTRAINT clients_uk_country_doc          UNIQUE(country_id$, doc_type_id$, doc_id, hu$)
																						USING INDEX (CREATE UNIQUE INDEX clients_ux_country_doc ON clients(country_id$, doc_type_id$, doc_id, hu$) TABLESPACE contr_i),
 CONSTRAINT clients_fk_cid$                 FOREIGN KEY(cid$)                            REFERENCES clients(id$),
 CONSTRAINT clients_fk_country_id$          FOREIGN KEY(country_id$)                     REFERENCES countries(id$),
 CONSTRAINT clients_fk_doc_type_id$         FOREIGN KEY(doc_type_id$)                    REFERENCES doc_types(id$),
 CONSTRAINT clients_fk_branch_id$           FOREIGN KEY(branch_id$)                      REFERENCES branches(id$),
 CONSTRAINT clients_ck_status$              CHECK(status$ IN('C', 'D', 'H'))
)TABLESPACE contr_d;

--                                          *                     *                      *                                          *
CREATE TABLE contracts(
 id$                                        &pk_type                                     CONSTRAINT contracts_nn_id$                NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT contracts_nn_status$            NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT contracts_nn_date$              NOT NULL,
 user$                                      &user_type                                   CONSTRAINT contracts_nn_user$              NOT NULL,
 client_id$                                 &pk_type                                     CONSTRAINT contracts_nn_client_id$         NOT NULL,
 si                                         &pk_type                                     CONSTRAINT contracts_nn_si                 NOT NULL,
 status                                     VARCHAR2(1)           DEFAULT 'K'            CONSTRAINT contracts_nn_status             NOT NULL,
 open_date                                  DATE                  DEFAULT SYSDATE        CONSTRAINT contracts_nn_open_date          NOT NULL,
 applied_at                                 DATE                                         CONSTRAINT contracts_nn_applied_at         NOT NULL,
 phone_number                               NUMBER(15)                                   CONSTRAINT contracts_nn_phone_number       NOT NULL,
 sc                                         NUMBER(5)             DEFAULT 0              CONSTRAINT contracts_nn_sc                 NOT NULL,
 password_tries                             NUMBER(1)             DEFAULT 0              CONSTRAINT contracts_nn_password_tries     NOT NULL,
 cipher_suite                               &cipher_suite_type                           CONSTRAINT contracts_nn_cipher_suite       NOT NULL,
 close_date                                 DATE,
 a_number                                   VARCHAR2(128),
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT contracts_pk_id$                PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX contracts_ux_id$ ON contracts(id$) TABLESPACE contr_i),
 CONSTRAINT contracts_fk_cid$               FOREIGN KEY(cid$)                            REFERENCES contracts(id$),
 CONSTRAINT contracts_fk_client_id$         FOREIGN KEY(client_id$)                      REFERENCES clients(id$),
 CONSTRAINT contracts_ck_status$            CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT contracts_ck_status             CHECK(status IN ('K', 'A', 'B', 'C')),
 CONSTRAINT contracts_ck_phone_number       CHECK(phone_number > 0),
 CONSTRAINT contracts_ck_sc                 CHECK(sc BETWEEN 0 AND 65535),
 CONSTRAINT contracts_ck_password_tries     CHECK(password_tries >= 0),
 CONSTRAINT contracts_ck_close_date         CHECK(close_date > open_date)
)TABLESPACE contr_d;

--                                          *                     *                      *                                          *
CREATE TABLE accounts(
 id$                                        &pk_type                                     CONSTRAINT accounts_nn_id$                 NOT NULL,
 status$                                    VARCHAR2(1)           DEFAULT 'C'            CONSTRAINT accounts_nn_status$             NOT NULL,
 date$                                      DATE                  DEFAULT SYSDATE        CONSTRAINT accounts_nn_date$               NOT NULL,
 user$                                      &user_type                                   CONSTRAINT accounts_nn_user$               NOT NULL,
 contract_id$                               &pk_type                                     CONSTRAINT accounts_nn_contract_id$        NOT NULL,
 kind                                       VARCHAR2(1)                                  CONSTRAINT accounts_nn_kind                NOT NULL,
 interface                                  &ifc_type                                    CONSTRAINT accounts_nn_interface           NOT NULL,
 a_number                                   &acc_type                                    CONSTRAINT accounts_nn_a_number            NOT NULL,
 name                                       &acc_name_type                               CONSTRAINT accounts_nn_name                NOT NULL,
 priority                                   NUMBER(3)             DEFAULT 0              CONSTRAINT accounts_nn_priority            NOT NULL,
 single_amount                              &amount_type,
 single_currency                            VARCHAR2(3),
 day_amount                                 &amount_type,
 day_total_amount                           &amount_type,
 day_currency                               VARCHAR2(3),
 day_quantity                               NUMBER(5),
 day_total_quantity                         NUMBER(5),
 day_date                                   DATE,
 month_amount                               &amount_type,
 month_total_amount                         &amount_type,
 month_currency                             VARCHAR2(3),
 month_quantity                             NUMBER(5),
 month_total_quantity                       NUMBER(5),
 month_date                                 DATE,
 from_hour                                  NUMBER(2),
 from_min                                   NUMBER(2),
 to_hour                                    NUMBER(2),
 to_min                                     NUMBER(2),
 hu$                                        &pk_type,
 cid$                                       &pk_type,
 CONSTRAINT accounts_pk_id$                 PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX accounts_ux_id$ ON accounts(id$) TABLESPACE contr_i),
 CONSTRAINT accounts_uk_ckin                UNIQUE(contract_id$, kind, interface, a_number, hu$)
																						USING INDEX (CREATE UNIQUE INDEX accounts_ux_ckin ON accounts(contract_id$, kind, interface, a_number, hu$) TABLESPACE contr_i),
 CONSTRAINT accounts_uk_ckn                 UNIQUE(contract_id$, kind, name, hu$)
																						USING INDEX (CREATE UNIQUE INDEX accounts_ux_ckn ON accounts(contract_id$, kind, name, hu$) TABLESPACE contr_i),
 CONSTRAINT accounts_fk_cid$                FOREIGN KEY(cid$)                            REFERENCES accounts(id$),
 CONSTRAINT accounts_fk_contract_id$        FOREIGN KEY(contract_id$)                    REFERENCES contracts(id$),
 CONSTRAINT accounts_ck_status$             CHECK(status$ IN('C', 'D', 'H')),
 CONSTRAINT accounts_ck_kind                CHECK(kind IN('S', 'R')),
 CONSTRAINT accounts_ck_single_amount       CHECK(single_amount >= 0),
 CONSTRAINT accounts_ck_single_currency     CHECK((single_amount IS NOT NULL AND single_currency IS NOT NULL) OR (single_amount IS NULL AND single_currency IS NULL)),
 CONSTRAINT accounts_ck_day_amount          CHECK(day_amount > 0 AND day_amount >= single_amount),
 CONSTRAINT accounts_ck_day_currency        CHECK((day_amount IS NOT NULL AND day_currency IS NOT NULL) OR (day_amount IS NULL AND day_currency IS NULL)),
 CONSTRAINT accounts_ck_day_tot_amnt        CHECK(day_total_amount >= 0 AND day_total_amount <= day_amount),
 CONSTRAINT accounts_ck_day_quantity        CHECK(day_quantity BETWEEN 1 AND 65535),
 CONSTRAINT accounts_ck_day_tot_qty         CHECK(day_total_quantity >= 0 AND day_total_quantity <= day_quantity),
 CONSTRAINT accounts_ck_month_amount        CHECK(month_amount > 0 AND month_amount >= day_amount),
 CONSTRAINT accounts_ck_month_currency      CHECK((month_amount IS NOT NULL AND month_currency IS NOT NULL) OR (month_amount IS NULL AND month_currency IS NULL)),
 CONSTRAINT accounts_ck_month_tot_amnt      CHECK(month_total_amount >= 0 AND month_total_amount <= month_amount),
 CONSTRAINT accounts_ck_month_quantity      CHECK((month_quantity BETWEEN 1 AND 65535) AND month_quantity >= day_quantity),
 CONSTRAINT accounts_ck_month_tot_qty       CHECK(month_total_quantity >= 0 AND month_total_quantity <= month_quantity),
 CONSTRAINT accounts_ck_from_hour           CHECK(from_hour >= 0 AND from_hour < 24),
 CONSTRAINT accounts_ck_from_min            CHECK(from_min >= 0 AND from_min < 60),
 CONSTRAINT accounts_ck_to_hour             CHECK(to_hour >= 0 AND to_hour < 24),
 CONSTRAINT accounts_ck_to_min              CHECK(to_min >= 0 AND to_min < 60),
 CONSTRAINT accounts_ck_from_to             CHECK(to_hour > from_hour OR (to_hour = from_hour AND to_min >= from_min))
)TABLESPACE contr_d;

--                                          *                     *                      *                                          *
CREATE TABLE srs(
 id$                                        &pk_type                                     CONSTRAINT srs_nn_id$                      NOT NULL,
 status                                     VARCHAR2(1)           DEFAULT 'A'            CONSTRAINT srs_nn_status                   NOT NULL,
 a_date                                     DATE                  DEFAULT SYSDATE        CONSTRAINT srs_nn_a_date                   NOT NULL,
 phone_number                               NUMBER(15)                                   CONSTRAINT srs_nn_phone_number             NOT NULL,
 csi                                        &pk_type                                     CONSTRAINT srs_nn_csi                      NOT NULL,
 cipher_suite                               &cipher_suite_type,
 init_interface                             &ifc_type,
 type_code                                  &mc_type,
 contract_id$                               &pk_type,
 sr_type_id$                                &pk_type,
 rmki                                       NUMBER(3),
 sai                                        NUMBER(3),
 sc                                         NUMBER(5),
 sms_sent                                   VARCHAR2(1),
 status_detail                              NUMBER(3),
 src_name                                   &acc_name_type,
 src_interface                              &ifc_type,
 src_account                                &acc_type,
 dst_name                                   &acc_name_type,
 dst_interface                              &ifc_type,
 dst_account                                &acc_type,
 amount                                     &amount_type,
 currency                                   VARCHAR2(3),
 ptc                                        VARCHAR2(250),
 description                                VARCHAR2(4000),
 st_type                                    NUMBER(3),
 data                                       BLOB,
 rn                                         BLOB,
 sn                                         BLOB,
 CONSTRAINT srs_pk_id$                      PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX srs_ux_id$ ON srs(id$) TABLESPACE sr_i),
 CONSTRAINT srs_fk_contract_id$             FOREIGN KEY(contract_id$)                    REFERENCES contracts(id$),
 CONSTRAINT srs_fk_sr_type_id$              FOREIGN KEY(sr_type_id$)                     REFERENCES sr_types(id$),
 CONSTRAINT srs_ck_status                   CHECK(status IN('A', 'S', 'R', 'D', 'T', 'E', 'P', 'N', 'C')),
 CONSTRAINT srs_ck_phone_number             CHECK(phone_number > 0),
 CONSTRAINT srs_ck_rmki                     CHECK(rmki BETWEEN 0 AND 255),
 CONSTRAINT srs_ck_sai                      CHECK(sai BETWEEN 0 AND 255),
 CONSTRAINT srs_ck_sc                       CHECK(sc BETWEEN 1 AND 65535),
 CONSTRAINT srs_ck_amount                   CHECK(amount > 0 AND amount < 10000000000),
 CONSTRAINT srs_ck_sms_sent                 CHECK(sms_sent IN ('Y', 'N')),
 CONSTRAINT srs_ck_st_type                  CHECK(st_type BETWEEN 0 AND 255),
 CONSTRAINT srs_ck_status_detail            CHECK(status_detail BETWEEN 0 AND 229)
)TABLESPACE sr_d LOB (data, rn, sn) STORE AS (PCTVERSION 0);

--                                          *                     *                      *                                          *
CREATE TABLE active_srs(
 id$                                        &pk_type                                     CONSTRAINT active_srs_nn_id$               NOT NULL,
 lag                                        NUMBER(3)              DEFAULT 0,
 cancelled                                  VARCHAR2(1),
 send_ntf                                   VARCHAR2(1),
 CONSTRAINT active_srs_pk_id$               PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX active_srs_ux_id$ ON active_srs(id$) TABLESPACE sr_i),
 CONSTRAINT active_srs_fk_id$               FOREIGN KEY(id$)                             REFERENCES srs(id$),
 CONSTRAINT active_srs_ck_cancelled         CHECK(cancelled = 'Y'),
 CONSTRAINT active_srs_ck_send_ntf          CHECK(send_ntf = 'Y')
)TABLESPACE sr_d;

--                                          *                     *                      *                                          *
CREATE TABLE messages(
 id$                                        NUMBER(38)                                   CONSTRAINT messages_nn_id$                 NOT NULL,
 sr_id$                                     &pk_type                                     CONSTRAINT messages_nn_sr_id$              NOT NULL,
 interface                                  &ifc_type                                    CONSTRAINT messages_nn_interface           NOT NULL,
 direction                                  VARCHAR2(1)                                  CONSTRAINT messages_nn_direction           NOT NULL,
 a_date                                     DATE                  DEFAULT SYSDATE        CONSTRAINT messages_nn_a_date              NOT NULL,
 data                                       BLOB                                         CONSTRAINT messages_nn_data                NOT NULL,
 CONSTRAINT messages_pk_id$                 PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX messages_ux_id$ ON messages(id$) TABLESPACE msg_i),
 CONSTRAINT messages_fk_sr_id$              FOREIGN KEY(sr_id$)                          REFERENCES srs(id$),
 CONSTRAINT messages_ck_direction           CHECK(direction IN('S', 'R'))
)TABLESPACE msg_d LOB (data) STORE AS (PCTVERSION 0);

--                                          *                     *                      *                                          *
CREATE TABLE system_log(
 id$                                        &pk_type                                     CONSTRAINT system_log_nn_id$               NOT NULL,
 occurred_at                                DATE                  DEFAULT SYSDATE        CONSTRAINT system_log_nn_occurred_at       NOT NULL,
 code                                       VARCHAR2(16)                                 CONSTRAINT system_log_nn_code              NOT NULL,
 description                                VARCHAR2(4000),
 sr_id$                                     &pk_type,
-- contract_id$                               &pk_type,
 user$                                      &user_type,
 CONSTRAINT system_log_pk_id$               PRIMARY KEY(id$)
																						USING INDEX (CREATE UNIQUE INDEX system_log_ux_id$ ON system_log(id$) TABLESPACE misc_i),
 CONSTRAINT system_log_fk_sr_id$            FOREIGN KEY(sr_id$)                          REFERENCES srs(id$)--,
-- CONSTRAINT system_log_fk_contract_id$      FOREIGN KEY(contract_id$)                    REFERENCES contracts(id$)
)TABLESPACE misc_d;

SPOOL OFF
