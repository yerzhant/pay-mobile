/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Triggers for generation of primary keys' values.
 *
 * 2010.04.08 Yerzhan Tulepov         Created.
 */

SPOOL Logs\5_Triggers.log

CREATE OR REPLACE TRIGGER branches_tr
BEFORE INSERT ON branches
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT branches_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END branches;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER doc_types_tr
BEFORE INSERT ON doc_types
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT doc_types_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END doc_types;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER countries_tr
BEFORE INSERT ON countries
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT countries_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END countries;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER currencies_tr
BEFORE INSERT ON currencies
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT currencies_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END currencies;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER users_tr
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT users_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END users;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER users_branches_tr
BEFORE INSERT ON users_branches
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT users_branches_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END users_branches;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER status_details_tr
BEFORE INSERT ON status_details
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT status_details_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END status_details;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER global_params_tr
BEFORE INSERT ON global_params
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT global_params_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END global_params;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER interfaces_tr
BEFORE INSERT ON interfaces
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT interfaces_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END interfaces;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER sr_types_tr
BEFORE INSERT ON sr_types
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT sr_types_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END sr_types;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER sr_type_codes_tr
BEFORE INSERT ON sr_type_codes
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT sr_type_codes_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END sr_type_codes;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER fx_rates_tr
BEFORE INSERT ON fx_rates
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT fx_rates_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END fx_rates;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER cipher_suites_tr
BEFORE INSERT ON cipher_suites
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT cipher_suites_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END cipher_suites;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER applications_tr
BEFORE INSERT ON applications
FOR EACH ROW
BEGIN
  SELECT applications_seq.NEXTVAL INTO :NEW.id$ FROM dual;
END applications;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER app_accounts_tr
BEFORE INSERT ON app_accounts
FOR EACH ROW
BEGIN
  SELECT app_accounts_seq.NEXTVAL INTO :NEW.id$ FROM dual;
END app_accounts;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER clients_tr
BEFORE INSERT ON clients
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT clients_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END clients;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER contracts_tr
BEFORE INSERT ON contracts
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT contracts_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END contracts;
/
SHOW ERRORS

CREATE OR REPLACE TRIGGER accounts_tr
BEFORE INSERT ON accounts
FOR EACH ROW
BEGIN
  IF :NEW.id$ IS NULL THEN
    SELECT accounts_seq.NEXTVAL INTO :NEW.id$ FROM dual;
  END IF;
END accounts;
/
SHOW ERRORS

SPOOL OFF
