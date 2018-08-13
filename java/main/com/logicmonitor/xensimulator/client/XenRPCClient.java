package com.logicmonitor.xensimulator.client;

import com.logicmonitor.xensimulator.utils.SSLUtils;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * this is a client which will help you to set or update the simulator internal status by internal api.
 * @see com.logicmonitor.xensimulator.utils.SimAPI
 */
public class XenRPCClient {
    private final String url;

    public XenRPCClient(String url) {
        this.url = url;
        try {
            URL urlObj = new URL(url);
            if (urlObj.getProtocol().equals("https") && XenSimulatorSettings.ignoreSSL) {
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
        config.setConnectionTimeout(10 * 1000);
        config.setReplyTimeout(10 * 1000);
        XmlRpcClient client = new XmlRpcClient();

        // use Commons HttpClient as transport
        client.setTransportFactory(
                new RecordedXmlTransportFactory(client));
        // set configuration
        client.setConfig(config);

        // make the a regular call
        return client.execute(className + "." + method, params);
    }

}