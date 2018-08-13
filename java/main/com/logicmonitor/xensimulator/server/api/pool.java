package com.logicmonitor.xensimulator.server.api;

public class pool extends BaseAPI {
    @Override
    public String getType() {
        return "pool";
    }

    @Override
    public String getFileForAllRecords() {
        return "/xmltemplates/pool_all_records.xml";
    }
}
