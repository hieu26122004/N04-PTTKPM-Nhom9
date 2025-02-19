package com.nhson.authservice.email.listener;

import com.nhson.authservice.email.entities.Email;
import com.nhson.authservice.email.service.EmailService;
import com.nhson.authservice.email.event.MagicLinkGeneratedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MagicLinkGeneratedListener implements ApplicationListener<MagicLinkGeneratedEvent> {
    private final EmailService emailService;
    public MagicLinkGeneratedListener(EmailService emailService) {
        this.emailService = emailService;
    }
    @Override
    public void onApplicationEvent(MagicLinkGeneratedEvent event) {
        try {
            Email email = event.getEmail();
            email.setLastTryAt(new Date());
            emailService.saveEmail(email);
            log.info("Saved email for recipients: {}", email.getRecipients());
        } catch (Exception e) {
            log.error("Failed to save email from event: {}", e.getMessage(), e);
        }
    }

}
