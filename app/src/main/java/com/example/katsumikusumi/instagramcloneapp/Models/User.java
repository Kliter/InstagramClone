package com.example.katsumikusumi.instagramcloneapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

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

    protected User(Parcel in) {
        email = in.readString();
        phone_number = in.readLong();
        user_id = in.readString();
        username = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeLong(phone_number);
        dest.writeString(user_id);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
