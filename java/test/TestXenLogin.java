import com.logicmonitor.xensimulator.client.Client;
import com.logicmonitor.xensimulator.server.XenSimulator;
import com.logicmonitor.xensimulator.utils.SimulatorSettings;
import com.xensource.xenapi.*;

import java.io.File;
import java.net.URL;

public class TestXenLogin {



    public static void main(String[] args) throws Exception {


        SimulatorSettings.ignoreSSL = true;


        System.out.println(new File(".").getCanonicalPath());

        new XenSimulator(8080, "test", "testpass").start();
        Client cl = new Client("https://127.0.0.1:8080");
        cl.request("host", "reg_datasource", new Object[]{"cpu0", 1.3D});
        cl.request("host", "add_obj", new Object[]{"host", "ThisIsATestHost"});
        System.out.println("Done....");
        Connection c = null;
        Session s = null;
        c = new Connection(new URL("https://127.0.0.1:8080/"));
        s = Session.loginWithPassword(c, "test", "testpass", APIVersion.latest().toString());
//        System.out.println(Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce"));
//        System.out.println(com.xensource.xenapi.Host.getAllRecords(c));
//        System.out.println(HostCpu.getAllRecords(c));
//        System.out.println(PIF.getAllRecords(c));
//        System.out.println(Pool.getAllRecords(c));


        Host h = Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce");
        HostMetrics hm = h.getMetrics(c);
        System.out.println(hm.getRecord(c));

        Session.logout(c);
        //System.exit(0);


    }


}
