package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class host extends BaseAPI {
    public static int API_VERSION_MAJOR = 1;
    public static int API_VERSION_MINOR = 2;

    public static Map<String, Object> ds2Value = new ConcurrentHashMap<>();

    public Map reg_datasource(String dsName, Object value) {
        ds2Value.put(dsName, value);
        return Response.RESPONSE_OK;
    }
    
    public Map reg_datasource(String dsName, Double value) {
        ds2Value.put(dsName, value);
        return Response.RESPONSE_OK;
    }

    @API
    public Map query_data_source(String session, String ref, String dsName) throws Exception {
        Object o = ds2Value.get(dsName);
        if (o == null) {
            return Response.RESPONSE_INTERNAL_ERROR;
        }
        return Response.newRsp().withValue(o).build();
    }

    @API
    public Map get_API_version_major(String session, String ref) throws Exception {
        return Response.newRsp().withValue("" + API_VERSION_MAJOR).build();
    }

    @API
    public Map get_API_version_minor(String session, String ref) throws Exception {
        return Response.newRsp().withValue("" + API_VERSION_MINOR).build();
    }

    @API
    public Map get_by_uuid(String session, String uuid) throws Exception {
        // check whether this object exists
        Map resp = get_all_records(session);
        Map allHosts = (Map)resp.get(Response.VALUE);
        for (Object value : allHosts.values()) {
            Map valueMapForAHost = (Map) value;
            if (valueMapForAHost.get("uuid").equals(uuid)) {
                return Response.newRsp().withValue(uuid).build();
            }
        }
        return Response.Response_UUID_INVALID;
    }

    @API
    public Map get_metrics(String session, String uuid) throws Exception {
        return Response.newRsp().withValue(uuid).build();
    }

    @Override
    public String getType() {
        return "host";
    }

    @Override
    public String getFileForAllRecords() {
        return "/host_all_records.xml";
    }

}
