package com.nhson.authservice.email.publisher;

import com.nhson.authservice.email.entities.Email;
import com.nhson.authservice.email.event.MagicLinkGeneratedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MagicLinkGeneratedApplicationPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    public MagicLinkGeneratedApplicationPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    public void publish(String magicLink, List<String> recipients,String username){
        Map<String, Object> context = new HashMap<>();
        context.put("username", username);
        context.put("magicLink", magicLink);
        Email email = Email.builder()
                .recipients(recipients)
                .subject("RESET PASSWORD")
                .templatePath("templates/reset-email-template.vm")
                .context(context)
                .retryCount(0)
                .build();
        MagicLinkGeneratedEvent magicLinkGeneratedEvent = new MagicLinkGeneratedEvent(this,email);
        applicationEventPublisher.publishEvent(magicLinkGeneratedEvent);
    }
}
