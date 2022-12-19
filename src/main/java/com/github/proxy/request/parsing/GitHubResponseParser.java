package com.github.proxy.request.parsing;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import com.github.proxy.request.data.GitHubBranch;
import com.github.proxy.request.data.GitHubRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GitHubResponseParser {

    public List<String> parseGitHubRepositoriesToRepositoriesNamesList(ResponseEntity<GitHubRepository[]> responseEntity) {
        return Arrays.stream(Objects.requireNonNull(responseEntity.getBody()))
                .filter(r -> !r.isFork())
                .map(GitHubRepository::getName)
                .collect(Collectors.toList());
    }

    public Repository parseGitHubBranchesToRepository(ResponseEntity<GitHubBranch[]> responseEntity, String repositoryName) {
        List<Branch> branches = Arrays.stream(Objects.requireNonNull(responseEntity.getBody()))
                .map(b -> new Branch(b.getName(), b.getCommit().getSha()))
                .collect(Collectors.toList());
        return new Repository(repositoryName, branches);
    }
}
