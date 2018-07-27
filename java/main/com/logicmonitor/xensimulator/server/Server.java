package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.server.api.host;
import com.logicmonitor.xensimulator.server.api.session;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;

public class Server {

    public int port;
    public Server(int port, String user, String pass) {
        this.port = port;
        session.pass = pass;
        session.user = user;
    }

    public void start() throws IOException {
        WebServer webServer = new WebServer(port);
        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        PropertyHandlerMapping phm = new PropertyHandlerMapping();

        try {
            phm.addHandler("session", session.class);
            phm.addHandler("host", host.class);
        }
        catch (XmlRpcException e) {
            throw new IllegalStateException("Fail to init", e);
        }

        xmlRpcServer.setHandlerMapping(phm);

        XmlRpcServerConfigImpl serverConfig =
                (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setKeepAliveEnabled(true);
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);
        webServer.start();
    }

}