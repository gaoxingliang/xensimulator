package com.logicmonitor.xensimulator.server.api;

import com.xensource.xenapi.Host;
import com.xensource.xenapi.HostMetrics;
import com.xensource.xenapi.Pool;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Map;

public class TestHost extends TestCase {

    @Test
    public void test() throws Exception {

        Map<Host, Host.Record> allHostRecords = Host.getAllRecords(c);
        System.out.println(allHostRecords);

        Pool.Record poolRecord = Pool.getAllRecords(c).values().iterator().next();
        Host h2 = poolRecord.master;
        System.out.println("for host in pool:" + h2.getUuid(c));
        System.out.println("host address:" + h2.getAddress(c));

        Host h = Host.getByUuid(c, "4ac188d2-7dfa-44ae-8e6f-3b88d75220ce");
        System.out.println("Host got:" + h.toWireString());
        System.out.println(h.getUuid(c));
        HostMetrics hm = h.getMetrics(c);
        HostMetrics.Record r = hm.getRecord(c);
        Assert.assertTrue(r.live);
        Assert.assertEquals(1024 * 1024 * 1024L, r.memoryTotal.longValue());

        Host h3 = allHostRecords.keySet().iterator().next();
        System.out.println(h3.getRecord(c));
    }
}
