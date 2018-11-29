package com.example.katsumikusumi.instagramcloneapp.models;

public class User {

    private String email;
    private long phone_number;
    private String user_id;
    private String username;

    public User(String email, long phone_number, String user_id, String username) {
        this.email = email;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.username = username;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User(" +
                "user_id= " + user_id + '\'' +
                ", phone_number= " + phone_number + '\'' +
                ", email= " + email + '\'' +
                ", username= " + username + '\'' +
                ")";
    }

}
