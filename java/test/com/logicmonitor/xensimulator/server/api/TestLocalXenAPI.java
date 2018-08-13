package com.logicmonitor.xensimulator.server.api;

import com.xensource.xenapi.*;
import com.xensource.xenapi.PIF;
import com.xensource.xenapi.VBD;
import com.xensource.xenapi.VM;

import java.net.URL;

public class TestLocalXenAPI {
    public static void main(String[] args) throws Exception {

        boolean local = false;
        Connection c = null;
        Session s = null;
        c = new Connection(new URL("http://192.168.170.151"));
        s = Session.loginWithPassword(c, "root", "123456", APIVersion.latest().toString());

        Host.getAllRecords(c).entrySet().stream().forEach(e -> {
            Host h = e.getKey();
            System.out.println(e.getValue().toMap());
        });

        Pool.Record poolRecord = Pool.getAllRecords(c).values().iterator().next();
        Host h2 = poolRecord.master;
        System.out.println("for host in pool:" + h2.getUuid(c));

        Host h = Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce");
        System.out.println(h.getUuid(c));
        System.out.println(h.getAddress(c));
        HostMetrics hm = h.getMetrics(c);
        System.out.println("cpu usage:" + h.queryDataSource(c, "cpu0"));
        System.out.println(hm.getRecord(c));

        System.out.println(com.xensource.xenapi.VM.getAllRecords(c));

        System.out.println("=======");
        com.xensource.xenapi.VM vm = VM.getByUuid(c, "ec944b8d-861e-ac96-ec47-5f56df8ef772");
        System.out.println(vm);
        System.out.println(vm.getMetrics(c).getRecord(c));


        System.out.println("PIF");
        System.out.println(com.xensource.xenapi.PIF.getAllRecords(c));
        com.xensource.xenapi.PIF p = com.xensource.xenapi.PIF.getByUuid(c, "fa120a3e-6bb4-77af-7ffc-9f1110e8a84b");
        com.xensource.xenapi.PIF.Record pr = PIF.getByUuid(c, "fa120a3e-6bb4-77af-7ffc-9f1110e8a84b").getRecord(c);
        System.out.println(pr.device);
        System.out.println(p.getMetrics(c));
        System.out.println(p.getMetrics(c).getRecord(c));
        System.out.println(h.getDataSources(c));


        System.out.println("All vbds:" + VBD.getAllRecords(c));

        Session.logout(c);
    }
}
