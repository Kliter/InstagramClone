package com.example.katsumikusumi.instagramcloneapp.models;

import com.google.firebase.database.FirebaseDatabase;

public class UserAccoutSettigs {

    private String description;
    private String display_name;
    private long followers;
    private long followings;
    private long posts;
    private String profile_photo;
    private String username;
    private String website;

    public UserAccoutSettigs(String description, String display_name, long followers, long followings, long posts, String profile_photo, String username, String website) {
        this.description = description;
        this.display_name = display_name;
        this.followers = followers;
        this.followings = followings;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.username = username;
        this.website = website;
    }

    public UserAccoutSettigs() {}

    public String getDescription() {
        return description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public long getFollowers() {
        return followers;
    }

    public long getFollowings() {
        return followings;
    }

    public long getPosts() {
        return posts;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public String getUsername() {
        return username;
    }

    public String getWebsite() {
        return website;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public void setFollowings(long followings) {
        this.followings = followings;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "UserAccoutSettigs{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", followers=" + followers +
                ", followings=" + followings +
                ", posts=" + posts +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
