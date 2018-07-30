A Xen Simulator demo

Support SSL 

###new a keystore file
```
keytool -genkey -keystore xensim.keystore -keyalg RSA
```

###run with the keystore file
```
-Djavax.net.ssl.keyStore=xensim.keystore -Djavax.net.ssl.keyStorePassword=123456
```