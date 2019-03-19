/*
 * Copyright (C) 2010 Yerzhan Tulepov. All rights reserved.
 *
 * Locks pay_mobile account.
 *
 * 2010.04.07 Yerzhan Tulepov         Created.
 */

SET ECHO OFF
SPOOL Logs\11_Lock_Pay_Mobile.log

ALTER USER pay_mobile ACCOUNT LOCK;

SPOOL OFF
EXIT
