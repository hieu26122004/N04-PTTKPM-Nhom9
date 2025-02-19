package com.nhson.classservice.application.event;

import com.nhson.classservice.application.model.Class;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AnnouncementCreateEvent extends ApplicationEvent {
    private final String announcementId;
    private final Class clazz;
    public AnnouncementCreateEvent(Object source, String announcementId, Class clazz) {
        super(source);
        this.announcementId = announcementId;
        this.clazz = clazz;
    }
}
