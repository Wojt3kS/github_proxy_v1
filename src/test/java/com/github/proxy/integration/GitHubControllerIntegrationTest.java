package com.github.proxy.integration;

import com.github.proxy.SpringFoxConfig;
import com.github.proxy.controller.GitHubController;
import com.github.proxy.request.GitHubClient;
import com.github.proxy.request.GitHubConnector;
import com.github.proxy.request.parsing.GitHubResponseParser;
import com.github.proxy.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringFoxConfig.class, GitHubController.class, GitHubResponseParser.class, GitHubClient.class,
        GitHubConnector.class, GitHubService.class})
@WebAppConfiguration
@WebMvcTest
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
        this.mockMvc.perform(get("/github/user/repos/{username}", "Wojt3kS")
                .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.ownerLogin").value("Wojt3kS"));
    }

    @Test
    public void getUserRepositoriesWithValidUserAndInvalidAcceptParam() throws Exception {
        this.mockMvc.perform(get("/github/user/repos/{username}", "Wojt3kS")
                .header("Accept", "application/json,application/xml"))
                .andExpect(status().is(406))
                .andExpect(jsonPath("$.message")
                        .value("Wrong accept header param: application/json,application/xml Only application/json is allowed"));
    }

    @Test
    public void getUserRepositoriesWithInvalidUserAndValidAcceptParam() throws Exception {
        this.mockMvc.perform(get("/github/user/repos/{username}", "ThisUserDoesNotExist921034854743")
                .header("Accept", "application/json"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.message")
                        .value("Not Found"));
    }

}