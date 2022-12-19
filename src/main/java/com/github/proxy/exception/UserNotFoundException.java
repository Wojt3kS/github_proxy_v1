package com.github.proxy.exception;

public class UserNotFoundException extends RuntimeException {

    private int responseCode;

    public UserNotFoundException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
