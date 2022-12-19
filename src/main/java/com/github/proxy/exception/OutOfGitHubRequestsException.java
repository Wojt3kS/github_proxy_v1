package com.github.proxy.exception;

public class OutOfGitHubRequestsException extends RuntimeException {

    private static final String OUT_OF_REQUESTS_MESSAGE = "Request is currently impossible. GitHub API rate limits exceeded";

    @Override
    public String getMessage() {
        return OUT_OF_REQUESTS_MESSAGE;
    }
}
