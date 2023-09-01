package com.guet.photo_sharing.entity;


import java.time.LocalDateTime;

public class User {
    private String email;
    private String username;
    private String password;
    private boolean status;
    private String image;
    private String createtime;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", image='" + image + '\'' +
                ", createtime=" + createtime +
                '}';
    }

    public User() {
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public User(String email, String username, String password, boolean status, String image, String createtime) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.image = image;
        this.createtime = createtime;
    }
}