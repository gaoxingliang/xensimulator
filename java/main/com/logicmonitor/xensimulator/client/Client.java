package com.logicmonitor.xensimulator.client;

import com.logicmonitor.xensimulator.utils.LMProtocolSocketFactory;
import com.logicmonitor.xensimulator.utils.SimulatorSettings;
import com.logicmonitor.xensimulator.utils.TrustAllManager;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.net.MalformedURLException;
import java.net.URL;

public class Client {
    private final String url;

    public Client(String url) {
        this.url = url;
        try {
            URL urlObj = new URL(url);
            if (urlObj.getProtocol().equals("https") && SimulatorSettings.ignoreSSL) {
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

                Protocol easyhttps = new Protocol("https", new LMProtocolSocketFactory(sc.getSocketFactory()), urlObj.getPort() > 0 ?
                        urlObj.getPort() : 443);
                Protocol.registerProtocol("https", easyhttps);
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url " + url, e);
        }

    }


    public Object request(String className, String method, Object[] params) throws Exception {
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(url));
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);
        XmlRpcClient client = new XmlRpcClient();

        // use Commons HttpClient as transport
        client.setTransportFactory(
                new RecordedXmlTransportFactory(client));
        // set configuration
        client.setConfig(config);

        // make the a regular call
        return client.execute(className + "." + method, params);
    }

    public static void main(String[] args) throws Exception {


    }
}