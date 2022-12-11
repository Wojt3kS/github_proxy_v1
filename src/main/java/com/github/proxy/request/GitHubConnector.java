package com.github.proxy.request;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class GitHubConnector {

    public Response sendGitHubRequest(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder().url(url);
        return client.newCall(builder.build()).execute();
    }
}
