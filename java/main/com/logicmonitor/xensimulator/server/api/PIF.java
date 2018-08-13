package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.utils.API;

import java.util.Map;

public class PIF extends BaseAPI {
    @Override
    public String getType() {
        return "PIF";
    }

    @Override
    public String getFileForAllRecords() {
        return "/xmltemplates/PIF_all_records.xml";
    }

    @API
    public Map get_metrics(String session, String pifRef) {
        return null;
    }

}
