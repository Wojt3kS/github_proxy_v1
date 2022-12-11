package com.github.proxy.request;

import com.github.proxy.data.Repository;
import com.github.proxy.request.exception.UserNotFoundException;
import com.github.proxy.request.parsing.GitHubResponseParser;
import com.squareup.okhttp.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;

@Component
public class GitHubClient {

    private final String GITHUB_GET_USER_URL = "https://api.github.com/users/%s/repos";
    private final String GITHUB_GET_BRANCH_LIST_URL = "https://api.github.com/repos/%s/%s/branches";

    private final GitHubConnector connector;
    private final GitHubResponseParser responseParser;

    public GitHubClient(GitHubConnector connector, GitHubResponseParser responseParser) {
        this.connector = connector;
        this.responseParser = responseParser;
    }

    public List<String> sendGetGithubUserRequest(String username) throws IOException, UserNotFoundException {
        Response response = connector.sendGitHubRequest(new URL(String.format(GITHUB_GET_USER_URL, username)));
        int responseCode = response.code();
        if (response.code() != HttpStatus.OK.value()) {
            throw new UserNotFoundException(response.message(), responseCode);
        }
        Reader responseReader = response.body().charStream();
        List<String> repositoriesNames = responseParser.parseUserResponseToRepositoriesNamesList(responseReader);
        responseReader.close();
        return repositoriesNames;
    }

    public Repository sendGetGithubBranchListRequest(String username, String repositoryName) throws IOException {
        Response response = connector.sendGitHubRequest(new URL(String.format(GITHUB_GET_BRANCH_LIST_URL, username, repositoryName)));
        Reader responseReader = response.body().charStream();
        Repository repository = responseParser.parseRepositoriesResponseToRepository(responseReader, repositoryName);
        responseReader.close();
        return repository;
    }
}
