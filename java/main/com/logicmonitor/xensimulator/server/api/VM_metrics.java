package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.NotSupport;
import com.logicmonitor.xensimulator.utils.RandomValue;
import com.logicmonitor.xensimulator.utils.XMLResponse;

import java.util.Date;
import java.util.Map;
import java.util.Random;

public class VM_metrics extends BaseAPI {
    @Override
    public String getType() {
        return "VM_metrics";
    }

    @Override
    public String getFileForAllRecords() {
        throw new NotSupport();
    }

    @RandomValue(attrs = "memory_actual")
    @API
    public Map get_record(String session, String vmuuid) throws Exception {
        // this has too much attributes let's use the template
        XMLResponse xmlResponse = XMLResponse.parse(VM_metrics.class.getResourceAsStream("/VM_metrics_get_record.xml"));

        Map template = (Map)xmlResponse.result.get(Response.VALUE);
        template.put("uuid", vmuuid);
        template.put("last_updated", new Date());
        template.put("memory_actual", new Random().nextInt(1024) + "");
        return Response.newRsp().withValue(template).build();
    }
}
