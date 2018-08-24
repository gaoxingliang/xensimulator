package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import org.apache.commons.io.input.TeeInputStream;
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
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

/**
 * This webserver we extend the web server to support http get requests
 */
public class ExtendWebServer extends WebServer {
    public static final Logger LOG = LogManager.getLogger();

    private final boolean _ssl;

    public ExtendWebServer(int port, boolean ssl) {
        super(port);
        this._ssl = ssl;
    }

    @Override
    protected ServerSocket createServerSocket(int port, int backlog, InetAddress addr) throws IOException {
        try {
            if (_ssl) {
                /**
                 * let's initialize the keystore
                 */
                InputStream keyIs = null;
                KeyStore ks = null;

                keyIs = ExtendWebServer.class.getResourceAsStream(XenSimulatorSettings.keystoreFile);

                ks = KeyStore.getInstance("JKS");
                ks.load(keyIs, XenSimulatorSettings.keystorePass);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, XenSimulatorSettings.keystorePass);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ks);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                ServerSocketFactory ssocketFactory = sslContext.getServerSocketFactory();
                ServerSocket sslsocket = ssocketFactory.createServerSocket(port);
                LOG.info("Server opened on {} with backlog {}", port, backlog);
                return sslsocket;
            }
            else {
                return super.createServerSocket(port, backlog, addr);
            }
        }
        catch (Exception e) {
            LOG.error("Fail to create socket, ssl={}, port={}", _ssl, port, e);
            throw new IOException("Fail to create socket on port " + port, e);
        }
    }


    protected ThreadPool.Task newTask(WebServer pServer, XmlRpcStreamServer pXmlRpcServer,
                                      Socket pSocket) throws IOException {
        return new GetRequestSupportedConnection(pServer, pXmlRpcServer, pSocket);
    }


    static class GetRequestSupportedConnection extends Connection {

        protected OutputStream output;
        protected InputStream rawinput;

        // this will be used to record the response and request
        // when a request is finished, reset it!
        private ByteArrayOutputStream requestCopy;

        /**
         * Creates a new webserver connection on the given socket.
         *
         * @param pWebServer The webserver maintaining this connection.
         * @param pServer    The server being used to execute requests.
         * @param pSocket    The server socket to handle; the <code>Connection</code>
         *                   is responsible for closing this socket.
         * @throws IOException
         */
        public GetRequestSupportedConnection(WebServer pWebServer, XmlRpcStreamServer pServer, Socket pSocket) throws IOException {
            super(pWebServer, pServer, pSocket);

            try {
                Field outputField = Connection.class.getDeclaredField("output");
                outputField.setAccessible(true);
                output = (OutputStream) outputField.get(this);
            }
            catch (Exception e) {
                throw new IllegalStateException("Fail to access the out field", e);
            }

            try {
                Field inputField = Connection.class.getDeclaredField("input");
                inputField.setAccessible(true);
                rawinput = (InputStream) inputField.get(this);
                requestCopy = new ByteArrayOutputStream();
                TeeInputStream dup = new TeeInputStream(rawinput, requestCopy, true);
                inputField.set(this, dup);
            }
            catch (Exception e) {
                throw new IllegalStateException("Fail to access the input field", e);
            }
        }

        @Override
        public void writeResponse(RequestData pData, OutputStream pBuffer) throws IOException {
            ByteArrayOutputStream response = (ByteArrayOutputStream) pBuffer;
            LOG.debug("Request={}", requestCopy.toString());
            requestCopy.reset();
            LOG.debug("Success Response={}", response.toString());


            if (XenSimulatorSettings.responseDelayInMs > 0) {
                try {
                    Thread.sleep(XenSimulatorSettings.responseDelayInMs);
                }
                catch (InterruptedException e) {
                }
            }
            super.writeResponse(pData, pBuffer);
        }

        @Override
        public void writeErrorHeader(RequestData pData, Throwable pError, int pContentLength) throws IOException {
            if (pError instanceof GetRequestException) {

                try {
                    GetRequestException getRequestException = (GetRequestException)pError;
                    String uri = getRequestException.getUri();
                    writeGetRequestResponse(XenSimulatorSettings.getRequestHandler.processGetRequest(uri));
                }
                catch (Exception e) {
                    LOG.error("error when processing get request", e);
                }
            }
            else {
                super.writeErrorHeader(pData, pError, pContentLength);
            }
        }

        @Override
        public void writeError(RequestData pData, Throwable pError, ByteArrayOutputStream pStream) throws IOException {
            LOG.error("Request={}", requestCopy.toString());
            LOG.error("Error response={}", pStream.toString());
            requestCopy.reset();

            super.writeError(pData, pError, pStream);
        }

        private void writeGetRequestResponse(String response) throws IOException {
            output.write(toHTTPBytes("HTTP/1.1"));
            output.write(toHTTPBytes(" 200 OK"));
            output.write(newline);
            writeContentLengthHeader(response.getBytes().length);
            output.write(serverName);
            output.write(newline);
            output.write(newline);
            output.write(response.getBytes());
            output.flush();
        }


        private static final byte[] clength = toHTTPBytes("Content-Length: ");
        private static final byte[] newline = toHTTPBytes("\r\n");
        private static final byte[] serverName = toHTTPBytes("Server: Apache XML-RPC 1.0");

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
