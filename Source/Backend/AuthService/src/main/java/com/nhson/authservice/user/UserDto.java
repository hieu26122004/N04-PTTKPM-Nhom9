package com.nhson.authservice.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Status status;

    enum Status {
        OK , ERROR
    }
}