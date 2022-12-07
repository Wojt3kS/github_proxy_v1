package com.github.proxy.service;

import com.github.proxy.controller.GitHubController;
import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.request.GitHubConnector;
import com.github.proxy.request.parsing.GitHubResponseParser;
import com.squareup.okhttp.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class GitHubService {

    private static final Logger logger = LogManager.getLogger(GitHubService.class);

    private static final String ALL_REPOSITORIES_NOT_DOWNLOADED_MESSAGE = "Not all repositories downloaded";

    private final GitHubConnector connector;

    public GitHubService(GitHubConnector connector) {
        this.connector = connector;
    }

    public UserRepositoriesResponse getUserRepositories(String username) throws IOException {
        Response userResponse = connector.sendGetGithubUserRequest(username);
        int responseCode = userResponse.code();
        String responseBody = userResponse.body().string();
        if (userResponse.code() != HttpStatus.OK.value()) {
            return new UserRepositoriesResponse(responseCode, userResponse.message());
        }
        GitHubResponseParser responseParser = new GitHubResponseParser();
        List<String> repositoryNames = responseParser.parseUserResponseToRepositoriesNamesList(responseBody);
        List<Repository> repositories = new ArrayList<>();
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.min(32, repositoryNames.size()));
        for (String repositoryName : repositoryNames) {
            threadPool.submit(() -> {
                try {

                    Response branchesResponse = connector.sendGetGithubBranchListRequest(username, repositoryName);
                    String branchesResponseBody = branchesResponse.body().string();
                    Repository repository = responseParser.parseRepositoriesResponseToRepository(branchesResponseBody, repositoryName);
                    repositories.add(repository);
                } catch (IOException e) {
                    logger.error(e);
                }
            });

        }
        threadPool.shutdown();
        boolean allTreadsFinished;
        try {
            allTreadsFinished = threadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e);
            allTreadsFinished = false;
        }
        if (allTreadsFinished) {
            return new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());
        } else {
            return new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), ALL_REPOSITORIES_NOT_DOWNLOADED_MESSAGE);
        }
    }
}
