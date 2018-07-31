package com.logicmonitor.xensimulator.server.api;

public class PIF extends BaseAPI {
    @Override
    public String getType() {
        return "PIF";
    }

    @Override
    public String getFileForAllRecords() {
        return "/PIF_all_records.xml";
    }

}
