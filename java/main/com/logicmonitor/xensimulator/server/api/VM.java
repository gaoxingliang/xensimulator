package com.logicmonitor.xensimulator.server.api;

public class VM extends BaseAPI {
    @Override
    public String getType() {
        return "VM";
    }

    @Override
    public String getFileForAllRecords() {
        return "/VM_all_records.xml";
    }
}
