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

        System.out.println(VM.getAllRecords(c));

        System.out.println("=======");
        VM vm = VM.getByUuid(c, "ec944b8d-861e-ac96-ec47-5f56df8ef772");
        System.out.println(vm);
        System.out.println(vm.getMetrics(c).getRecord(c));


        System.out.println("PIF");
        System.out.println(PIF.getAllRecords(c));
        PIF p = PIF.getByUuid(c, "fa120a3e-6bb4-77af-7ffc-9f1110e8a84b");
        PIF.Record pr = PIF.getByUuid(c, "fa120a3e-6bb4-77af-7ffc-9f1110e8a84b").getRecord(c);
        System.out.println(pr.device);
        System.out.println(p.getMetrics(c).getRecord(c));
        System.out.println(h.getDataSources(c));
        //System.out.println(h.queryDataSource(c, "pif_" + pr.device + "_vif.rx"));
        Session.logout(c);
    }
}
