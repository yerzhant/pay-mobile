/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Creation of users and assigning them necessary grants.
 * The script is to be run under system user.
 *
 * 2010.04.07 Yerzhan Tulepov         Created.
 */

SET ECHO OFF
SET VERIFY OFF
SPOOL Logs\1_Create_Users.log

DEFINE users_passwords = &1

CREATE USER pay_mobile IDENTIFIED BY &users_passwords
       DEFAULT TABLESPACE misc_d
       TEMPORARY TABLESPACE temp1
       QUOTA UNLIMITED ON app_d
       QUOTA UNLIMITED ON app_i
       QUOTA UNLIMITED ON contr_d
       QUOTA UNLIMITED ON contr_i
       QUOTA UNLIMITED ON dic_d
       QUOTA UNLIMITED ON dic_i
       QUOTA UNLIMITED ON msg_d
       QUOTA UNLIMITED ON msg_i
       QUOTA UNLIMITED ON misc_d
       QUOTA UNLIMITED ON misc_i
       QUOTA UNLIMITED ON sr_d
       QUOTA UNLIMITED ON sr_i;

CREATE USER app_entry     IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER sec_officer   IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER setup         IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER pm_client     IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER front_server  IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER smpp_server   IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER interface     IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER sr_controller IDENTIFIED BY &users_passwords DEFAULT TABLESPACE misc_d TEMPORARY TABLESPACE temp1;
CREATE USER ofm_ps        IDENTIFIED BY &users_passwords DEFAULT TABLESPACE ofm    TEMPORARY TABLESPACE temp1 QUOTA UNLIMITED ON ofm;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW, CREATE PROCEDURE, CREATE TRIGGER TO pay_mobile;
GRANT CREATE SESSION TO app_entry;
GRANT CREATE SESSION TO sec_officer;
GRANT CREATE SESSION TO setup;
GRANT CREATE SESSION TO pm_client;
GRANT CREATE SESSION TO front_server;
GRANT CREATE SESSION TO smpp_server;
GRANT CREATE SESSION TO interface;
GRANT CREATE SESSION TO sr_controller;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO ofm_ps;

SPOOL OFF
EXIT
