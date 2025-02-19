package com.nhson.authservice.user;

import java.time.LocalDateTime;

public class ProfileReq {
    protected String userId;
    protected String userName;
    protected String email;
    protected String status;
    protected LocalDateTime createdAt;
    protected LocalDateTime lastLogin;

    public ProfileReq(User user){
        this.userId = user.userId;
        this.userName = user.username;
        this.email = user.email;
        this.status = user.status;
        this.createdAt = user.createdAt;
        this.lastLogin = user.lastLogin;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
}
