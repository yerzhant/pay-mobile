/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Drop all the users.
 *
 * 2010.04.07 Yerzhan Tulepov         Created.
 */

SET ECHO OFF

DROP USER app_entry;
DROP USER sec_officer;
DROP USER setup;
DROP USER pm_client;
DROP USER front_server;
DROP USER smpp_server;
DROP USER interface;
DROP USER sr_controller;
DROP USER ofm_ps CASCADE;
DROP USER pay_mobile CASCADE;

EXIT
