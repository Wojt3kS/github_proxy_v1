package com.github.proxy.service;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.request.GitHubClient;
import com.github.proxy.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GitHubServiceTest {

    @Mock
    private GitHubClient client;

    private GitHubService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GitHubService(client);
    }

    @Test
    void getUserRepositoriesForExistingUser() {
        String username = "Wojt3kS";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))),
                new Repository(repositoryNames.get(2), Arrays.asList(new Branch("master", "e323becf22f6d54aef5d84779795370e87da1bb4"))));
        UserRepositoriesResponse expectedResult = new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());

        when(client.sendGetGithubUserRequest(username)).thenReturn(repositoryNames);
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(0))).thenReturn(repositories.get(0));
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(1))).thenReturn(repositories.get(1));
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(2))).thenReturn(repositories.get(2));

        UserRepositoriesResponse result = service.getUserRepositories(username);

        verify(client).sendGetGithubUserRequest(username);
        verify(client).sendGetGithubBranchListRequest(username, repositoryNames.get(0));
        verify(client).sendGetGithubBranchListRequest(username, repositoryNames.get(1));
        verify(client).sendGetGithubBranchListRequest(username, repositoryNames.get(2));
        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesForExistingUserWithPartialAnswer() {
        String username = "Wojt3kS";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))));
        UserRepositoriesResponse expectedResult = new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), "Not all repositories downloaded");


        when(client.sendGetGithubUserRequest(username)).thenReturn(repositoryNames);
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(0))).thenReturn(repositories.get(0));
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(1))).thenReturn(repositories.get(1));
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(2))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        UserRepositoriesResponse result = service.getUserRepositories(username);

        verify(client).sendGetGithubUserRequest(username);
        verify(client).sendGetGithubBranchListRequest(username, repositoryNames.get(0));
        verify(client).sendGetGithubBranchListRequest(username, repositoryNames.get(1));
        verify(client).sendGetGithubBranchListRequest(username, repositoryNames.get(2));
        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesForNotExistingUser() {
        String username = "ThisUserDoesNotExist921034854743";
        int expectedStatus = 404;
        String expectedMessage = "Not found";

        when(client.sendGetGithubUserRequest(username)).thenThrow(new UserNotFoundException(expectedMessage, expectedStatus));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.getUserRepositories(username));

        verify(client).sendGetGithubUserRequest(username);
        assertEquals(expectedStatus, exception.getResponseCode());
        assertEquals(expectedMessage, exception.getMessage());
    }
}