package com.github.proxy.service;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.request.GitHubClient;
import com.github.proxy.request.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void getUserRepositoriesForExistingUser() throws IOException, UserNotFoundException {
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

        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesForExistingUserWithPartialAnswer() throws IOException, UserNotFoundException {
        String username = "Wojt3kS";
        List<String> repositoryNames = Arrays.asList("Algoritms", "github_proxy_v1", "MyTwitter");
        List<Repository> repositories = Arrays.asList(
                new Repository(repositoryNames.get(0), Arrays.asList(new Branch("master", "0271198c6e7bfc0c082b799a363f0547cf8eb287"))),
                new Repository(repositoryNames.get(1), Arrays.asList(new Branch("master", "7dec665362c527088cc0f6c2dfc75b9c6f410b4e"))));
        UserRepositoriesResponse expectedResult = new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), "Not all repositories downloaded");


        when(client.sendGetGithubUserRequest(username)).thenReturn(repositoryNames);
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(0))).thenReturn(repositories.get(0));
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(1))).thenReturn(repositories.get(1));
        when(client.sendGetGithubBranchListRequest(username, repositoryNames.get(2))).thenThrow(new IOException());

        UserRepositoriesResponse result = service.getUserRepositories(username);

        assertEquals(expectedResult, result);
    }

    @Test
    void getUserRepositoriesForNotExistingUser() throws IOException, UserNotFoundException {
        String username = "ThisUserDoesNotExist921034854743";
        int expectedStatus = 404;
        String expectedMessage = "Not found";
        UserRepositoriesResponse expectedResult = new UserRepositoriesResponse(expectedStatus, expectedMessage);

        when(client.sendGetGithubUserRequest(username)).thenThrow(new UserNotFoundException(expectedMessage, expectedStatus));

        UserRepositoriesResponse result = service.getUserRepositories(username);

        assertEquals(expectedResult, result);
    }
}