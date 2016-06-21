package com.kpi.diploma.engine;

import com.kpi.diploma.engine.oql.OQLCompiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author dtroshchuk
 */
public class Cache {
    private static Map<String, Object> regions = new HashMap<>();
    private static Map<String, Index> indexes = new HashMap<>();

    public static <T> T getRegion(String name) {
        Object map = regions.get(name);
        if (map == null) {
            synchronized (Cache.class) {
                map = regions.get(name);
                if (map == null) {
                    map = new HashMap<>();
                    regions.put(name, map);
                }
            }
        }
        return (T) map;
    }

    public static void deleteRegion(String name) {
        regions.remove(name);
    }

    public static Index createIndex(String name, String region, List<String> fields) {
        indexes.put(name, Index.create(region, fields));
        return indexes.get(name);
    }

    public static Index createComplexIndex(String name, Supplier<Map<String, ? extends Object>> funtion) {
        indexes.put(name, Index.create(funtion));
        return indexes.get(name);
    }

    public static Map<String, Index> getIndexes() {
        return indexes;
    }

    public static Query createQuery(String OQLQuery) throws Exception {
        return new Query(OQLCompiler.compile(OQLQuery));
    }
}
