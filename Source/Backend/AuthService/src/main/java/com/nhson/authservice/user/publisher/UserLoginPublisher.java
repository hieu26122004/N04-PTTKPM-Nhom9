package com.nhson.authservice.user.publisher;

import com.nhson.authservice.user.ProfileReq;
import com.nhson.authservice.user.User;
import com.nhson.authservice.user.event.UserLoginEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserLoginPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    public UserLoginPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    public void publishEvent(User user) {
        user.setLastLogin(LocalDateTime.now());
        ProfileReq req = new ProfileReq(user);
        UserLoginEvent loginEvent = new UserLoginEvent(this,req);
        applicationEventPublisher.publishEvent(loginEvent);
    }
}
