package com.logicmonitor.xensimulator.server.api;

import com.xensource.xenapi.VBD;
import com.xensource.xenapi.VM;

import java.util.Map;

public class TestVBD extends TestCase {
    @Override
    public void test() throws Exception {
        Map<VBD, VBD.Record> map = com.xensource.xenapi.VBD.getAllRecords(c);
        System.out.println(map);
        VM vm = map.values().iterator().next().VM;
        System.out.println("vm is:" + vm.toWireString());

        System.out.println(vm.queryDataSource(c, "vbd_xvda_write_latency"));
        System.out.println(vm.getUuid(c));
    }
}
