package com.github.proxy.request;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class GitHubConnector {

    private final String GITHUB_GET_USER_URL = "https://api.github.com/users/%s/repos";
    private final String GITHUB_GET_BRANCH_LIST_URL = "https://api.github.com/repos/%s/%s/branches";

    public Response sendGetGithubUserRequest(String username) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(new URL(String.format(GITHUB_GET_USER_URL, username)));
        return client.newCall(builder.build()).execute();
    }

    public Response sendGetGithubBranchListRequest(String username, String repositoryName) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(new URL(String.format(GITHUB_GET_BRANCH_LIST_URL, username, repositoryName)));
        return client.newCall(builder.build()).execute();
    }
}
