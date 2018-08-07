package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.NotSupport;
import com.logicmonitor.xensimulator.utils.XMLResponse;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAPI {

    public static final Logger LOG = LogManager.getLogger();

    public static Map<String, List<Object>> allObjects = new HashMap<>();

    // return what type of object this is.
    public abstract String getType();

    /**
     * return the simulator file when calling get_all_records api
     */
    public abstract String getFileForAllRecords();

    @API
    public Map get_record(String session, String objRef) throws Exception {
        throw new NotSupport();
    }

    @API
    public Map get_all(String session) throws Exception {
        List<Object> objects;
        synchronized (allObjects) {
            objects = allObjects.get(getType());
        }
        if (objects == null) {
            objects = new ArrayList<>();
        }
        return Response.newRsp().withValue(objects).build();
    }

    @API
    public Map get_by_uuid(String session, String uuid) throws Exception {
        Map resp = get_all_records(session);
        Map allThisTypeObjects = (Map)resp.get(Response.VALUE);
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry)entry;
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
        Map allThisTypeObjects = (Map)resp.get(Response.VALUE);
        for (Object entry : allThisTypeObjects.entrySet()) {
            Map.Entry oneEntry = (Map.Entry)entry;
            if (oneEntry.getKey().equals(objectRef)) {
                Map valueMapForThisEntry = (Map) oneEntry.getValue();
                return Response.newRsp().withValue(valueMapForThisEntry.get("uuid")).build();
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
     * @param session
     * @return
     * @throws Exception
     */
    @API
    public Map get_all_records(String session) throws Exception {
        try {
            XMLResponse response = XMLResponse.parse(host.class.getResourceAsStream(getFileForAllRecords()));
            return Response.newRsp().withValue(response.value).build();
        } catch (Exception e) {
            LOG.error("Fail to process get all records", e);
            return Response.newRsp().withError("Fail to process " + ExceptionUtils.getFullStackTrace(e)).build();
        }
    }


    public Map add_obj(String type, String o) throws Exception {
        return add_obj(type, (Object)o);
    }

    public Map add_obj(String type, Object o) throws Exception {
        synchronized (allObjects) {
            List<Object> objects = allObjects.get(type);
            if (objects == null) {
                objects = new ArrayList<>();
                allObjects.put(type, objects);
            }
            objects.add(o);
        }
        return Response.RESPONSE_OK;
    }

}
