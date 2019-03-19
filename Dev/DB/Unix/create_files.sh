#!/bin/sh

#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov. All rights reserved.
#
# Produce Step and Ini files.
#
# $ORACLE_BASE (see its value below) must not contain neither admin nor oradata.
# /u01/$DBNAME and /u02/$DBNAME must exist and be empty. They should be placed on seperate RAID-10 and RAID-1 arrays, respectively.
#
# History:
# 2010.04.02	Yerzhan Tulepov		Copied from PaySys and made changes applicable to PayMobile.
# 2008.02.09	Yerzhan Tulepov		Created.
#####################################################################

#============================ Parameters ============================
DBNAME=pm
DBDOMAIN=paysoft.kz

SYSPASSWORD=qwerty

ORACLE_BASE=/oracle/product/10.2.0
ORACLE_HOME=$ORACLE_BASE/db_1
DATADIR01=/u01/$DBNAME/data01
REDODIR01=/u02/$DBNAME/redo01
CTLFILE01=$DATADIR01/../control/control01.ctl
CTLFILE02=$REDODIR01/../control/control02.ctl
CTLFILE03=$ORACLE_BASE/admin/$DBNAME/control/control03.ctl

SGASIZE=1G

DBBLOCKSIZE=8192
SYSTEMSIZE=1G
SYSTEMNEXTSIZE=512M
SYSAUXSIZE=128M
SYSAUXNEXTSIZE=128M
TEMPSIZE=512M
TEMPNEXTSIZE=256M
UNDOSIZE=1G
UNDONEXTSIZE=512M
REDOSIZE=128M

SMALLSIZE=512M
MEDIUMSIZE=1G
LARGESIZE=2G
#====================================================================


#------------------------------ Step 1 ------------------------------
cat step_1-create_dir_struct.sh<<EOF
#!/bin/sh

#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov.
#
# Create directory structure and pwd file.
#####################################################################

DATA01=$ORACLE_BASE/oradata/$DBNAME/data01
REDO01=$ORACLE_BASE/oradata/$DBNAME/redo01
CTRL01=$ORACLE_BASE/oradata/$DBNAME/control/control01.ctl
CTRL02=$ORACLE_BASE/oradata/$DBNAME/control/control02.ctl
CTRL03=$ORACLE_BASE/oradata/$DBNAME/control/control03.ctl

mkdir $DATADIR01
mkdir $REDODIR01

cd $DATADIR01/..
mkdir control
cd $REDODIR01/..
mkdir control

mkdir $ORACLE_BASE/admin
mkdir $ORACLE_BASE/admin/$DBNAME
mkdir $ORACLE_BASE/admin/$DBNAME/control
mkdir $ORACLE_BASE/admin/$DBNAME/bdump
mkdir $ORACLE_BASE/admin/$DBNAME/udump

mkdir $ORACLE_BASE/oradata
mkdir $ORACLE_BASE/oradata/$DBNAME
mkdir $ORACLE_BASE/oradata/$DBNAME/control

ln -s $CTLFILE01 $CTRL01
ln -s $CTLFILE02 $CTRL02
ln -s $CTLFILE03 $CTRL03
ln -s $DATADIR01 $DATA01
ln -s $REDODIR01 $REDO01

$ORACLE_HOME/bin/orapwd file=$ORACLE_HOME/database/PWD$DBNAME.ora password=$SYSPASSWORD
EOF

#----------------------------- Init.Ora -----------------------------
cat init.ora<<EOF
#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov.
#
# Init.ora.
#####################################################################

BACKGROUND_DUMP_DEST='$ORACLE_BASE/admin/$DBNAME/bdump'

COMPATIBLE=10.2.0.1.0

CONTROL_FILES=(	'$CTRL01',
		'$CTRL02',
		'$CTRL03' )

DB_BLOCK_SIZE=$DBBLOCKSIZE
DB_DOMAIN=$DBDOMAIN
DB_NAME=$DBNAME

NLS_LANGUAGE=RUSSIAN
NLS_TERRITORY=KAZAKHSTAN

SGA_TARGET=$SGASIZE

SHARED_SERVERS=1

UNDO_MANAGEMENT=AUTO
UNDO_TABLESPACE=undo1

USER_DUMP_DEST='$ORACLE_BASE/admin/$DBNAME/udump'
EOF

#------------------------------ Step 2 ------------------------------
cat step_2-create_db.sh<<EOF
#!/bin/sh

#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov.
#
# Create database.
#####################################################################

ORACLE_SID=$DBNAME

$ORACLE_HOME/bin/sqlplus /nolog<<EOF
connect / as SYSDBA
set echo on
spool createDB.log
startup nomount pfile=init.ora;

CREATE DATABASE $DBNAME
	MAXINSTANCES 1
	MAXLOGHISTORY 1
	MAXLOGFILES 16
	MAXLOGMEMBERS 3
	MAXDATAFILES 128
	DATAFILE '$DATA01/system01.dbf' SIZE $SYSTEMSIZE AUTOEXTEND ON NEXT $SYSTEMNEXTSIZE MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL
	SYSAUX
	DATAFILE '$DATA01/sysaux01.dbf' SIZE $SYSAUXSIZE AUTOEXTEND ON NEXT $SYSAUXNEXTSIZE MAXSIZE UNLIMITED
	DEFAULT TEMPORARY TABLESPACE temp1
	TEMPFILE '$DATA01/temp01.dbf'   SIZE $TEMPSIZE   AUTOEXTEND ON NEXT $TEMPNETXSIZE   MAXSIZE UNLIMITED
	UNDO TABLESPACE undo1
	DATAFILE '$DATA01/undo01.dbf'   SIZE $UNDOSIZE   AUTOEXTEND ON NEXT $UNDONEXTSIZE   MAXSIZE UNLIMITED
	CHARACTER SET CL8MSWIN1251
	NATIONAL CHARACTER SET AL16UTF16
	SET TIME_ZONE = 'UTC'
	LOGFILE GROUP 1 ('$REDO01/redo01.log') SIZE $REDOSIZE,
		GROUP 2 ('$REDO01/redo02.log') SIZE $REDOSIZE,
		GROUP 3 ('$REDO01/redo03.log') SIZE $REDOSIZE
	USER SYS IDENTIFIED BY $SYSPASSWORD USER SYSTEM IDENTIFIED BY $SYSPASSWORD;

