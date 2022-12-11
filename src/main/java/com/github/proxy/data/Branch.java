package com.github.proxy.data;

import java.util.Objects;

public class Branch {

    private String name;
    private String lastCommitSha;

    public Branch(String name, String lastCommitSha) {
        this.name = name;
        this.lastCommitSha = lastCommitSha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }

    public void setLastCommitSha(String lastCommitSha) {
        this.lastCommitSha = lastCommitSha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Branch)) return false;
        Branch branch = (Branch) o;
        return getName().equals(branch.getName()) && getLastCommitSha().equals(branch.getLastCommitSha());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getLastCommitSha());
    }
}
