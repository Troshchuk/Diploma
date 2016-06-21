package com.kpi.diploma.engine.oql;

import com.kpi.diploma.engine.QueryType;

import java.util.List;

/**
 * @author dtroshchuk
 */
public class CompiledValue {
    QueryType queryType;
    List<String> regions;
    List<String> equals;

    public CompiledValue(QueryType queryType, List<String> regions, List<String> equals) {
        this.queryType = queryType;
        this.regions = regions;
        this.equals = equals;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public List<String> getEquals() {
        return equals;
    }

    public void setEquals(List<String> equals) {
        this.equals = equals;
    }
}
