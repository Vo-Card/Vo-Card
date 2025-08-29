package com.voc.database;

import java.util.List;

import com.voc.utils.Row;

public class SQLResult {
    private final boolean success;
    private final List<Row> data;
    private final long generatedKey;
    private final String errorMessage;

    public SQLResult(boolean success, List<Row> data, long generatedKey, String errorMessage) {
        this.success = success;
        this.data = data;
        this.generatedKey = generatedKey;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Row> getData() {
        return data;
    }

    public long getGeneratedKey() {
        return generatedKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
}
