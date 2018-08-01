import com.logicmonitor.xensimulator.utils.XMLResponse;
import com.xensource.xenapi.*;

import java.net.URL;

public class TestLocalXenAPI {
    public static void main(String[] args) throws Exception {
        XMLResponse r = XMLResponse.parse(TestLocalXenAPI.class.getResourceAsStream("/host_metrics_get_record.xml"));

        boolean local = false;
        Connection c = null;
        Session s = null;
        c = new Connection(new URL("http://192.168.170.151"));
        s = Session.loginWithPassword(c, "root", "123456", APIVersion.latest().toString());

        Host.getAllRecords(c).entrySet().stream().forEach(e -> {
            Host h = e.getKey();
            System.out.println(e.getValue().toMap());
        });

        Host h = Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce");
        HostMetrics hm = h.getMetrics(c);
        System.out.println("cpu usage:" + h.queryDataSource(c, "cpu0"));
        System.out.println(hm.getRecord(c));
        Session.logout(c);
    }
}
