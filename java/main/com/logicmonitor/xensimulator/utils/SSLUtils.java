package com.logicmonitor.xensimulator.utils;

import org.apache.commons.httpclient.protocol.Protocol;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class SSLUtils {
    public static void ignoreSSL(int port) {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new TrustAllManager();
        trustAllCerts[0] = tm;

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((h, v) -> true);
        }
        catch (Exception e) {
            throw new IllegalStateException("Fail to init", e);
        }

        Protocol easyhttps = new Protocol("https", new LMProtocolSocketFactory(sc.getSocketFactory()), port);
        Protocol.registerProtocol("https", easyhttps);
    }

}
