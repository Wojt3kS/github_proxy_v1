package com.github.proxy.request.exception;

public class UserNotFoundException extends Exception {

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
