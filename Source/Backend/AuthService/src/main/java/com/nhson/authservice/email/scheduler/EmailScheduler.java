package com.nhson.authservice.email.scheduler;

import com.nhson.authservice.email.service.EmailService;
import com.nhson.authservice.email.config.EmailConfig;
import com.nhson.authservice.email.entities.Email;
import com.nhson.authservice.email.repositories.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@EnableScheduling
@Configuration
@Component
@Slf4j
public class EmailScheduler {

    private final EmailService emailService;
    private final EmailConfig emailConfig;
    private final EmailRepository emailRepository;
    private final TaskScheduler taskScheduler;

    public EmailScheduler(EmailService emailService, EmailConfig emailConfig,
                          EmailRepository emailRepository, TaskScheduler taskScheduler) {

        this.emailService = emailService;
        this.emailConfig = emailConfig;
        this.emailRepository = emailRepository;
        this.taskScheduler = taskScheduler;

        this.taskScheduler.scheduleAtFixedRate(() -> {
            try {
                this.sendEmail();
            } catch (Exception e) {
                log.error("Error in scheduled task: {}", e.getMessage(), e);
            }
        }, Duration.ofMillis(this.emailConfig.getRetryDelayMs()));
    }
    @Transactional
    public void sendEmail() {
        Optional<Email> emailOptional = emailRepository.findFirstByOrderByLastTryAtAsc();
        if (emailOptional.isEmpty()) {
            return;
        }
        Email emailToSend = emailOptional.get();
        if (emailToSend.getRetryCount() >= emailConfig.getMaxRetryCount()) {
            log.error("Email failed after maximum retries: {}", emailToSend.getRecipients());
            emailRepository.delete(emailToSend);
            return;
        }
        try {
            emailService.sendEmail(emailToSend);
            emailRepository.delete(emailToSend);
        } catch (Exception e) {
            log.error("Error while sending email to {}: {}", emailToSend.getRecipients(), e.getMessage(), e);
            emailToSend.setLastTryAt(new Date());
            emailToSend.setRetryCount(emailToSend.getRetryCount() + 1);
            emailRepository.save(emailToSend);
        }
    }
}
