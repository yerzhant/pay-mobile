@echo off
keytool -importkeystore -v -srckeystore keystore.ks -srcstoretype jceks -srcstorepass qwerty -srcalias thawte_test -destkeystore export.ks -deststorepass qwerty
keytool -exportcert -v -alias thawte_test -file thawte_test.der -keystore keystore.ks -storetype jceks -storepass qwerty
keytool -exportcert -v -alias thawte_test_csint -file thawte_test_csint.der -keystore keystore.ks -storetype jceks -storepass qwerty
keytool -exportcert -v -alias thawte_test_cscross -file thawte_test_cscross.der -keystore keystore.ks -storetype jceks -storepass qwerty
pause
