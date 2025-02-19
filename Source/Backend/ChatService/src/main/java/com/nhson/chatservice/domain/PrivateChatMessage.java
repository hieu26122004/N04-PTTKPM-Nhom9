package com.nhson.chatservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document(collection = "private_chat_messages")
public class PrivateChatMessage {
    @Id
    private String id;
    @Field("sender")
    private String sender;
    @Field("recipient")
    private String recipient;
    @Field("content")
    private String content;
    @Field("timestamp")
    private Date timestamp;
    @Field("attachment")
    private Object attachment;
    @Field("conversation")
    private String conversation;

    public PrivateChatMessage() {
    }

    public PrivateChatMessage(String sender, String recipient, String content, Date timestamp, Object attachment, String conversation) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
        this.attachment = attachment;
        this.conversation = conversation;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }
}