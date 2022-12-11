package com.github.proxy.data;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Repository)) return false;
        Repository that = (Repository) o;
        return getName().equals(that.getName()) && getBranches().equals(that.getBranches());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBranches());
    }
}
