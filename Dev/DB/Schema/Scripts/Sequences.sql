/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Creation of sequences.
 *
 * 2010.04.08 Yerzhan Tulepov         Created.
 */

SPOOL Logs\3_Sequences.log

CREATE SEQUENCE branches_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE doc_types_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE countries_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE currencies_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE users_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE users_branches_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE status_details_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE global_params_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE interfaces_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE sr_types_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE sr_type_codes_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE fx_rates_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE cipher_suites_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE system_log_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE applications_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE app_accounts_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE clients_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE contracts_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE accounts_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE srs_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE &pk_maxvalue
       MINVALUE 1
       CYCLE;

CREATE SEQUENCE messages_seq
       INCREMENT BY 1
       START WITH 1
       MAXVALUE 9999999999999999999999999999
       MINVALUE 1
       CYCLE;

SPOOL OFF
