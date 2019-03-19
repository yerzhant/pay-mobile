@echo off
keytool -importkeystore -v -srckeystore keystore.ks -srcstoretype jceks -srcstorepass s0meth1ng# -srcalias demo.paysoft.kz -destkeystore export.ks -deststorepass s0meth1ng#
keytool -exportcert -v -alias demo.paysoft.kz -file demo.cer -keystore keystore.ks -storetype jceks -storepass s0meth1ng#
pause
