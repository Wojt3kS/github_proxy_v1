package com.github.proxy.integration;

import com.github.proxy.SpringFoxConfig;
import com.github.proxy.controller.GitHubController;
import com.github.proxy.controller.RestResponseEntityExceptionHandler;
import com.github.proxy.exception.UserNotFoundException;
import com.github.proxy.exception.WrongAcceptHeaderParamException;
import com.github.proxy.request.GitHubClient;
import com.github.proxy.request.parsing.GitHubResponseParser;
import com.github.proxy.service.GitHubService;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringFoxConfig.class, GitHubController.class, GitHubResponseParser.class, GitHubClient.class, GitHubService.class,
        RestResponseEntityExceptionHandler.class})
@WebAppConfiguration
@WebMvcTest
@WireMockTest(httpPort = 8083)
public class GitHubControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void verifyTestConfiguration() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(webApplicationContext.getBean(GitHubController.class));
    }

    @Test
    public void getUserRepositoriesWithValidUserAndAcceptParam() throws Exception {
        String expectedUserResponseBody = "[\n{\n\"id\": 129908121,\n\"node_id\": \"MDEwOlJlcG9zaXRvcnkxMjk5MDgxMjE=\",\n\"name\": \"basegrid\",\n\"full_name\": \"okydk/basegrid\",\n\"private\": false,\n\"owner\": {\n\"login\": \"okydk\",\n\"id\": 5545171,\n\"node_id\": \"MDQ6VXNlcjU1NDUxNzE=\",\n\"avatar_url\": \"https://avatars.githubusercontent.com/u/5545171?v=4\",\n\"gravatar_id\": \"\",\n\"url\": \"https://api.github.com/users/okydk\",\n\"html_url\": \"https://github.com/okydk\",\n\"followers_url\": \"https://api.github.com/users/okydk/followers\",\n\"following_url\": \"https://api.github.com/users/okydk/following{/other_user}\",\n\"gists_url\": \"https://api.github.com/users/okydk/gists{/gist_id}\",\n\"starred_url\": \"https://api.github.com/users/okydk/starred{/owner}{/repo}\",\n\"subscriptions_url\": \"https://api.github.com/users/okydk/subscriptions\",\n\"organizations_url\": \"https://api.github.com/users/okydk/orgs\",\n\"repos_url\": \"https://api.github.com/users/okydk/repos\",\n\"events_url\": \"https://api.github.com/users/okydk/events{/privacy}\",\n\"received_events_url\": \"https://api.github.com/users/okydk/received_events\",\n\"type\": \"User\",\n\"site_admin\": false\n},\n\"html_url\": \"https://github.com/okydk/basegrid\",\n\"description\": \"Dynamic CSS-grid base on CSS-variables. \uD83D\uDD25\",\n\"fork\": false}]";
        stubFor(get("/users/okydk/repos").withHost(equalTo("localhost")).willReturn(okJson(expectedUserResponseBody)));
        String expectedRepositoriesResponseBody = "[{\"name\":\"master\",\"commit\":{\"sha\":\"a2eda2a1e6f7147c7b9019e7e2389105fd84e43d9\",\"url\":\"https://api.github.com/repos/okydk/basegrid/commits/a2eda2a1e6f7147c7b9019e7e2389105fd84e43d\"},\"protected\":false}]";
        stubFor(get("/repos/okydk/basegrid/branches").withHost(equalTo("localhost")).willReturn(okJson(expectedRepositoriesResponseBody)));


        this.mockMvc.perform(get("/github/user/repos/{username}", "okydk")
                .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.ownerLogin").value("okydk"))
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.repos[0].name").value("basegrid"))
                .andExpect(jsonPath("$.repos[0].branches[0].name").value("master"))
                .andExpect(jsonPath("$.repos[0].branches[0].lastCommitSha").value("a2eda2a1e6f7147c7b9019e7e2389105fd84e43d9"));


    }

    @Test
    public void getUserRepositoriesWithValidUserAndInvalidAcceptParam() throws Exception {
        this.mockMvc.perform(get("/github/user/repos/{username}", "Wojt3kS")
                .header("Accept", "application/json,application/xml"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WrongAcceptHeaderParamException))
                .andExpect(status().is(406))
                .andExpect(jsonPath("$.message")
                        .value("Wrong accept header param: application/json,application/xml Only application/json is allowed"));
    }

    @Test
    public void getUserRepositoriesWithInvalidUserAndValidAcceptParam() throws Exception {
        int expectedStatus = 404;
        String expectedResponseBody = "{\n\"message\": \"Not Found\",\n\"documentation_url\": \"https://docs.github.com/rest/reference/repos#list-repositories-for-a-user\n}";
        stubFor(get("/users/ThisUserDoesNotExist/repos").withHost(equalTo("localhost")).willReturn(jsonResponse(expectedResponseBody, expectedStatus)));

        this.mockMvc.perform(get("/github/user/repos/{username}", "ThisUserDoesNotExist")
                .header("Accept", "application/json"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message")
                        .value("Not Found"));
    }

}