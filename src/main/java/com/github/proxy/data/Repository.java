package com.github.proxy.data;

import java.util.List;

public class Repository {

    private String name;
    private List<Branch> branches;

    public Repository(String name, List<Branch> branches) {
        this.name = name;
        this.branches = branches;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
}
