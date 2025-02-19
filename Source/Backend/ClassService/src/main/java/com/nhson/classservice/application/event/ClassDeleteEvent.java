package com.nhson.classservice.application.event;

import org.springframework.context.ApplicationEvent;

public class ClassDeleteEvent extends ApplicationEvent {
    private final String clazzId;

    public ClassDeleteEvent(Object source, String clazzId) {
        super(source);
        this.clazzId = clazzId;
    }
    public String getClazzId() {
        return clazzId;
    }
}
