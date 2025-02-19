package com.nhson.authservice.email.service;

import com.nhson.authservice.email.config.EmailConfig;
import com.nhson.authservice.email.entities.Email;
import com.nhson.authservice.email.repositories.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailConfig emailConfig;
    private final VelocityEngine velocityEngine;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EmailRepository emailRepository;

    private JavaMailSender getJavaMailSender(){
        return emailConfig.javaMailSender();
    }
    public void saveEmail(Email email){
        emailRepository.save(email);
    }
    public void sendEmail(@NotNull Email email) {
        if (email.getRecipients().isEmpty()) {
            throw new IllegalArgumentException("Recipients list cannot be empty.");
        }
        JavaMailSender sender = getJavaMailSender();
        VelocityContext velocityContext = new VelocityContext(email.getContext());
        StringWriter writer = new StringWriter();
        velocityEngine.mergeTemplate(email.getTemplatePath(), "UTF-8", velocityContext, writer);
        email.setContent(writer.toString());

        for (String recipient : email.getRecipients()) {
            MimeMessage message = sender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(emailConfig.getSender());
                helper.setTo(recipient);
                helper.setSubject(email.getSubject());
                helper.setText(writer.toString(), true);
                sender.send(message);
                log.info("Email sent successfully to {}", recipient);
            } catch (MessagingException e) {
                log.error("Failed to send email to {}: {}", recipient, e.getMessage(), e);
                throw new RuntimeException("Failed to send email to " + recipient, e);
            }
        }
    }
}
