package com.nhson.chatservice.event;

import org.springframework.context.ApplicationEvent;

import java.util.Date;

public class EventLogin extends ApplicationEvent {
    private final Date time;
    private final String username;
    private final String destination;
    public EventLogin(Object source, Date time, String username, String destination) {
        super(source);
        this.time = time;
        this.username = username;
        this.destination = destination;
    }
    public Date getTime() {
        return time;
    }
    public String getUsername() {
        return username;
    }
    public String getDestination() {
        return destination;
    }
}

