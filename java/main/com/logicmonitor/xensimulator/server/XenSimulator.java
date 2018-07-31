package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.server.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;

public class XenSimulator {

    static final Logger LOG = LogManager.getLogger();

    public final int httpsPort;

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
        }
        catch (XmlRpcException e) {
            throw new IllegalStateException("Fail to init", e);
        }
        WebServer httpsWebServer = new SSLWebServer(httpsPort);
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
}
