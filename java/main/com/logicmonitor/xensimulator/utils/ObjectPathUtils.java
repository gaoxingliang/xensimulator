package com.logicmonitor.xensimulator.utils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class implements a vistor style of get and updating a object attribute by a xml style path.
 * Eg a map:
 * {
 *     Status: "OK",
 *     Value: {
 *         "students": [
 *              "name1",
 *              "name2"
 *         ],
 *         "avgscore": 1.3
 *     }
 * }
 *
 * we can get and update by the following style path:
 * 1. the students[0]'s name
 *    The path is: $Value$.$students.$0
 * 2. the avgscore:
 *    The path is: $Value$avgscore
 *
 *    That's to say, for a array, the format should like:
 *          $arrayName$arrayIndex$objectAttr
 *
 */
public class ObjectPathUtils {


    interface ObjectPathVistor {
        void mapObjectFound(Map parent, Object pathKey, Object pathValue);

        void arrayObjectFound(Object[] parent, int index, Object pathValue);
    }

    static class GetObjectPathVistor implements ObjectPathVistor {
        AtomicReference<Object> resultRef = new AtomicReference<>();

        @Override
        public void mapObjectFound(Map parent, Object pathKey, Object pathValue) {
            resultRef.set(pathValue);
        }

        @Override
        public void arrayObjectFound(Object[] parent, int index, Object pathValue) {
            resultRef.set(pathValue);
        }

        public Object getResult() {
            return resultRef.get();
        }
    }

    static class UpdateObjectPathVistor implements ObjectPathVistor {
        private final Object newPathValue;

        public UpdateObjectPathVistor(Object newPathValue) {
            this.newPathValue = newPathValue;
        }

        @Override
        public void mapObjectFound(Map parent, Object pathKey, Object pathValue) {
            parent.put(pathKey, newPathValue);
        }

        @Override
        public void arrayObjectFound(Object[] parent, int index, Object pathValue) {
            parent[index] = pathValue;
        }
    }

    private static void visitObject(Object object, String xmlpath, ObjectPathVistor pathVistor) {
        Object currentLevelObject = object;
        if (!xmlpath.startsWith("$")) {
            throw new IllegalArgumentException("The xmlpath must start with $");
        }
        // ignore the first $
        String[] splitPath = xmlpath.substring(1).split("\\$");

        for (int i = 0; i < splitPath.length; i++) {
            if (currentLevelObject instanceof Map) {
                Object value = ((Map) currentLevelObject).get(splitPath[i]);
                if (value == null) {
                    throw new IllegalArgumentException(String.format("The xmlpath %s at %s not found", xmlpath, splitPath[i]));
                }
                else if (i == splitPath.length - 1 /*the last one*/) {
                    pathVistor.mapObjectFound((Map) currentLevelObject, splitPath[i], value);
                    return;
                }
                else {
                    currentLevelObject = value;
                }
            }
            else if (currentLevelObject instanceof Object[]) {
                // if the current level object is a array, so the split path must be like a $arrayName$arrayIndex
                int arrayIndex = 0;
                try {
                    arrayIndex = Integer.valueOf(splitPath[i]);
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid path - " + splitPath[i] + " which should like $arrayName$arrayIndex");
                }
                if (i == splitPath.length - 1 /*the last one*/) {
                    pathVistor.arrayObjectFound((Object[]) currentLevelObject, arrayIndex, ((Object[]) currentLevelObject)[arrayIndex]);
                    return;
                }
                else {
                    currentLevelObject = ((Object[]) currentLevelObject)[arrayIndex];
                }
            }
            else {
                throw new NotSupport(String.format("The class - %s not support yet when updating object", currentLevelObject.getClass()
                        .getName()));
            }
        }
        throw new IllegalArgumentException(String.format("The xmlpath %s not found", xmlpath));
    }

    public static Object get(Object object, String xmlpath) {
        GetObjectPathVistor vistor = new GetObjectPathVistor();
        visitObject(object, xmlpath, vistor);
        return vistor.getResult();
    }

    public static void update(Object object, String xmlpath, Object newValue) {
        UpdateObjectPathVistor vistor = new UpdateObjectPathVistor(newValue);
        visitObject(object, xmlpath, vistor);
    }

}
