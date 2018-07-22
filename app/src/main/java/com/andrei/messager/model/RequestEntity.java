package com.andrei.messager.model;

public class RequestEntity {

    private String id;
    private User user;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RequestEntity{" +
                "id='" + id + '\'' +
                ", user=" + user +
                '}';
    }
}
