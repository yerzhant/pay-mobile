@echo off
C:\oracle\product\10.2.0\db_1\bin\sqlplus -s system/qwerty@devpm @Scripts\Create_Users qwerty
C:\oracle\product\10.2.0\db_1\bin\sqlplus -s pay_mobile/qwerty@devpm @Scripts\Create_Objects
rem sqlplus -s system/qwerty@devpm @Scripts\Lock_Pay_Mobile
pause
