package com.nhson.authservice.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserToken {
    private String id;
    private String username;
    private String email;
    private String token;
    private Long tokenExpiry;

    public UserToken(String id, String username, String email, String token, Long tokenExpiry) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.token = token;
        this.tokenExpiry = tokenExpiry;
    }
}
