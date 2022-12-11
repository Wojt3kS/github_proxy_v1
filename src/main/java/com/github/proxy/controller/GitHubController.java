package com.github.proxy.controller;

import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.service.GitHubService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/github")
public class GitHubController {

    private static final Logger logger = LogManager.getLogger(GitHubController.class);

    private final String ACCEPT_HEADER_PARAM = "Accept";
    private final String ACCEPT_JSON_HEADER_PARAM = "application/json";
    private final String WRONG_ACCEPT_PARAM_MESSAGE = "Wrong accept header param: %s Only application/json is allowed";

    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    @ExceptionHandler(value = IOException.class)
    @GetMapping(path = "/user/repos/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserRepositoriesResponse> getUserRepositories(@PathVariable String username, HttpServletRequest request) throws IOException {
        String acceptHeaderParam = request.getHeader(ACCEPT_HEADER_PARAM);
        if (!ACCEPT_JSON_HEADER_PARAM.equals(acceptHeaderParam)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new UserRepositoriesResponse(HttpStatus.NOT_ACCEPTABLE.value(), String.format(WRONG_ACCEPT_PARAM_MESSAGE, acceptHeaderParam)));
        }
        UserRepositoriesResponse userRepositories;
        try {
            userRepositories = service.getUserRepositories(username);
        } catch (IOException e) {
            logger.error(e);
            throw (e);
        }
        if (HttpStatus.OK.value() == userRepositories.getStatus()) {
            return ResponseEntity.ok().body(userRepositories);
        } else if (HttpStatus.PARTIAL_CONTENT.value() == userRepositories.getStatus()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(userRepositories);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userRepositories);
        }
    }
}
