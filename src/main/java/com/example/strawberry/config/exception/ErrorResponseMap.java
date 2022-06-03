package com.example.strawberry.config.exception;

import java.util.Map;

public class ErrorResponseMap {
        private Integer status;
        private Map<String, String> error;

    public ErrorResponseMap(Integer status, Map<String, String> error) {
        this.status = status;
        this.error = error;
    }

    public ErrorResponseMap() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, String> getError() {
        return error;
    }

    public void setError(Map<String, String> error) {
        this.error = error;
    }
}


