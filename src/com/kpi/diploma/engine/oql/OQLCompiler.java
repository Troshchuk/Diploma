package com.kpi.diploma.engine.oql;

import com.kpi.diploma.engine.QueryType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dtroshchuk
 */
public class OQLCompiler {
    public static CompiledValue compile(String OQLQuery) throws Exception {
        String[] lexems = OQLQuery.split(" ");
        QueryType queryType;
        List<String> regions = new ArrayList<>();
        List<String> equals = new ArrayList<>();
        switch (lexems[0]) {
            case "SELECT":
                queryType = QueryType.SELECT;
                if (!lexems[1].equals("*")) {
                    throw new Exception("OQLCompiler not support selection by specific fields");
                }
                if (!lexems[2].equals("FROM")) {
                    throw new Exception("Syntax error");
                }
                int i = 3;

                for (; i < lexems.length; i++) {
                    if (!lexems[i].endsWith(",")) {
                        String region = lexems[i];
                        regions.add(region);
                        i++;
                        break;
                    }

                    String region = lexems[i].substring(0, lexems[i].length() - 1);
                    regions.add(region);
                }
                if (i == lexems.length) {
                    break;
                }
                if (!lexems[i].equals("WHERE")) {
                    throw new Exception("Syntax error");
                }
                i++;
                for (; i < lexems.length; i++) {
                    if (!lexems[i].endsWith(",")) {
                        String[] equal = lexems[i].split("=");
                        equals.add(equal[0]);
                        equals.add(equal[1]);
                        break;
                    }

                    String[] equal = lexems[i].substring(0, lexems[i].length() - 1).split("=");
                    equals.add(equal[0]);
                    equals.add(equal[1]);
                }
                break;
            default:
                throw new Exception("OQLCompiler not supported this operation");
        }

        return new CompiledValue(queryType, regions, equals);
    }
}
