package com.nhson.userservice.domain;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProfileReq {
    protected String userId;
    protected String userName;
    protected String email;
    protected String status;
    protected LocalDateTime createdAt;
    protected LocalDateTime lastLogin;
}
