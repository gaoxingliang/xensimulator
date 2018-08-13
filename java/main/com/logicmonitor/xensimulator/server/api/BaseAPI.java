package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.NotSupport;
import com.logicmonitor.xensimulator.utils.XMLResponse;
import com.logicmonitor.xensimulator.utils.XenSimulatorSettings;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public abstract class BaseAPI {

    public static final Logger LOG = LogManager.getLogger();

    // return what type of object this is.
    public abstract String getType();

    /**
     * return the simulator file when calling get_all_records api
     */
    public abstract String getFileForAllRecords();

    @API
    public Map get_record(String session, String objRef) throws Exception {
        throw new NotSupport("Not support " + getType() + ".get_record");
    }

    @API
    public Map get_all(String session) throws Exception {

        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map) resp.get(Response.VALUE);
        List<Object> thisTypeObjects = new ArrayList<>();
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry) entry;
            thisTypeObjects.add(oneEntry.getKey());
        }
        return Response.newRsp().withValue(thisTypeObjects).build();
    }

    @API
    public Map get_by_uuid(String session, String uuid) throws Exception {
        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map) resp.get(Response.VALUE);
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry) entry;
            Map valueMapForThisEntry = (Map) oneEntry.getValue();
            if (valueMapForThisEntry.get("uuid").toString().equals(uuid)) {
                return Response.newRsp().withValue(oneEntry.getKey()).build();
            }
        }
        return Response.Response_UUID_INVALID;
    }

    @API
    public Map get_uuid(String session, String objectRef) throws Exception {
        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map) resp.get(Response.VALUE);
        Set<String> existsUUIDs = new TreeSet<>();
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry) entry;
            existsUUIDs.add(oneEntry.getKey().toString());
            if (oneEntry.getKey().equals(objectRef)) {
                Map valueMapForThisEntry = (Map) oneEntry.getValue();
                return Response.newRsp().withValue(valueMapForThisEntry.get("uuid")).build();
            }
        }
        LOG.warn("UUID for type={} not found={}, all UUIDs are={}", getType(), objectRef, existsUUIDs);
        return Response.Response_UUID_INVALID;
    }


    /**
     * Returns a map like:
     * {
     * Status: Success
     * Value: the value objects
     * }
     *
     * @param session
     * @return
     * @throws Exception
     */
    @API
    public Map get_all_records(String session) throws Exception {
        try {
            XMLResponse response = XenSimulatorSettings.getXmlResponseForFile(getFileForAllRecords());
            return Response.newRsp().withValue(response.value).build();
        }
        catch (Exception e) {
            LOG.error("Fail to process get all records", e);
            return Response.newRsp().withError("Fail to process " + ExceptionUtils.getFullStackTrace(e)).build();
        }
    }


}
