# GitHubProxy

GitHubProxy allows user to get user repository data in simple form. All data is directly from GitHub API

## Usage

Available methods:

GET

getUserRepositories

/github/user/repos/{username}

accept: application/json header required (406) in other cases

Possible responses:

200 - OK

404 - user does not exist

406 - no accept: application/json in header

500 - server error

Response body example:

```json
{
  "ownerLogin": "username",
  "repos": [
    {
      "name": "repositoryName",
      "branches": [
        {
          "name": "branchName",
          "lastCommitSha": "lastCommitSha"
        }
      ]
    }
  ],
  "status": "responseStatus"
}
```



## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.