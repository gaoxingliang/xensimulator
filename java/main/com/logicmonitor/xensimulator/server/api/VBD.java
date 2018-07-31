package com.logicmonitor.xensimulator.server.api;

public class VBD extends BaseAPI {
    @Override
    public String getType() {
        return "VBD";
    }

    @Override
    public String getFileForAllRecords() {
        return "";
    }
}
