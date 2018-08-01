package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.server.api.*;
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

    public final int httpsPort;
    private WebServer httpsWebServer;

    public XenSimulator(int httpsPort, String user, String pass) {
        this.httpsPort = httpsPort;
        session.pass = pass;
        session.user = user;
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
        }
        catch (XmlRpcException e) {
            throw new IllegalStateException("Fail to init", e);
        }
        httpsWebServer = new SSLWebServer(httpsPort);

        httpsWebServer.getXmlRpcServer().setTypeFactory(new TypeFactoryImpl(httpsWebServer.getXmlRpcServer()) {
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

        XmlRpcServer httpsXmlRpcServer = httpsWebServer.getXmlRpcServer();
        httpsXmlRpcServer.setHandlerMapping(phm);
        XmlRpcServerConfigImpl httpsServerConfig =
                (XmlRpcServerConfigImpl) httpsXmlRpcServer.getConfig();
        httpsServerConfig.setKeepAliveEnabled(true);
        httpsServerConfig.setEnabledForExtensions(true);
        httpsServerConfig.setContentLengthOptional(false);
        httpsWebServer.start();
        LOG.info("simulator started at port={}", httpsPort);

    }

    public void stop() {
        if (httpsWebServer != null) {
            httpsWebServer.shutdown();
        }
    }
}
