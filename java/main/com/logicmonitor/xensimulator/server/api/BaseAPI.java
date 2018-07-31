package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.XMLResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseAPI {

    public static Map<String, List<Object>> allObjects = new HashMap<>();

    // return what type of object this is.
    public abstract String getType();

    /**
     * return the simulator file when calling get_all_records api
     */
    public abstract String getFileForAllRecords();

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
    public Map get_all_records(String session) throws Exception {
        XMLResponse response = XMLResponse.parse(host.class.getResourceAsStream(getFileForAllRecords()));
        return Response.newRsp().withValue(response.value).build();
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
