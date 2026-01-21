package com.learning.springboot.bootwebmvc.part01_web_mvc;

import java.util.Map;

public class ApiError {

    private final String message;
    private final Map<String, String> fieldErrors;

    public ApiError(String message, Map<String, String> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
