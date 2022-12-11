package com.github.proxy.request;

import com.squareup.okhttp.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class GitHubConnectorTest {

    private final GitHubConnector gitHubConnector = new GitHubConnector();

    @Test
    void sendGitHubRequest() throws IOException {
        Response response = gitHubConnector.sendGitHubRequest(new URL("https://api.github.com/users/Wojt3kS/repos"));
        assertEquals(200, response.code());
    }

    @Test
    void sendInvalidGitHubRequest() throws IOException {
        Response response = gitHubConnector.sendGitHubRequest(new URL("https://api.github.com/users//repos"));
        assertEquals(404, response.code());
    }
}
