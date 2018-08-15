package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class VM extends BaseAPI {
    @Override
    public String getType() {
        return "VM";
    }

    @Override
    public String getFileForAllRecords() {
        return "/xmltemplates/VM_all_records.xml";
    }

    @API
    public Map get_metrics(String session, String vmuuid) {
        return Response.newRsp().withValue(vmuuid).build();
    }

    @API
    public Map query_data_source(String session, String vmref, String datasource) throws Exception {
        Object[] allDatasources = (Object[])XenSimulatorSettings.getXmlResponseValueForFile("/xmltemplates/VM_query_datasource.xml");

        for (Object singleDataSource: allDatasources){
            Map singleMap = (Map) singleDataSource;
            if (singleMap.get("name_label").toString().equals(datasource)) {
                return Response.newRsp().withValue(singleMap.get("value")).build();
            }
        }
        LOG.warn("No data source found with vmref={},datasource={}", vmref, datasource);
        return Response.Response_UUID_INVALID;
    }

    @API
    public Map get_power_state(String session, String vmref) throws Exception {
        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map) resp.get(Response.VALUE);
        Set<String> existsRefs = new TreeSet<>();
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry) entry;
            existsRefs.add(oneEntry.getKey().toString());
            if (oneEntry.getKey().equals(vmref)) {
                Map valueMapForThisEntry = (Map) oneEntry.getValue();
                return Response.newRsp().withValue(valueMapForThisEntry.get("power_state")).build();
            }
        }
        LOG.warn("vmref for type={} not found={}, all vmrefs are={}", getType(), vmref, existsRefs);
        return Response.Response_UUID_INVALID;

    }
}
