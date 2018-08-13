package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;

import java.util.Map;
import java.util.Random;

public class host_cpu extends BaseAPI {
    @Override
    public String getType() {
        return "host_cpu";
    }


    @Override
    public Map get_all_records(String session) throws Exception {
        Map resp = super.get_all_records(session);
        /**
         * let's register the cpu metrics for host object
         */
        ((Map) resp.get(Response.VALUE)).entrySet().forEach(e -> {
            Map.Entry en = (Map.Entry) e;
            Map signleCpu = (Map) (en.getValue());
            host.ds2Value.put("cpu" + signleCpu.get("number"), new Random().nextInt(100) * 1d);
        });
        return resp;
    }

    @Override
    public String getFileForAllRecords() {
        return "/xmltemplates/host_cpu_all_records.xml";
    }
}
