package com.logicmonitor.xensimulator.server;

import com.logicmonitor.xensimulator.utils.FluentMap;

import java.util.Map;

public class session {
    public Map login_with_password(String a, String b) {
        System.out.println(a + " " + b);

        return FluentMap.newMap().put("Status", "Success").put("Value", "This isSessionID").build();
    }
}
