package com.github.proxy.service;

import com.github.proxy.data.Repository;
import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.request.GitHubClient;
import com.github.proxy.exception.UserNotFoundException;
import com.github.proxy.utils.ThreadPoolUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GitHubService {

    private static final Logger logger = LogManager.getLogger(GitHubService.class);

    private static final String ALL_REPOSITORIES_NOT_DOWNLOADED_MESSAGE = "Not all repositories downloaded";

    private final GitHubClient client;

    public GitHubService(GitHubClient client) {
        this.client = client;
    }

    public UserRepositoriesResponse getUserRepositories(String username) throws UserNotFoundException {
        List<String> repositoryNames;
        repositoryNames = client.sendGetGithubUserRequest(username);
        List<Repository> repositories = new ArrayList<>();
        boolean allTreadsFinishedSuccessfully = getRepositoriesDetailParallel(repositoryNames, username, repositories);
        if (allTreadsFinishedSuccessfully) {
            return new UserRepositoriesResponse(username, repositories, HttpStatus.OK.value());
        } else {
            return new UserRepositoriesResponse(username, repositories, HttpStatus.PARTIAL_CONTENT.value(), ALL_REPOSITORIES_NOT_DOWNLOADED_MESSAGE);
        }
    }

    private boolean getRepositoriesDetailParallel(List<String> repositoryNames, String username, List<Repository> repositories) {
        ThreadPoolExecutor threadPool = ThreadPoolUtils.createThreadPool(repositoryNames.size());
        AtomicBoolean noHttpErrors = new AtomicBoolean(true);
        repositoryNames.forEach(repositoryName -> threadPool.submit(() -> {
            try {
                repositories.add(client.sendGetGithubBranchListRequest(username, repositoryName));
            } catch (HttpStatusCodeException e) {
                logger.error(e);
                noHttpErrors.set(false);
            }
        }));
        return ThreadPoolUtils.shutdownThreadPool(threadPool) && noHttpErrors.get();
    }
}
