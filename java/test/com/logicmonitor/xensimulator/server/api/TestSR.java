package com.logicmonitor.xensimulator.server.api;

import com.xensource.xenapi.SR;

public class TestSR extends TestCase {
    @Override
    public void test() throws Exception {
        System.out.println(SR.getAllRecords(c));
    }
}
