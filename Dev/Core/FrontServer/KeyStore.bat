@echo off
keytool -genkeypair -v -alias demo.paysoft.kz -keyalg rsa -keysize 2048 -sigalg sha1withrsa -dname "CN=demo.paysoft.kz, O=PaySoft\, LLP, L=Almaty, S=Almaty, C=KZ" -validity 730 -keystore keystore.ks -storetype jceks -storepass s0meth1ng#
keytool -genseckey -v -alias key_1 -keyalg hmacsha256 -keysize 256 -keystore keystore.ks -storetype jceks -storepass s0meth1ng#
copy keystore.ks keystore.orig
pause
