package com.kpi.diploma.engine;

import com.kpi.diploma.engine.oql.CompiledValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dtroshchuk
 */
public class Query {
    private CompiledValue compiledValue;

    public Query(CompiledValue compiledValue) {
        this.compiledValue = compiledValue;
    }

    public <T> T execute() {
        Collection<Index> indexes = Cache.getIndexes().values();
        for (Index index : indexes) {
            if (compiledValue.getRegions().contains(index.getRegion()) && compiledValue.getEquals().size() / 2 == index.getFields().size()) {
                boolean eq = true;
                for (int i = 0; i < index.getFields().size(); i++) {
                    if (!compiledValue.getEquals().contains(index.getFields().get(i))) {
                        eq = false;
                        break;
                    }
                }
                if (eq) {
                    List<String> values = new ArrayList<>();
                    for (int i = 1; i < compiledValue.getEquals().size(); i+=2) {
                        values.add(compiledValue.getEquals().get(i));
                    }
                    return index.get(values);
                }
            }
        }
        String region = compiledValue.getRegions().get(0);
        Map regionMap = Cache.getRegion(region);
        for (Object o : regionMap.values()) {
            boolean eq = true;
            for (int i = 0; i < compiledValue.getEquals().size(); i+=2) {
                try {
                    Field field = o.getClass().getDeclaredField(compiledValue.getEquals().get(i));
                    field.setAccessible(true);
                    if (!field.get(o).toString().equals(compiledValue.getEquals().get(i + 1))) {
                        eq = false;
                        break;
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (eq) {
                return (T) o;
            }
        }

        return null;
    }
}
