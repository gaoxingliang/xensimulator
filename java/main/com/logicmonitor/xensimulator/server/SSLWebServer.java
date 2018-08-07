package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.utils.SimulatorSettings;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.server.XmlRpcStreamServer;
import org.apache.xmlrpc.util.ThreadPool;
import org.apache.xmlrpc.webserver.Connection;
import org.apache.xmlrpc.webserver.RequestData;
import org.apache.xmlrpc.webserver.WebServer;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class SSLWebServer extends WebServer {
    public static final Logger LOG = LogManager.getLogger();


    public SSLWebServer(int port) {
        super(port);
    }

    @Override
    protected ServerSocket createServerSocket(int port, int backlog, InetAddress addr) throws IOException {
        try {

            /**
             * let's initialize the keystore
             */

            InputStream keyIs = null;
            KeyStore ks = null;

            keyIs = SSLWebServer.class.getResourceAsStream(SimulatorSettings.keystoreFile);

            ks = KeyStore.getInstance("JKS");
            ks.load(keyIs, SimulatorSettings.keystorePass);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, SimulatorSettings.keystorePass);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            ServerSocketFactory ssocketFactory = sslContext.getServerSocketFactory();
            ServerSocket sslsocket = ssocketFactory.createServerSocket(port);
            LOG.info("Server opened on {} with backlog {}", port, backlog);
            return sslsocket;
        }
        catch (Exception e) {
            LOG.error("Fail to create ssl socket", e);
            throw new IOException("Fail to create ssl socket on port " + port, e);
        }
    }


    protected ThreadPool.Task newTask(WebServer pServer, XmlRpcStreamServer pXmlRpcServer,
                                      Socket pSocket) throws IOException {
        return new GetRequestSupportedConnecttion(pServer, pXmlRpcServer, pSocket);
    }


    static class GetRequestSupportedConnecttion extends Connection {

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

        @Override
        public void writeResponse(RequestData pData, OutputStream pBuffer) throws IOException {
            super.writeResponse(pData, pBuffer);

        }

        @Override
        public void writeErrorHeader(RequestData pData, Throwable pError, int pContentLength) throws IOException {
            if (pError.getClass().getCanonicalName().equals("org.apache.xmlrpc.webserver.Connection.BadRequestException")) {
                try {
                    // if this is a get request, will run into this
                    byte[] content = new byte[1024 * 4];
                    int len = IOUtils.read(SSLWebServer.class.getResourceAsStream("/getresponse.html"), content);
                    output.write(toHTTPBytes("HTTP/1.1"));
                    output.write(toHTTPBytes(" 200 OK"));
                    output.write(newline);
                    writeContentLengthHeader(len);
                    output.write(serverName);
                    output.write(newline);

                    output.write(newline);
                    output.write(content, 0, len);
                }
                catch (Exception e) {
                    LOG.error("error when processing get request", e);
                }
            }
            else {
                super.writeErrorHeader(pData, pError, pContentLength);
            }
        }

        private static final byte[] clength = toHTTPBytes("Content-Length: ");
        private static final byte[] newline = toHTTPBytes("\r\n");
        private static final byte[] serverName = toHTTPBytes("Server: Apache XML-RPC 1.0\r\n");

        private void writeContentLengthHeader(int pContentLength) throws IOException {
            if (pContentLength == -1) {
                return;
            }
            output.write(clength);
            output.write(toHTTPBytes(Integer.toString(pContentLength)));
            output.write(newline);
        }

        /**
         * Returns the US-ASCII encoded byte representation of text for
         * HTTP use (as per section 2.2 of RFC 2068).
         */
        static final byte[] toHTTPBytes(String text) {
            try {
                return text.getBytes("US-ASCII");
            }
            catch (UnsupportedEncodingException e) {
                throw new Error(e.getMessage() +
                        ": HTTP requires US-ASCII encoding");
            }
        }


    }
}
