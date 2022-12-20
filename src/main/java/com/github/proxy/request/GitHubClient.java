package com.github.proxy.request;

import com.github.proxy.data.Repository;
import com.github.proxy.request.data.GitHubBranch;
import com.github.proxy.request.data.GitHubRepository;
import com.github.proxy.exception.UserNotFoundException;
import com.github.proxy.request.parsing.GitHubResponseParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GitHubClient {

    @Value("${github.api.user.request.url}")
    private String GITHUB_GET_USER_URL;
    @Value("${github.api.repositories.request.url}")
    private String GITHUB_GET_BRANCH_LIST_URL;

    private final GitHubResponseParser responseParser;

    @Autowired
    public GitHubClient(GitHubResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    public GitHubClient(GitHubResponseParser responseParser, String githubGetUserUrl, String githubGetBranchListUrl){
        this.responseParser = responseParser;
        this.GITHUB_GET_USER_URL = githubGetUserUrl;
        this.GITHUB_GET_BRANCH_LIST_URL = githubGetBranchListUrl;
    }

    public List<String> sendGetGithubUserRequest(String username) throws UserNotFoundException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        ResponseEntity<GitHubRepository[]> responseEntity =
                restTemplate.getForEntity(String.format(GITHUB_GET_USER_URL, username), GitHubRepository[].class);
        return responseParser.parseGitHubRepositoriesToRepositoriesNamesList(responseEntity);
    }

    public Repository sendGetGithubBranchListRequest(String username, String repositoryName) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        ResponseEntity<GitHubBranch[]> responseEntity =
                restTemplate.getForEntity(String.format(GITHUB_GET_BRANCH_LIST_URL, username, repositoryName), GitHubBranch[].class);
        return responseParser.parseGitHubBranchesToRepository(responseEntity, repositoryName);
    }
}
