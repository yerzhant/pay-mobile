/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Creates pay_mobile schema objects.
 *
 * 2010.04.07 Yerzhan Tulepov         Created.
 */

SET ECHO OFF
SET VERIFY OFF

ALTER SESSION SET PLSQL_WARNINGS='ENABLE:ALL';

@@Tables
@@Sequences
@@Init_Tables
@@Triggers
@@Indexes
@@Views
@@Packages
--ACCEPT pause PROMPT "Paused"
--@@Procedures
@@Grants

EXIT
