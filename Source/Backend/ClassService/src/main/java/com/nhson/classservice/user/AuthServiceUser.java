package com.nhson.classservice.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthServiceUser {
    private Status status;
    enum Status {
        OK , ERROR
    }

}
