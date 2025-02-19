package com.nhson.authservice.email.event;

import com.nhson.authservice.email.entities.Email;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MagicLinkGeneratedEvent extends ApplicationEvent {
    private final Email email;
    public MagicLinkGeneratedEvent(Object source, Email email) {
        super(source);
        this.email = email;
    }
}
