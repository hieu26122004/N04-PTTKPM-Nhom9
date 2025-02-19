package com.nhson.authservice.email.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Email {
    @Id
    private String id;
    private List<String> recipients;
    private String sender;
    private String subject;
    private String content;
    private boolean sent;
    private Date lastTryAt;
    private String templatePath;
    private Map<String, Object> context;
    private int retryCount;
}
