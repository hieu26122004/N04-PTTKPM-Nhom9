package com.nhson.chatservice.domain;

import java.util.Date;

public class PublicChatMessage{
    private String sender;
    private String content;
    private Date timestamp;

    public PublicChatMessage(String sender, String content, Date timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

