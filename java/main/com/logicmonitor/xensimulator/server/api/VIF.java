package com.logicmonitor.xensimulator.server.api;

public class VIF extends BaseAPI {
    @Override
    public String getType() {
        return "VIF";
    }

    @Override
    public String getFileForAllRecords() {
        return "/xmltemplates/VIF_all_records.xml";
    }
}
