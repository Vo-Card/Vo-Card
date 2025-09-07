package com.voc.utils;

import java.util.List;

public class Convertors {
    public static void convertIdsToString(List<Row> rows, String... keys) {
        for (Row row : rows) {
            for (String key : keys) {
                Object value = row.get(key);
                if (value != null) {
                    row.put(key, value.toString());
                }
            }
        }
    }
}
