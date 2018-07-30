import com.logicmonitor.xensimulator.client.Client;
import com.logicmonitor.xensimulator.server.Server;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TestXenLogin {

    public static class SantabaTM extends X509ExtendedTrustManager implements TrustManager, X509TrustManager {
        public SantabaTM() {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        }
    }

    public static void main(String[] args) throws Exception {


        System.out.println(new File(".").getCanonicalPath());
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new SantabaTM();
        trustAllCerts[0] = tm;

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init((KeyManager[])null, trustAllCerts, (SecureRandom)null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        });


        Protocol easyhttps = new Protocol("https", new LMProtocolSocketFactory(sc.getSocketFactory()), 8080);
        Protocol.registerProtocol("https", easyhttps);

        new Server(8080, "test", "testpass").start();
        Client cl = new Client("https://127.0.0.1:8080");
        cl.request("host", "reg_datasource", new Object[]{"cpu0", 1.3D});
        cl.request("host", "add_obj", new Object[]{"host", "ThisIsATestHost"});
        System.out.println("Done....");
        boolean local = true;
        Connection c = null;
        Session s = null;
        if (local) {
            c = new Connection(new URL("https://127.0.0.1:8080/"));
            s = Session.loginWithPassword(c, "test", "testpass", APIVersion.latest().toString());
        }
        else {
            c = new Connection(new URL("http://192.168.170.151"));
            s = Session.loginWithPassword(c, "root", "123456", APIVersion.latest().toString());
        }

       // System.out.println(s.getThisHost(c).queryDataSource(c, "cpu0"));

//        System.out.println(s.getThisHost(c).queryDataSource(c, "cpu0"));

        System.out.println(com.xensource.xenapi.Host.getAll(c));

        //Session.logout(c);


    }
    static class LMProtocolSocketFactory implements ProtocolSocketFactory {
        SSLSocketFactory sf;
        public LMProtocolSocketFactory(SSLSocketFactory sf) {
            this.sf = sf;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
            return sf.createSocket(host, port, localAddress, localPort);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws
                IOException, UnknownHostException, ConnectTimeoutException {
            return sf.createSocket(host, port, localAddress, localPort);
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return sf.createSocket(host, port);
        }
    }

}
