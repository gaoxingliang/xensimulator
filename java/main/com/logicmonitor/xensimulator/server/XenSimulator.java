package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.server.api.*;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.StringSerializer;
import org.apache.xmlrpc.serializer.TypeSerializer;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.xml.sax.SAXException;

import java.io.IOException;

public class XenSimulator {

    static final Logger LOG = LogManager.getLogger();

    public final int httpsPort, httpPort;
    private WebServer httpsWebServer;
    private WebServer httpWebServer;

    public XenSimulator(int httpsPort, String user, String pass) {
        this(httpsPort, 0, user, pass);
    }

    /**
     * New a xen simulator
     * @param httpsPort the https port. if <=0, will not start https
     * @param httpPort the http port. if <=0, will not start http
     * @param user
     * @param pass
     */
    public XenSimulator(int httpsPort, int httpPort, String user, String pass) {
        this.httpsPort = httpsPort;
        this.httpPort = httpPort;
        session.pass = pass;
        session.user = user;
        if (httpsPort <= 0 && httpPort<=0) {
            throw new IllegalArgumentException("Must set at least http port or https port");
        }
    }

    public void start() throws IOException {

        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        try {
            phm.addHandler("session", session.class);
            phm.addHandler("host", host.class);
            phm.addHandler("host_cpu", host_cpu.class);
            phm.addHandler("PIF", PIF.class);
            phm.addHandler("VM", VM.class);
            phm.addHandler("VBD", VBD.class);
            phm.addHandler("VIF", VIF.class);
            phm.addHandler("SR", SR.class);
            phm.addHandler("pool", pool.class);
            phm.addHandler("host_metrics", host_metrics.class);
            phm.addHandler("VM_metrics", VM_metrics.class);


            phm.addHandler(XenSimulatorSettings.class.getName(), XenSimulatorSettings.class);
        }
        catch (XmlRpcException e) {
            throw new IllegalStateException("Fail to init", e);
        }

        if (httpsPort > 0) {

            httpsWebServer = new ExtendWebServer(httpsPort, true);

            configServer(httpsWebServer, phm);
            httpsWebServer.start();
            LOG.info("simulator started at port={}", httpsPort);
        }
        if (httpPort > 0) {
            httpWebServer = new ExtendWebServer(httpPort, false);
            configServer(httpWebServer, phm);
            httpWebServer.start();
        }
    }

    private void configServer(WebServer webserver, PropertyHandlerMapping phm) {
        webserver.getXmlRpcServer().setTypeFactory(new TypeFactoryImpl(webserver.getXmlRpcServer()) {
            @Override
            public TypeSerializer getSerializer(XmlRpcStreamConfig pConfig, Object pObject) throws SAXException {
                if (pObject instanceof Long) {
                    // if this is a long value, re-map into a String
                    // because if not, it will use the org.apache.xmlrpc.serializer.I8Serializer
                    // and got a string like:
                    //                                      <name>memory_free</name>
                    //                                    <value>
                    //                                        <ex:i8>104857600</ex:i8>
                    //                                    </value>
                    // this will not work for xen api
                    return new StringSerializer();
                }
                return super.getSerializer(pConfig, pObject);
            }
        });

        XmlRpcServer rpcServer = webserver.getXmlRpcServer();
        rpcServer.setHandlerMapping(phm);
        XmlRpcServerConfigImpl serverConfig =
                (XmlRpcServerConfigImpl) rpcServer.getConfig();
        serverConfig.setKeepAliveEnabled(true);
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);
    }

    public void stop() {
        if (httpsWebServer != null) {
            httpsWebServer.shutdown();
        }
        if (httpWebServer != null) {
            httpWebServer.shutdown();
        }
    }
}
