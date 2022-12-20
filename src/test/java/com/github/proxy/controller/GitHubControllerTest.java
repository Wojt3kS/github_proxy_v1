package com.github.proxy.controller;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.exception.UserNotFoundException;
import com.github.proxy.exception.WrongAcceptHeaderParamException;
import com.github.proxy.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubControllerTest {

    @Mock
    private GitHubService service;
    @Mock
    private HttpServletRequest request;

    private GitHubController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new GitHubController(service);
    }

    @Test
    void getUserRepositoriesWithValidUserAndAcceptParam() {
        String username = "Wojt3kS";
        String acceptHeaderParam = "application/json";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))),
                new Repository(repositoryNames.get(2), Arrays.asList(new Branch("master", "e323becf22f6d54aef5d84779795370e87da1bb4"))));
        UserRepositoriesResponse expectedRepositories = new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());
        ResponseEntity<UserRepositoriesResponse> expectedResult = ResponseEntity.status(HttpStatus.OK).body(expectedRepositories);

        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenReturn(expectedRepositories);

        ResponseEntity<UserRepositoriesResponse> result = controller.getUserRepositories(username, request);

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verifyNoMoreInteractions(request);
        verify(service).getUserRepositories(username);
        verifyNoMoreInteractions(service);
        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesWithValidUserAndAcceptParamWithPartialContentResponse() {
        String username = "Wojt3kS";
        String acceptHeaderParam = "application/json";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))));
        UserRepositoriesResponse expectedRepositories = new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), "Not all repositories downloaded");
        ResponseEntity<UserRepositoriesResponse> expectedResult =  ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(expectedRepositories);

        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenReturn(expectedRepositories);

        ResponseEntity<UserRepositoriesResponse> result = controller.getUserRepositories(username, request);

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verifyNoMoreInteractions(request);
        verify(service).getUserRepositories(username);
        verifyNoMoreInteractions(service);
        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesForNotExistingUser() {
        String username = "ThisUserDoesNotExist921034854743";
        String acceptHeaderParam = "application/json";
        int expectedStatus = 404;
        String expectedMessage = "Not found";

        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenThrow(new UserNotFoundException(expectedMessage, expectedStatus));

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> controller.getUserRepositories(username, request));

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verifyNoMoreInteractions(request);
        verify(service).getUserRepositories(username);
        verifyNoMoreInteractions(service);
        assertEquals(expectedMessage, userNotFoundException.getMessage());
        assertEquals(expectedStatus, userNotFoundException.getResponseCode());
    }

    @Test
    void getUserRepositoriesWithValidUserAndInvalidAcceptParam() {
        String username = "Wojt3kS";
        String acceptHeaderParam = "application/xml";
        String expectedMessage = "Wrong accept header param: application/xml Only application/json is allowed";

        when(request.getHeader(HttpHeaders.ACCEPT)).thenReturn(acceptHeaderParam);

        WrongAcceptHeaderParamException wrongAcceptHeaderParamException = assertThrows(WrongAcceptHeaderParamException.class,
                () -> controller.getUserRepositories(username, request));

        verify(request).getHeader(HttpHeaders.ACCEPT);
        verifyNoMoreInteractions(request);
        assertEquals(expectedMessage, wrongAcceptHeaderParamException.getMessage());
    }

}