package com.github.proxy.controller;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    void getUserRepositoriesWithValidUserAndAcceptParam() throws IOException {
        String username = "Wojt3kS";
        String acceptHeaderParam = "application/json";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))),
                new Repository(repositoryNames.get(2), Arrays.asList(new Branch("master", "e323becf22f6d54aef5d84779795370e87da1bb4"))));
        UserRepositoriesResponse expectedRepositories = new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());
        ResponseEntity<UserRepositoriesResponse> expectedResult = ResponseEntity.ok().body(expectedRepositories);

        when(request.getHeader("Accept")).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenReturn(expectedRepositories);

        ResponseEntity<UserRepositoriesResponse> result = controller.getUserRepositories(username, request);

        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesWithValidUserAndAcceptParamWithPartialContentResponse() throws IOException {
        String username = "Wojt3kS";
        String acceptHeaderParam = "application/json";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))));
        UserRepositoriesResponse expectedRepositories = new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), "Not all repositories downloaded");
        ResponseEntity<UserRepositoriesResponse> expectedResult =  ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(expectedRepositories);

        when(request.getHeader("Accept")).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenReturn(expectedRepositories);

        ResponseEntity<UserRepositoriesResponse> result = controller.getUserRepositories(username, request);

        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesForNotExistingUser() throws IOException {
        String username = "ThisUserDoesNotExist921034854743";
        String acceptHeaderParam = "application/json";
        int expectedStatus = 404;
        String expectedMessage = "Not found";
        UserRepositoriesResponse expectedRepositories = new UserRepositoriesResponse(expectedStatus, expectedMessage);
        ResponseEntity<UserRepositoriesResponse> expectedResult = ResponseEntity.status(HttpStatus.NOT_FOUND).body(expectedRepositories);

        when(request.getHeader("Accept")).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenReturn(expectedRepositories);

        ResponseEntity<UserRepositoriesResponse> result = controller.getUserRepositories(username, request);

        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesWithValidUserAndInvalidAcceptParam() throws IOException {
        String username = "Wojt3kS";
        String acceptHeaderParam = "application/xml";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))),
                new Repository(repositoryNames.get(2), Arrays.asList(new Branch("master", "e323becf22f6d54aef5d84779795370e87da1bb4"))));
        UserRepositoriesResponse expectedRepositories = new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());
        ResponseEntity<UserRepositoriesResponse> expectedResult = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new UserRepositoriesResponse(HttpStatus.NOT_ACCEPTABLE.value(),
                        String.format("Wrong accept header param: %s Only application/json is allowed", acceptHeaderParam)));

        when(request.getHeader("Accept")).thenReturn(acceptHeaderParam);
        when(service.getUserRepositories(username)).thenReturn(expectedRepositories);

        ResponseEntity<UserRepositoriesResponse> result = controller.getUserRepositories(username, request);

        assertEquals(expectedResult, result);
    }

}