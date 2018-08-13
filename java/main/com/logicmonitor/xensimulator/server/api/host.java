package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.SimAPI;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class host extends BaseAPI {


    public static int API_VERSION_MAJOR;
    public static int API_VERSION_MINOR;

    public static Map<String, Object> ds2Value = new ConcurrentHashMap<>();

    public static Map<String, Object> defaultDS2Value = new ConcurrentHashMap<>();

    /**
     * this will be used as the master slave structure.
     * We can set this value by
     */
    public static String masterIp = "127.0.0.1";

    static {
        try {
            Stream.of((Object[])XenSimulatorSettings.getXmlResponseValueForFile("/xmltemplates/host_get_datasources.xml")).forEach(o -> {
                // this is a map
                Map map = (Map)o;
                defaultDS2Value.put(map.get("name_label").toString(), map.get("value"));
            });
            LOG.info("All data sources loaded count={}", defaultDS2Value.size());
        }
        catch (Exception e) {
            LOG.error("Fail to load default data sources", e);
        }

        try {
            Map map = (Map) XenSimulatorSettings.getXmlResponseValueForFile("/xmltemplates/host_all_records.xml");
            Map hostMap = (Map)map.values().iterator().next();
            API_VERSION_MAJOR = Integer.valueOf(hostMap.get("API_version_minor").toString());
            API_VERSION_MINOR = Integer.valueOf(hostMap.get("API_version_major").toString());
            LOG.info("API version loaded major={}, minor={}", API_VERSION_MAJOR, API_VERSION_MINOR);
        } catch (Exception e) {
            LOG.error("Fail to load the api version", e);
            throw new IllegalStateException("Fail to load the API version " + e.getMessage(), e);
        }

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
            Map valueMap = (Map)XenSimulatorSettings.getXmlResponseValueForFile(getFileForAllRecords());
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
        return "/xmltemplates/host_all_records.xml";
    }

    @SimAPI
    public Map setMasterIP(String masterIp) {
        LOG.info("The master ip changed from {} to {}", host.masterIp, masterIp);
        host.masterIp = masterIp;
        return Response.RESPONSE_OK;
    }

}
