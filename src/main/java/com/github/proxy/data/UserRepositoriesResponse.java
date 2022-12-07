package com.github.proxy.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRepositoriesResponse {


    private String ownerLogin;
    private List<Repository> repos;
    private Integer status;
    private String message;

    public UserRepositoriesResponse(String ownerLogin, List<Repository> repos, int status) {
        this.ownerLogin = ownerLogin;
        this.repos = repos;
        this.status = status;
    }

    public UserRepositoriesResponse(String ownerLogin, List<Repository> repos, int status, String message) {
        this.ownerLogin = ownerLogin;
        this.repos = repos;
        this.status = status;
        this.message = message;
    }

    public UserRepositoriesResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public UserRepositoriesResponse() {

    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public List<Repository> getRepos() {
        return repos;
    }

    public void setRepos(List<Repository> repos) {
        this.repos = repos;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
