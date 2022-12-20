package com.github.proxy.utils;

import com.github.proxy.exception.WrongAcceptHeaderParamException;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public class RequestParamValidator {

    public static final String ACCEPT_JSON_HEADER_PARAM = "application/json";
    public static final String WRONG_ACCEPT_PARAM_MESSAGE = "Wrong accept header param: %s Only application/json is allowed";

    public static void validateAcceptHeaderParam(HttpServletRequest request) throws WrongAcceptHeaderParamException {
        String acceptHeaderParam = request.getHeader(HttpHeaders.ACCEPT);
        if (!ACCEPT_JSON_HEADER_PARAM.equals(acceptHeaderParam)) {
            throw new WrongAcceptHeaderParamException(String.format(WRONG_ACCEPT_PARAM_MESSAGE, acceptHeaderParam));
        }
    }
}
