package com.nhson.chatservice.event;

import java.util.Date;

public class EventLogout{
    private final String username;
    private final Date time;
    private final String destination;

    public EventLogout(String username, Date time, String destination) {
        this.username = username;
        this.time = time;
        this.destination = destination;
    }
    public String getUsername() {
        return username;
    }
    public Date getTime() {
        return time;
    }
    public String getDestination() {
        return destination;
    }
}
