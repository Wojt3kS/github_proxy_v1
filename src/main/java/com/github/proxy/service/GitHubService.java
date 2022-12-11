package com.github.proxy.service;

import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.request.GitHubClient;
import com.github.proxy.request.exception.UserNotFoundException;
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
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GitHubService {

    private static final Logger logger = LogManager.getLogger(GitHubService.class);

    private static final String ALL_REPOSITORIES_NOT_DOWNLOADED_MESSAGE = "Not all repositories downloaded";
    private static final int MAX_THREADS = 32;
    private static final int MAX_THREAD_WAIT_TIME = 30;

    private final GitHubClient client;

    public GitHubService(GitHubClient client) {
        this.client = client;
    }

    public UserRepositoriesResponse getUserRepositories(String username) throws IOException {
        List<String> repositoryNames;
        try {
            repositoryNames = client.sendGetGithubUserRequest(username);
        } catch (UserNotFoundException e) {
            logger.info(e);
            return new UserRepositoriesResponse(e.getResponseCode(), e.getMessage());
        }
        List<Repository> repositories = new ArrayList<>();
        boolean allTreadsFinishedSuccessfully = getRepositoriesDetailParallel(repositoryNames, username, repositories);
        if (allTreadsFinishedSuccessfully) {
            return new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());
        } else {
            return new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), ALL_REPOSITORIES_NOT_DOWNLOADED_MESSAGE);
        }
    }

    private boolean getRepositoriesDetailParallel(List<String> repositoryNames, String username, List<Repository> repositories) {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Math.min(MAX_THREADS, repositoryNames.size()));
        AtomicBoolean noThreadErrors = new AtomicBoolean(true);
        boolean allThreadsFinished;
        for (String repositoryName : repositoryNames) {
            threadPool.submit(() -> {
                try {
                    repositories.add(client.sendGetGithubBranchListRequest(username, repositoryName));
                } catch (IOException e) {
                    logger.error(e);
                    noThreadErrors.set(false);
                }
            });

        }
        threadPool.shutdown();
        try {
            allThreadsFinished = threadPool.awaitTermination(MAX_THREAD_WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e);
            noThreadErrors.set(false);
            allThreadsFinished = false;
        }
        return noThreadErrors.get() && allThreadsFinished;
    }
}
