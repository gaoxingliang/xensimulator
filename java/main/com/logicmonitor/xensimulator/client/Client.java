package com.logicmonitor.xensimulator.client;

import com.logicmonitor.xensimulator.utils.SSLUtils;
import com.logicmonitor.xensimulator.utils.SimulatorSettings;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class Client {
    private final String url;

    public Client(String url) {
        this.url = url;
        try {
            URL urlObj = new URL(url);
            if (urlObj.getProtocol().equals("https") && SimulatorSettings.ignoreSSL) {
                SSLUtils.ignoreSSL(urlObj.getPort() > 0 ? urlObj.getPort() : 443);
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