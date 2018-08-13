package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.client.XenRPCClient;
import com.logicmonitor.xensimulator.utils.ObjectPathUtils;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import junit.framework.Assert;
import org.junit.Test;

/**
 * This function will test we can update and set the xml values from remote
 */
public class TestRemoteChangeXmlCase extends  TestCase {

    @Test
    public void test() throws Exception {
        XenRPCClient client = new XenRPCClient("https://127.0.0.1:" + simulator.httpsPort);
        Object allFiles = client.request(XenSimulatorSettings.class.getName(), "getAllXmlFileNames", new Object[0]);
        System.out.println(allFiles);
        // get one file content value
        Object hostAllRecords = client.request(XenSimulatorSettings.class.getName(), "getXmlFileContent", new Object[]{"/xmltemplates/host_all_records.xml"});
        System.out.println(hostAllRecords);
        System.out.println("the socket count is:" + ObjectPathUtils.get(hostAllRecords, "$Value$OpaqueRef:144cc055-0ba7-f978-36c6-f63d4681e924$cpu_info$socket_count"));
        Object updateResult = client.request(XenSimulatorSettings.class.getName(), "updateXmlFileContent", new Object[]{"/xmltemplates/host_all_records.xml", "$Value$OpaqueRef:144cc055-0ba7-f978-36c6-f63d4681e924$cpu_info$socket_count", 66});
        System.out.println("Update result:" + updateResult);
        hostAllRecords = client.request(XenSimulatorSettings.class.getName(), "getXmlFileContent", new Object[]{"/xmltemplates/host_all_records.xml"});
        System.out.println("After update the socket count is:" + ObjectPathUtils.get(hostAllRecords, "$Value$OpaqueRef:144cc055-0ba7-f978-36c6-f63d4681e924$cpu_info$socket_count"));
        Assert.assertEquals("The value should be 66", 66, ObjectPathUtils.get(hostAllRecords, "$Value$OpaqueRef:144cc055-0ba7-f978-36c6-f63d4681e924$cpu_info$socket_count"));

        // we also has a array type of object
        Object updateArrayResult = client.request(XenSimulatorSettings.class.getName(), "updateXmlFileContent", new Object[]{"/xmltemplates/host_get_datasources.xml", "$Value$0$enabled", false});
        System.out.println("Update result:" + updateArrayResult);

        Object datasources = client.request(XenSimulatorSettings.class.getName(), "getXmlFileContent", new Object[]{"/xmltemplates/host_get_datasources.xml"});
        System.out.println("After update the enabled is:" + ObjectPathUtils.get(datasources, "$Value$0$enabled"));
        Assert.assertEquals("The value should be false", false, ObjectPathUtils.get(datasources, "$Value$0$enabled"));



    }
}
