package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.SimAPI;
import com.logicmonitor.xensimulator.utils.XMLResponse;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class host extends BaseAPI {
    public static int API_VERSION_MAJOR = 1;
    public static int API_VERSION_MINOR = 2;

    public static Map<String, Object> ds2Value = new ConcurrentHashMap<>();

    public static Map<String, Object> defaultDS2Value = new ConcurrentHashMap<>();

    /**
     * this will be used as the master slave structure.
     * We can set this value by
     */
    public static String masterIp = "127.0.0.1";

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
    @Override
    public Map get_record(String session, String objRef) throws Exception {
        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map)resp.get(Response.VALUE);
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry)entry;
            if (oneEntry.getKey().equals(objRef)) {
                return Response.newRsp().withValue(oneEntry.getValue()).build();
            }
        }
        return Response.Response_UUID_INVALID;
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

    @API
    public Map get_address(String session, String hostRef) throws Exception {
        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map)resp.get(Response.VALUE);
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry)entry;
            if (oneEntry.getKey().equals(hostRef)) {
                Map valueMapForThisEntry = (Map) oneEntry.getValue();
                Object oldValue = valueMapForThisEntry.get("address");
                LOG.info("Reset the response of get_address, oldValue={}, response={}", oldValue, masterIp);
                return Response.newRsp().withValue(masterIp).build();
            }
        }
        return Response.Response_UUID_INVALID;
    }


    /**
     * Returns a map like:
     * {
     *  Status: Success
     *  Value: the value objects
     * }
     *
     * Re-write this to make sure the ip address is the masterip
     * @param session
     * @return
     * @throws Exception
     */
    @API
    public Map get_all_records(String session) throws Exception {
        try {
            XMLResponse response = XMLResponse.parse(host.class.getResourceAsStream(getFileForAllRecords()));
            Map valueMap = (Map)response.value;
            for (Object entry : valueMap.entrySet()){
                Map.Entry oneHostEntry = (Map.Entry) entry;
                ((Map)oneHostEntry.getValue()).put("address", masterIp);
            }
            return Response.newRsp().withValue(valueMap).build();
        } catch (Exception e) {
            LOG.error("Fail to process get all records", e);
            return Response.newRsp().withError("Fail to process " + ExceptionUtils.getFullStackTrace(e)).build();
        }
    }


   @Override
    public String getType() {
        return "host";
    }

    @Override
    public String getFileForAllRecords() {
        return "/host_all_records.xml";
    }

    @SimAPI
    public Map setMasterIP(String session, String masterIp) {
        LOG.info("The master ip changed from {} to {}", host.masterIp, masterIp);
        host.masterIp = masterIp;
        return Response.RESPONSE_OK;
    }

}
