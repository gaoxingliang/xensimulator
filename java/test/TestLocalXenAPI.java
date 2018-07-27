//import com.logicmonitor.xensimulator.server.Server;
//import com.logicmonitor.xensimulator.server.api.Host;
//import com.xensource.xenapi.Connection;
//import com.xensource.xenapi.Session;
//
//import java.net.URL;
//
//public class TestLocalXenAPI {
//    public static void main(String[] args) throws Exception {
//        new Server(8080, "test", "testpass").start();
//        Host.regDataSource("cpu0", 0.03D);
//        boolean local = true;
//        Connection c = null;
//        Session s = null;
//        if (local) {
//            c = new Connection(new URL("http://127.0.0.1:8080/"));
//            s = Session.loginWithPassword(c, "test", "testpass");
//        }
//        else {
//            c = new Connection(new URL("http://192.168.170.151"));
//            s = Session.loginWithPassword(c, "root", "123456");
//        }
//
//        System.out.println(s.getThisHost(c).queryDataSource(c, "cpu0"));
//
//        Session.logout(c);
//    }
//}
