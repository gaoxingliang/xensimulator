package com.logicmonitor.xensimulator;

import java.util.HashMap;
import java.util.Map;

public class Response {


    public static final String STATUS = "Status";
    public static final String STATUS_SUCCESS = "Success";
    public static final String STATUS_FAILURE = "Failure";

    public static final String VALUE = "Value";
    public static final String ERROR_DESCR = "ErrorDescription";


    public static final Map Response_UUID_INVALID = Response.newRsp().withError("UUID_INVALID", "The UUID not exists",
            "Please check the uuid for the templates *_all_records.xml").build();

    public static final Map RESPONSE_AUTH_FAILED = Response.newRsp().withError("SESSION_AUTHENTICATION_FAILED", "Authe failed", "Auth failed").build();

    public static final Map RESPONSE_INTERNAL_ERROR = Response.newRsp().withError("INTERNAL_ERROR", "Something wrong in simulator").build();

    public static final Map RESPONSE_OK = Response.newRsp().build();

    private Response(){
        _resp = new HashMap();
        _resp.put(STATUS, STATUS_SUCCESS);
    }

    private Map _resp;

    public static Response newRsp(){
        return new Response();
    }

    public Response withStatus(String status) {
        _resp.put(STATUS, status);
        return this;
    }
    public Response withValue(Object value) {
        _resp.put(VALUE, value);
        return this;
    }

    public Response withError(String ...error) {
        /**
         * log4j:WARN Please initialize the log4j system properly.
         * Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 1
         * 	at com.xensource.xenapi.Types.checkResponse(Types.java:1234)
         * 	at com.xensource.xenapi.Connection.dispatch(Connection.java:360)
         * 	at com.xensource.xenapi.Host.queryDataSource(Host.java:1710)
         * 	at TestXenLogin.main(TestXenLogin.java:27)
         */
        _resp.put(STATUS, STATUS_FAILURE);
        String[] errors = error;
        _resp.put(ERROR_DESCR, errors);
        return this;
    }

    public Response put(Object k, Object v) {
        _resp.put(String.valueOf(k), v);
        return this;
    }

    public Map build() {
        return _resp;
    }



}
