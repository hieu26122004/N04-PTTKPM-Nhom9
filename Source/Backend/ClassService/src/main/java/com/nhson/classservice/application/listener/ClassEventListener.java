package com.nhson.classservice.application.listener;

import com.nhson.classservice.application.event.ClassDeleteEvent;
import com.nhson.classservice.application.event.MaterialCreateEvent;
import com.nhson.classservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClassEventListener {
    private final RabbitTemplate rabbitTemplate;

    public ClassEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @EventListener
    public void handleClassDeleteEvent(ClassDeleteEvent event) throws Exception {
        CorrelationData correlationData = new CorrelationData(event.getClazzId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "class.delete", event.getClazzId(), correlationData);
    }
    @EventListener
    public void handleMaterialCreateEvent(MaterialCreateEvent event) throws Exception {
        CorrelationData correlationData = new CorrelationData(event.getMaterialId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME + event.getClazz().getClassId(), "material.create", event.getMaterialId(), correlationData);
    }
}
