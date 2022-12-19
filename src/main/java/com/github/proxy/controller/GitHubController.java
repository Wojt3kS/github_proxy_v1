package com.github.proxy.controller;

import com.github.proxy.data.UserRepositoriesResponse;
import com.github.proxy.service.GitHubService;
import com.github.proxy.utils.RequestParamValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/github")
public class GitHubController {

    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    @GetMapping(path = "/user/repos/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserRepositoriesResponse> getUserRepositories(@PathVariable String username, HttpServletRequest request) {
        RequestParamValidator.validateAcceptHeaderParam(request);
        UserRepositoriesResponse userRepositories = service.getUserRepositories(username);
        return ResponseEntity.status(HttpStatus.valueOf(userRepositories.getStatus())).body(userRepositories);
    }
}
