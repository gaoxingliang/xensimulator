package com.logicmonitor.xensimulator.server;

import org.apache.xmlrpc.server.XmlRpcStreamServer;
import org.apache.xmlrpc.util.ThreadPool;
import org.apache.xmlrpc.webserver.Connection;
import org.apache.xmlrpc.webserver.RequestData;
import org.apache.xmlrpc.webserver.WebServer;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SSLWebServer extends WebServer {

    public boolean ssl = false;
    public SSLWebServer(int pPort) {
        super(pPort);
    }

    @Override
    protected ServerSocket createServerSocket(int pPort, int backlog, InetAddress addr) throws IOException {
        try {

            // -Djavax.net.ssl.keyStore=/Users/edward.gao/Downloads/xensim.keystore -Djavax.net.ssl.keyStorePassword=123456
//
        ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
            ServerSocket sslsocket = ssocketFactory.createServerSocket(pPort);
            return sslsocket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    protected ThreadPool.Task newTask(WebServer pServer, XmlRpcStreamServer pXmlRpcServer,
                                      Socket pSocket) throws IOException {
        return new GetRequestSupportedConnecttion(pServer, pXmlRpcServer, pSocket);
    }


    class GetRequestSupportedConnecttion extends Connection {

        protected OutputStream output;

        /**
         * Creates a new webserver connection on the given socket.
         *
         * @param pWebServer The webserver maintaining this connection.
         * @param pServer    The server being used to execute requests.
         * @param pSocket    The server socket to handle; the <code>Connection</code>
         *                   is responsible for closing this socket.
         * @throws IOException
         */
        public GetRequestSupportedConnecttion(WebServer pWebServer, XmlRpcStreamServer pServer, Socket pSocket) throws IOException {
            super(pWebServer, pServer, pSocket);

            try {
                Field outputField = Connection.class.getDeclaredField("output");
                outputField.setAccessible(true);
                output = (OutputStream) outputField.get(this);
            }
            catch (Exception e) {
                throw new IllegalStateException("Fail to access the out field", e);
            }
        }


        // private final OutputStream output;

        @Override
        public void writeErrorHeader(RequestData pData, Throwable pError, int pContentLength) throws IOException {
            if (pError.getClass().getCanonicalName().equals("org.apache.xmlrpc.webserver.Connection.BadRequestException")) {
                try {
                    final byte[] content = toHTTPBytes("Method " + pData.getMethod()
                            + " not implemented (try POST)\r\n");
                    output.write(toHTTPBytes("1.1"));
                    output.write(toHTTPBytes(" 200 OK"));
                    output.write(newline);
                    output.write(serverName);
                    writeContentLengthHeader(content.length);
                    output.write(newline);
                    output.write(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                super.writeErrorHeader(pData, pError, pContentLength);
            }
        }

        private final byte[] clength = toHTTPBytes("Content-Length: ");
        private final byte[] newline = toHTTPBytes("\r\n");
        private final byte[] serverName = toHTTPBytes("Server: Apache XML-RPC 1.0\r\n");

        private void writeContentLengthHeader(int pContentLength) throws IOException {
            if (pContentLength == -1) {
                return;
            }
            output.write(clength);
            output.write(toHTTPBytes(Integer.toString(pContentLength)));
            output.write(newline);
        }

        /** Returns the US-ASCII encoded byte representation of text for
         * HTTP use (as per section 2.2 of RFC 2068).
         */
        final byte[] toHTTPBytes(String text) {
            try {
                return text.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e.getMessage() +
                        ": HTTP requires US-ASCII encoding");
            }
        }


    }
}
