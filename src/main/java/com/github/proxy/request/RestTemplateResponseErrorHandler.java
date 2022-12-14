package com.github.proxy.request;

import com.github.proxy.exception.OutOfGitHubRequestsException;
import com.github.proxy.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
                httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {

        if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.SERVER_ERROR) {
            throw new HttpServerErrorException(httpResponse.getStatusCode());
        } else if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException(httpResponse.getStatusText(), httpResponse.getStatusCode().value());
            } else if (httpResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new OutOfGitHubRequestsException();
            }
            throw new HttpClientErrorException(httpResponse.getStatusCode());
        }
    }
}