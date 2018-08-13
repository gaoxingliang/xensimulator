package com.logicmonitor.xensimulator.server.api;

import com.xensource.xenapi.VIF;
import com.xensource.xenapi.VM;

public class TestVIF extends TestCase {
    @Override
    public void test() throws Exception {
        System.out.println(VIF.getAllRecords(c));
        VM vm = VIF.getAllRecords(c).values().iterator().next().VM;
        vm.getUuid(c);
    }
}
