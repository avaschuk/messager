package com.andrei.messager.model;

import java.io.Serializable;

public class User implements Serializable {

    private String id;

    private String username;

    private String email;

    private Boolean hasRequest;

    public User() {
        this.hasRequest = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getHasRequest() {
        return hasRequest;
    }

    public void setHasRequest(Boolean hasRequest) {
        this.hasRequest = hasRequest;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", hasRequest=" + hasRequest +
                '}';
    }
}
