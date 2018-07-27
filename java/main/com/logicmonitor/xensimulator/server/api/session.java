package com.logicmonitor.xensimulator.server.api;

import com.logicmonitor.xensimulator.Response;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class session extends BaseAPI {

    public static String user = "DefaultUser";
    public static String pass = "DefaultPass";
    public static final Set<String> sessions = new ConcurrentSkipListSet<>();
    public Map login_with_password(String user, String pass, String version) throws Exception {
        if (Objects.equals(user, session.user) && Objects.equals(pass, session.pass)) {
            String session = UUID.randomUUID().toString();
            sessions.add(session);
            return Response.newRsp().withValue(session).build();
        }
        return Response.RESPONSE_AUTH_FAILED;
    }

    public Map logout(String ref) {
        return Response.RESPONSE_OK;
    }

    public Map get_this_host(String session, String ref) throws Exception {
        return Response.newRsp().put("Value", ref).build();
    }

    @Override
    public String getType() {
        return "session";
    }
}
