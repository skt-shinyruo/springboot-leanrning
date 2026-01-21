package com.learning.springboot.bootwebclient.part01_web_client;

public class DownstreamServiceException extends RuntimeException {

    private final int status;

    public DownstreamServiceException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

