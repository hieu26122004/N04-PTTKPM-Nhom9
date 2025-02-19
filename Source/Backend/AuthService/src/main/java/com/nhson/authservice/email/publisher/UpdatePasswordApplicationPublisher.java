package com.nhson.authservice.email.publisher;

import com.nhson.authservice.email.entities.Email;
import com.nhson.authservice.email.event.UpdatePasswordEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UpdatePasswordApplicationPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;
    public UpdatePasswordApplicationPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    public void publish(String newPassword, List<String> recipients, String username){

        Map<String, Object> context = new HashMap<>();
        context.put("username", username);
        context.put("newPassword", newPassword);

        Email email = Email.builder()
                .recipients(recipients)
                .subject("RESET PASSWORD")
                .templatePath("templates/update-password-template.vm")
                .context(context)
                .retryCount(0)
                .build();
        UpdatePasswordEvent updatePasswordEvent = new UpdatePasswordEvent(this,email);
        applicationEventPublisher.publishEvent(updatePasswordEvent);
    }
}
