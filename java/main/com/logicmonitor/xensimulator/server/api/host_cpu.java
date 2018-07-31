package com.logicmonitor.xensimulator.server.api;

public class host_cpu extends BaseAPI {
    @Override
    public String getType() {
        return "host_cpu";
    }

    @Override
    public String getFileForAllRecords() {
        return "/host_cpu_all_records.xml";
    }
}
