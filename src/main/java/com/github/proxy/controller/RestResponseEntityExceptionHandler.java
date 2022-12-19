package com.github.proxy.controller;

import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.exception.OutOfGitHubRequestsException;
import com.github.proxy.exception.UserNotFoundException;
import com.github.proxy.exception.WrongAcceptHeaderParamException;
import com.github.proxy.utils.RequestParamValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    protected ResponseEntity<Object> handleUserNotFound(UserNotFoundException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new UserRepositoriesResponse(e.getResponseCode(), e.getMessage()));
    }

    @ExceptionHandler(value = {WrongAcceptHeaderParamException.class})
    protected ResponseEntity<UserRepositoriesResponse> handleWrongAcceptHeaderParam(WrongAcceptHeaderParamException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new UserRepositoriesResponse(HttpStatus.NOT_ACCEPTABLE.value(),
                        String.format(RequestParamValidator.WRONG_ACCEPT_PARAM_MESSAGE, request.getHeader(RequestParamValidator.ACCEPT_HEADER_PARAM))));
    }

    @ExceptionHandler(value = {OutOfGitHubRequestsException.class})
    protected ResponseEntity<UserRepositoriesResponse> handleOutOfGitHubApiRequests(OutOfGitHubRequestsException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new UserRepositoriesResponse(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler(value = {HttpStatusCodeException.class})
    protected ResponseEntity<UserRepositoriesResponse> handleGitHubApiError(HttpStatusCodeException e, WebRequest request) {
        return ResponseEntity.status(e.getStatusCode())
                .body(new UserRepositoriesResponse(e.getStatusCode().value(), e.getMessage()));
    }

}