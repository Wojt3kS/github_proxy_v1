package com.github.proxy.request.parsing;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import com.github.proxy.request.data.GitHubBranch;
import com.github.proxy.request.data.GitHubRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitHubResponseParser {

    public List<String> parseUserResponseToRepositoriesNamesList(Reader responseReader) {
        Gson gson = new Gson();
        Type gitHubRepositoryListType = new TypeToken<ArrayList<GitHubRepository>>(){}.getType();
        List<GitHubRepository> userRepositories = gson.fromJson(responseReader, gitHubRepositoryListType);
        return userRepositories.stream()
                .filter(r -> !r.isFork())
                .map(GitHubRepository::getName)
                .collect(Collectors.toList());
    }

    public Repository parseRepositoriesResponseToRepository(Reader responseReader, String repositoryName) {
        Gson gson = new Gson();
        Type gitHubBranchListType = new TypeToken<ArrayList<GitHubBranch>>(){}.getType();
        List<GitHubBranch> gitHubBranches = gson.fromJson(responseReader, gitHubBranchListType);
        List<Branch> branches = gitHubBranches.stream()
                .map(b -> new Branch(b.getName(), b.getCommit().getSha()))
                .collect(Collectors.toList());
        return new Repository(repositoryName, branches);
    }
}
