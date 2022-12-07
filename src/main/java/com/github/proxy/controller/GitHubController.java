package com.github.proxy.controller;

import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.service.GitHubService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/github")
public class GitHubController {

    private static final Logger logger = LogManager.getLogger(GitHubController.class);

    private final String ACCEPT_HEADER_PARAM = "Accept";
    private final String ACCEPT_JSON_HEADER_PARAM = "application/json";
    private final String WRONG_ACCEPT_PARAM_MESSAGE = "Wrong accept header param: %s Only application/json is allowed";
    private final String UNKNOWN_SERVER_ERROR = "Unknown server error occurred";

    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    @GetMapping(path = "/user/repos/{username}", produces = "application/json")
    public ResponseEntity<UserRepositoriesResponse> getUserRepositories(@PathVariable String username, HttpServletRequest request) {
        String acceptHeaderParam = request.getHeader(ACCEPT_HEADER_PARAM);
        if (!ACCEPT_JSON_HEADER_PARAM.equals(acceptHeaderParam)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserRepositoriesResponse(HttpStatus.NOT_ACCEPTABLE.value(), String.format(WRONG_ACCEPT_PARAM_MESSAGE, acceptHeaderParam)));
        }
        UserRepositoriesResponse userRepositories = null;
        try {
            userRepositories = service.getUserRepositories(username);
        } catch (IOException e) {
            logger.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserRepositoriesResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), UNKNOWN_SERVER_ERROR));
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
