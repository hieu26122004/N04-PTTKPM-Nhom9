package com.nhson.authservice.email.config;

import lombok.Getter;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.Properties;
import java.util.concurrent.Executors;

@Configuration
@Getter
public class EmailConfig {

    @Value("${practice-makes-perfect.email.sender}")
    private String sender;
    @Value("${practice-makes-perfect.email.host}")
    private String host;
    @Value("${practice-makes-perfect.email.port}")
    private int port;
    @Value("${practice-makes-perfect.email.smtp.username}")
    private String smtpUser;
    @Value("${practice-makes-perfect.email.smtp.password}")
    private String smtpPassword;
    @Value("${practice-makes-perfect.email.settings.ssl}")
    private boolean smtpSslEnable;
    @Value("${practice-makes-perfect.email.settings.trusted}")
    private String smtpSslTrust;
    @Value("${practice-makes-perfect.email.settings.startTls}")
    private boolean smtpStartTls;
    @Value("${practice-makes-perfect.email.settings.starttls.required}")
    private boolean smtpStartTlsRequired;
    @Value("${practice-makes-perfect.email.settings.debug}")
    private boolean debug;
    @Value("${practice-makes-perfect.email.poolSizeScheduler}")
    private int poolSizeScheduler;
    @Value("${practice-makes-perfect.email.maxRetryCount}")
    private int maxRetryCount;
    @Value("${practice-makes-perfect.email.retryDelayMs}")
    private long retryDelayMs;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(smtpUser);
        mailSender.setPassword(smtpPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", smtpStartTls);
        props.put("mail.smtp.starttls.required", smtpStartTlsRequired);
        props.put("mail.smtp.ssl.enable", smtpSslEnable);
        props.put("mail.smtp.ssl.trust", smtpSslTrust);
        props.put("mail.debug", debug);

        return mailSender;
    }
    @Bean
    public VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init(properties);
        return velocityEngine;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(0, Thread.ofVirtual().factory()));
    }
}
