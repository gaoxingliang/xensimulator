package com.logicmonitor.xensimulator.utils;

import java.util.HashMap;
import java.util.Map;

public class FluentMap {
    private Map map;
    private FluentMap(){
        map = new HashMap<>();
    }

    public static FluentMap newMap() {
        return new FluentMap();
    }

    public FluentMap put(Object k, Object v) {
        map.put(k, v);
        return this;
    }

    public Map build() {
        return map;
    }
}
