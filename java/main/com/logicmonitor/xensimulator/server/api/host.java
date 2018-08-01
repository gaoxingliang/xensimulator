package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.XMLResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class host extends BaseAPI {
    public static int API_VERSION_MAJOR = 1;
    public static int API_VERSION_MINOR = 2;

    public static Map<String, Object> ds2Value = new ConcurrentHashMap<>();

    public static Map<String, Object> defaultDS2Value = new ConcurrentHashMap<>();

    public host() {
        try {
            XMLResponse xmlResponse = XMLResponse.parse(host.class.getResourceAsStream("/host_get_datasources.xml"));
            Stream.of((Object[])xmlResponse.value).forEach(o -> {
                // this is a map
                Map map = (Map)o;
                defaultDS2Value.put(map.get("name_label").toString(), map.get("value"));
            });
            LOG.info("Default DS loaded count={}", defaultDS2Value.size());
        }
        catch (Exception e) {
            LOG.error("Fail to load default data sources", e);
        }
    }

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
            o = defaultDS2Value.get(dsName);
        }
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
