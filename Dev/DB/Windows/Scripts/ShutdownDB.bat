@echo off
C:\oracle\product\10.2.0\db_1\bin\oradim -shutdown -sid pm -shuttype srvc,inst
C:\oracle\product\10.2.0\db_1\bin\lsnrctl stop
pause
