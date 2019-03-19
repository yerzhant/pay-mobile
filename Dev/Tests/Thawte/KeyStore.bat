@echo off
keytool -genkeypair -v -alias thawte_test -keyalg rsa -keysize 2048 -sigalg sha1withrsa -dname "CN=paysoft.dyndns-server.com, O=PaySoft\, LLP, L=Almaty, S=Almaty, C=KZ" -validity 365 -keystore keystore.ks -storetype jceks -storepass qwerty
keytool -genseckey -v -alias key_1 -keyalg hmacsha256 -keysize 256 -keystore keystore.ks -storetype jceks -storepass qwerty
rem keytool -genkeypair -v -alias test -keyalg rsa -keysize 2048 -sigalg sha1withrsa -dname "CN=192.168.1.54, O=PaySoft\, LLP, L=Almaty, C=KZ" -validity 180 -keystore keystore.ks
pause
