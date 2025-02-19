package com.nhson.authservice.user.event;

import com.nhson.authservice.user.ProfileReq;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserLoginEvent extends ApplicationEvent {
    private final ProfileReq profileReq;
    public UserLoginEvent(Object source, ProfileReq profileReq) {
        super(source);
        this.profileReq = profileReq;
    }
}
