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

    @Override
    public String getType() {
        return "host";
    }
}