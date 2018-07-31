import com.logicmonitor.xensimulator.client.Client;
import com.logicmonitor.xensimulator.server.XenSimulator;
import com.xensource.xenapi.*;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
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
        sc.init((KeyManager[]) null, trustAllCerts, (SecureRandom) null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        });


        Protocol easyhttps = new Protocol("https", new LMProtocolSocketFactory(sc.getSocketFactory()), 8080);
        Protocol.registerProtocol("https", easyhttps);

        new XenSimulator(8080, "test", "testpass").start();
        Client cl = new Client("https://127.0.0.1:8080");
        cl.request("host", "reg_datasource", new Object[]{"cpu0", 1.3D});
        cl.request("host", "add_obj", new Object[]{"host", "ThisIsATestHost"});
        System.out.println("Done....");
        Connection c = null;
        Session s = null;
        c = new Connection(new URL("https://127.0.0.1:8080/"));
        s = Session.loginWithPassword(c, "test", "testpass", APIVersion.latest().toString());
        System.out.println(com.xensource.xenapi.Host.getAllRecords(c));
        System.out.println(HostCpu.getAllRecords(c));
        System.out.println(PIF.getAllRecords(c));
        System.out.println(Pool.getAllRecords(c));
        Session.logout(c);
        System.exit(0);


    }

    static class LMProtocolSocketFactory implements ProtocolSocketFactory {
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

}
