package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;

import java.util.Map;

public class VM extends BaseAPI {
    @Override
    public String getType() {
        return "VM";
    }

    @Override
    public String getFileForAllRecords() {
        return "/VM_all_records.xml";
    }

    @API
    public Map get_metrics(String session, String vmuuid) {
        return Response.newRsp().withValue(vmuuid).build();
    }

}
