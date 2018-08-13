package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.NotSupport;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;

import java.util.Map;

public class VM_metrics extends BaseAPI {
    @Override
    public String getType() {
        return "VM_metrics";
    }

    @Override
    public String getFileForAllRecords() {
        throw new NotSupport("Not implement the VM_metrics.get_all_record yet");
    }

    @API
    public Map get_record(String session, String vmuuid) throws Exception {
        // this has too much attributes let's use the template
        return Response.newRsp().withValue(XenSimulatorSettings.getXmlResponseValueForFile("/xmltemplates/VM_metrics_get_record.xml"))
                .build();
    }
}
