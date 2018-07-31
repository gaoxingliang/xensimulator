package com.logicmonitor.xensimulator.utils;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class LMProtocolSocketFactory implements ProtocolSocketFactory {
        SSLSocketFactory sf;

        public LMProtocolSocketFactory(SSLSocketFactory sf) {
            this.sf = sf;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
            return sf.createSocket(host, port, localAddress, localPort);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws
                IOException {
            return sf.createSocket(host, port, localAddress, localPort);
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return sf.createSocket(host, port);
        }
    }