package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.FluentMap;
import com.logicmonitor.xensimulator.utils.NotSupport;

import java.util.Date;
import java.util.Map;
import java.util.Random;

public class host_metrics extends BaseAPI {
    @Override
    public String getType() {
        return "host_metrics";
    }

    @Override
    public Map get_record(String session, String objRef) throws Exception {
        return Response.newRsp().withValue(FluentMap.newMap().put("uuid", objRef)
                .put("memory_free", new Random().nextInt(1024 * 1024 * 100) + 102400L)
                .put("memory_total", 1024 * 1024 * 1024L)
                .put("last_updated", new Date())
                .put("live", true)
                .build())
                .build();
    }

    @Override
    public String getFileForAllRecords() {
        throw new NotSupport();
    }
}
