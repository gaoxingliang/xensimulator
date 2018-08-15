package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.client.XenRPCClient;
import com.logicmonitor.xensimulator.server.XenSimulator;
import com.logicmonitor.xensimulator.utils.SSLUtils;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.Session;

import java.net.URL;

public class Example {
    public static void main(String[] args) throws Exception {
        XenSimulatorSettings.ignoreSSL = true;
        int httpsPort = 8080, httpPort = 9090;
        XenSimulator simulator = new XenSimulator(httpsPort, httpPort, "test", "testpass");
        simulator.start();
        SSLUtils.ignoreSSL(8080);
        Connection c = new Connection(new URL(String.format("https://127.0.0.1:%d/", httpsPort)));
        Session s = Session.loginWithPassword(c, "test", "testpass", APIVersion.latest().toString());
        System.out.println("All hosts:" + Host.getAllRecords(c));
        simulator.stop();

        // change the value of xml
        XenRPCClient client = new XenRPCClient("https://127.0.0.1:" + simulator.httpsPort);

        String vmInstance = "OpaqueRef:8a38e59d-c143-16e1-c475-30324368d589";
        Object updateIsATemplateResult = client.request(XenSimulatorSettings.class.getName(), "updateXmlFileContent", new
                Object[]{"/xmltemplates/VM_all_records.xml", "$Value$" + vmInstance + "$is_a_template", true});
        System.out.println("Update the xml response " + updateIsATemplateResult);



    }
}
