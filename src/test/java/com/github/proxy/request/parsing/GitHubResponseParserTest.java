package com.github.proxy.request.parsing;

import com.github.proxy.data.Branch;
import com.github.proxy.data.Repository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitHubResponseParserTest {

    private final GitHubResponseParser gitHubResponseParser = new GitHubResponseParser();

    @Test
    void parseUserResponseToRepositoriesNamesList() {
        String responseToParse = "[{\"id\":568551,\"node_id\":\"MDEwOlJlcG9zaXRvcnk1Njg1NTE=\",\"name\":\"SolMan\",\"full_name\":\"Stefan/SolMan\",\"private\":false,\"owner\":{\"login\":\"Stefan\",\"id\":2061,\"node_id\":\"MDQ6VXNlcjIwNjE=\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/2061?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/Stefan\",\"html_url\":\"https://github.com/Stefan\",\"followers_url\":\"https://api.github.com/users/Stefan/followers\",\"following_url\":\"https://api.github.com/users/Stefan/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/Stefan/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/Stefan/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/Stefan/subscriptions\",\"organizations_url\":\"https://api.github.com/users/Stefan/orgs\",\"repos_url\":\"https://api.github.com/users/Stefan/repos\",\"events_url\":\"https://api.github.com/users/Stefan/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/Stefan/received_events\",\"type\":\"User\",\"site_admin\":false},\"html_url\":\"https://github.com/Stefan/SolMan\",\"description\":\"Homepage\",\"fork\":false,\"url\":\"https://api.github.com/repos/Stefan/SolMan\",\"forks_url\":\"https://api.github.com/repos/Stefan/SolMan/forks\",\"keys_url\":\"https://api.github.com/repos/Stefan/SolMan/keys{/key_id}\",\"collaborators_url\":\"https://api.github.com/repos/Stefan/SolMan/collaborators{/collaborator}\",\"teams_url\":\"https://api.github.com/repos/Stefan/SolMan/teams\",\"hooks_url\":\"https://api.github.com/repos/Stefan/SolMan/hooks\",\"issue_events_url\":\"https://api.github.com/repos/Stefan/SolMan/issues/events{/number}\",\"events_url\":\"https://api.github.com/repos/Stefan/SolMan/events\",\"assignees_url\":\"https://api.github.com/repos/Stefan/SolMan/assignees{/user}\",\"branches_url\":\"https://api.github.com/repos/Stefan/SolMan/branches{/branch}\",\"tags_url\":\"https://api.github.com/repos/Stefan/SolMan/tags\",\"blobs_url\":\"https://api.github.com/repos/Stefan/SolMan/git/blobs{/sha}\",\"git_tags_url\":\"https://api.github.com/repos/Stefan/SolMan/git/tags{/sha}\",\"git_refs_url\":\"https://api.github.com/repos/Stefan/SolMan/git/refs{/sha}\",\"trees_url\":\"https://api.github.com/repos/Stefan/SolMan/git/trees{/sha}\",\"statuses_url\":\"https://api.github.com/repos/Stefan/SolMan/statuses/{sha}\",\"languages_url\":\"https://api.github.com/repos/Stefan/SolMan/languages\",\"stargazers_url\":\"https://api.github.com/repos/Stefan/SolMan/stargazers\",\"contributors_url\":\"https://api.github.com/repos/Stefan/SolMan/contributors\",\"subscribers_url\":\"https://api.github.com/repos/Stefan/SolMan/subscribers\",\"subscription_url\":\"https://api.github.com/repos/Stefan/SolMan/subscription\",\"commits_url\":\"https://api.github.com/repos/Stefan/SolMan/commits{/sha}\",\"git_commits_url\":\"https://api.github.com/repos/Stefan/SolMan/git/commits{/sha}\",\"comments_url\":\"https://api.github.com/repos/Stefan/SolMan/comments{/number}\",\"issue_comment_url\":\"https://api.github.com/repos/Stefan/SolMan/issues/comments{/number}\",\"contents_url\":\"https://api.github.com/repos/Stefan/SolMan/contents/{+path}\",\"compare_url\":\"https://api.github.com/repos/Stefan/SolMan/compare/{base}...{head}\",\"merges_url\":\"https://api.github.com/repos/Stefan/SolMan/merges\",\"archive_url\":\"https://api.github.com/repos/Stefan/SolMan/{archive_format}{/ref}\",\"downloads_url\":\"https://api.github.com/repos/Stefan/SolMan/downloads\",\"issues_url\":\"https://api.github.com/repos/Stefan/SolMan/issues{/number}\",\"pulls_url\":\"https://api.github.com/repos/Stefan/SolMan/pulls{/number}\",\"milestones_url\":\"https://api.github.com/repos/Stefan/SolMan/milestones{/number}\",\"notifications_url\":\"https://api.github.com/repos/Stefan/SolMan/notifications{?since,all,participating}\",\"labels_url\":\"https://api.github.com/repos/Stefan/SolMan/labels{/name}\",\"releases_url\":\"https://api.github.com/repos/Stefan/SolMan/releases{/id}\",\"deployments_url\":\"https://api.github.com/repos/Stefan/SolMan/deployments\",\"created_at\":\"2010-03-18T17:05:00Z\",\"updated_at\":\"2012-12-14T02:35:12Z\",\"pushed_at\":null,\"git_url\":\"git://github.com/Stefan/SolMan.git\",\"ssh_url\":\"git@github.com:Stefan/SolMan.git\",\"clone_url\":\"https://github.com/Stefan/SolMan.git\",\"svn_url\":\"https://github.com/Stefan/SolMan\",\"homepage\":\"\",\"size\":48,\"stargazers_count\":1,\"watchers_count\":1,\"language\":null,\"has_issues\":true,\"has_projects\":true,\"has_downloads\":true,\"has_wiki\":true,\"has_pages\":false,\"has_discussions\":false,\"forks_count\":0,\"mirror_url\":null,\"archived\":false,\"disabled\":false,\"open_issues_count\":0,\"license\":null,\"allow_forking\":true,\"is_template\":false,\"web_commit_signoff_required\":false,\"topics\":[],\"visibility\":\"public\",\"forks\":0,\"open_issues\":0,\"watchers\":1,\"default_branch\":\"master\"}]";
        List<String> expectedResult = Arrays.asList("SolMan");
        List<String> wrongResult = Arrays.asList("SolMan", "Koza");
        List<String> result = gitHubResponseParser.parseUserResponseToRepositoriesNamesList(responseToParse);
        assertEquals(expectedResult, result);
        assertNotEquals(wrongResult, result);
    }

    @Test
    void parseRepositoriesResponseToRepository() {
        String responseToParse = "[{\"name\":\"master\",\"commit\":{\"sha\":\"a2eda2a1e6f7147c7b9019e7e2389105fd84e43d\",\"url\":\"https://api.github.com/repos/okydk/basegrid/commits/a2eda2a1e6f7147c7b9019e7e2389105fd84e43d\"},\"protected\":false}]";
        Repository expectedResult = new Repository("basegrid", Arrays.asList(new Branch("master", "a2eda2a1e6f7147c7b9019e7e2389105fd84e43d")));
        Repository result = gitHubResponseParser.parseRepositoriesResponseToRepository(responseToParse, "basegrid");
        assertEquals(expectedResult.getName(), result.getName());
        assertEquals(expectedResult.getBranches().get(0).getName(), result.getBranches().get(0).getName());
        assertEquals(expectedResult.getBranches().get(0).getLastCommitSha(), result.getBranches().get(0).getLastCommitSha());
    }
}