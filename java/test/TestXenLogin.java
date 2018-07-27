import com.logicmonitor.xensimulator.client.Client;
import com.logicmonitor.xensimulator.server.Server;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Session;

import java.net.URL;

public class TestXenLogin {
    public static void main(String[] args) throws Exception {
        new Server(8080, "test", "testpass").start();
        Client cl = new Client("http://127.0.0.1:8080");
        cl.request("host", "reg_datasource", new Object[]{"cpu0", 1.3D});
        cl.request("host", "add_obj", new Object[]{"host", "ThisIsATestHost"});
        boolean local = true;
        Connection c = null;
        Session s = null;
        if (local) {
            c = new Connection(new URL("http://127.0.0.1:8080/"));
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
}
