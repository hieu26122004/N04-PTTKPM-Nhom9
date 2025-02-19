package com.nhson.classservice.application.event;

import com.nhson.classservice.application.model.Class;
import org.springframework.context.ApplicationEvent;

public class MaterialCreateEvent extends ApplicationEvent {
    private final String materialId;
    private final Class clazz;
    public MaterialCreateEvent(Object source, String materialId, Class clazz) {
        super(source);
        this.materialId = materialId;
        this.clazz = clazz;
    }
    public String getMaterialId() {
        return materialId;
    }
    public Class getClazz(){return clazz;}
}
