package com.nhson.authservice.user.event;

import com.nhson.authservice.user.ProfileReq;
import org.springframework.context.ApplicationEvent;

public class UserRegisterEvent extends ApplicationEvent {
    private final ProfileReq profileReq;
    public UserRegisterEvent(Object source, ProfileReq profileReq) {
        super(source);
        this.profileReq = profileReq;
    }
    public ProfileReq getCreateProfileReq(){
        return this.profileReq;
    }
}