spool off
exit
EOF

#------------------------------ Step 3 ------------------------------
cat step_3-create_ts.sh<<EOF
#!/bin/sh

#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov.
#
# Create tablespaces.
#####################################################################

ORACLE_SID=$DBNAME

$ORACLE_HOME/bin/sqlplus /nolog<<EOF
connect / as SYSDBA
set echo on
spool createTS.log

CREATE TABLESPACE app_d   DATAFILE '$DATA01/app01_d.dbf'   SIZE $MEDIUMSIZE AUTOEXTEND ON NEXT $MEDIUMSIZE MAXSIZE UNLIMITED;
CREATE TABLESPACE app_i   DATAFILE '$DATA01/app01_i.dbf'   SIZE $MEDIUMSIZE AUTOEXTEND ON NEXT $MEDIUMSIZE MAXSIZE UNLIMITED;

CREATE TABLESPACE contr_d DATAFILE '$DATA01/contr01_d.dbf' SIZE $LARGESIZE  AUTOEXTEND ON NEXT $LARGESIZE  MAXSIZE UNLIMITED;
CREATE TABLESPACE contr_i DATAFILE '$DATA01/contr01_i.dbf' SIZE $LARGESIZE  AUTOEXTEND ON NEXT $LARGESIZE  MAXSIZE UNLIMITED;

CREATE TABLESPACE dic_d   DATAFILE '$DATA01/dic01_d.dbf'   SIZE $SMALLSIZE  AUTOEXTEND ON NEXT $SMALLSIZE  MAXSIZE UNLIMITED;
CREATE TABLESPACE dic_i   DATAFILE '$DATA01/dic01_i.dbf'   SIZE $SMALLSIZE  AUTOEXTEND ON NEXT $SMALLSIZE  MAXSIZE UNLIMITED;

CREATE TABLESPACE msg_d   DATAFILE '$DATA01/msg01_d.dbf'   SIZE $LARGESIZE  AUTOEXTEND ON NEXT $LARGESIZE  MAXSIZE UNLIMITED;
CREATE TABLESPACE msg_i   DATAFILE '$DATA01/msg01_i.dbf'   SIZE $LARGESIZE  AUTOEXTEND ON NEXT $LARGESIZE  MAXSIZE UNLIMITED;

CREATE TABLESPACE misc_d  DATAFILE '$DATA01/misc01_d.dbf'  SIZE $MEDIUMSIZE AUTOEXTEND ON NEXT $MEDIUMSIZE MAXSIZE UNLIMITED;
CREATE TABLESPACE misc_i  DATAFILE '$DATA01/misc01_i.dbf'  SIZE $MEDIUMSIZE AUTOEXTEND ON NEXT $MEDIUMSIZE MAXSIZE UNLIMITED;

CREATE TABLESPACE sr_d    DATAFILE '$DATA01/sr01_d.dbf'    SIZE $LARGESIZE  AUTOEXTEND ON NEXT $LARGESIZE  MAXSIZE UNLIMITED;
CREATE TABLESPACE sr_i    DATAFILE '$DATA01/sr01_i.dbf'    SIZE $LARGESIZE  AUTOEXTEND ON NEXT $LARGESIZE  MAXSIZE UNLIMITED;

CREATE TABLESPACE ofm     DATAFILE '$DATA01/ofm01.dbf'     SIZE $SMALLSIZE  AUTOEXTEND ON NEXT $SMALLSIZE  MAXSIZE UNLIMITED;

spool off
exit
EOF

#------------------------------ Step 4 ------------------------------
cat step_4-create_catalog.sh<<EOF
#!/bin/sh

#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov.
#
# Create catalog.
#####################################################################

ORACLE_SID=$DBNAME

$ORACLE_HOME/bin/sqlplus /nolog<<EOF
connect / as SYSDBA
set echo off
spool createCATALOG.log

@$ORACLE_HOME/rdbms/admin/catalog.sql
@$ORACLE_HOME/rdbms/admin/catproc.sql

spool off
exit
EOF

#------------------------------ Step 5 ------------------------------
cat step_5-postCreation.sh<<EOF
#!/bin/sh

#####################################################################
# Copyright (C) 2008-2010 by Yerzhan Tulepov.
#
# Post-creation procedures.
#####################################################################

ORACLE_SID=$DBNAME

$ORACLE_HOME/bin/sqlplus /nolog<<EOF
connect system/$SYSPASSWORD
set echo off
spool postCREATION.log

@$ORACLE_HOME/sqlplus/admin/pupbld.sql;

disconnect
connect / as SYSDBA

@$ORACLE_HOME/rdbms/admin/utlrp.sql;
CREATE SPFILE FROM PFILE='$PWD/init.ora';

SHUTDOWN;
STARTUP;

spool off
exit
EOF
