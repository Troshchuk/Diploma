package com.kpi.diploma.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dtroshchuk
 */
public class Index {
    private Map<String, Object> indexedObjects = new HashMap<>();
    private String region;
    private List<String> fields;

    public static Index create(String region, List<String> fields) {
        Index index = new Index();
        index.region = region;
        index.fields = fields;

        Map regionMap = Cache.getRegion(region);

        for (Object object : regionMap.values()) {
            String key = "";
            for (String field : fields) {
                try {
                    Field f = object.getClass().getDeclaredField(field);
                    f.setAccessible(true);
                    key += f.get(object).toString() + "#";
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            index.indexedObjects.put(key, object);
        }

        return index;
    }

    public <T> T get(List<String> values) {
        String key = "";
        for (String value : values) {
            key += value + "#";
        }
        return (T) indexedObjects.get(key);
    }

    public List<String> getFields() {
        return fields;
    }

    public String getRegion() {
        return region;
    }
}
