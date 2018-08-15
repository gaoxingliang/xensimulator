package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.server.XenSimulator;
import com.logicmonitor.xensimulator.utils.SSLUtils;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import com.xensource.xenapi.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestXenLogin {



    public static void main(String[] args) throws Exception {


        XenSimulatorSettings.ignoreSSL = true;


        System.out.println(new File(".").getCanonicalPath());

        new XenSimulator(8080, 9090, "test", "testpass").start();
        SSLUtils.ignoreSSL(8080);
        System.out.println("Done....");
        Connection c = null;
        Session s = null;
        c = new Connection(new URL("https://127.0.0.1:8080/"));
        XenSimulatorSettings.getRequestHandler = new XenSimulatorSettings.GetRequestHandler() {
            @Override
            public String processGetRequest(String uri) {
                System.out.println(" get uri: " + uri);
                try {
                    return IOUtils.toString(XenSimulatorSettings.class.getResourceAsStream("/xmltemplates/vm_rrd.xml"));
                }
                catch (IOException e) {
                    throw new IllegalStateException("Fail to prepare get data", e);
                }
            }
        };
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
