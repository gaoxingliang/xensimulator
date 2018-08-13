package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;
import com.logicmonitor.xensimulator.utils.API;
import com.logicmonitor.xensimulator.utils.NotSupport;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class session extends BaseAPI {

    public static String user = "DefaultUser";
    public static String pass = "DefaultPass";
    public static final Set<String> sessions = new ConcurrentSkipListSet<>();

    @API
    public Map login_with_password(String user, String pass, String version) throws Exception {
        if (Objects.equals(user, session.user) && Objects.equals(pass, session.pass)) {
            String session = UUID.randomUUID().toString();
            sessions.add(session);
            return Response.newRsp().withValue(session).build();
        }
        return Response.RESPONSE_AUTH_FAILED;
    }

    @API
    public Map logout(String ref) {
        return Response.RESPONSE_OK;
    }

    @API
    public Map get_this_host(String session, String ref) throws Exception {
        return Response.newRsp().put("Value", ref).build();
    }

    @Override
    public String getType() {
        return "session";
    }

    @Override
    public String getFileForAllRecords() {
        throw new NotSupport("Not support " + getType() + ".get_all_records");
    }

    @API
    @Override
    public Map get_all_records(String session) throws Exception {
        return null;
    }
}
