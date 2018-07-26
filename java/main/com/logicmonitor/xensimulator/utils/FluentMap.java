package com.logicmonitor.xensimulator.utils;

import java.util.HashMap;
import java.util.Map;

public class FluentMap {
    private Map<Object, Object> _map;
    private FluentMap(){
        _map = new HashMap<>();
    }

    public static FluentMap newMap(){
        return new FluentMap();
    }

    public FluentMap put(Object key, Object value) {
        _map.put(key, value);
        return this;
    }



    public Map<Object, Object> build() {
        return _map;
    }


}
