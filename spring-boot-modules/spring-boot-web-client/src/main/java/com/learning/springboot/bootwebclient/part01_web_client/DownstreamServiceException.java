package com.learning.springboot.bootwebclient.part01_web_client;

public class DownstreamServiceException extends RuntimeException {

    private final int status;
    private final String errorBody;

    public DownstreamServiceException(int status, String message) {
        this(status, message, null);
    }

    public DownstreamServiceException(int status, String message, String errorBody) {
        super(message);
        this.status = status;
        this.errorBody = errorBody;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorBody() {
        return errorBody;
    }
}
