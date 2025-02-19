package com.nhson.authservice.user.listener;

import com.nhson.authservice.user.event.UserLoginEvent;
import com.nhson.authservice.user.event.UserRegisterEvent;
import com.nhson.authservice.queue.RabbitMQConfig;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;;

@Component
public class UserEventListener {

    private final RabbitTemplate rabbitTemplate;

    public UserEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @EventListener
    public void handleRegisterEvent(UserRegisterEvent userRegisterEvent){
        CorrelationData correlationData = new CorrelationData(userRegisterEvent.getCreateProfileReq().getUserId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "user.create", userRegisterEvent.getCreateProfileReq(),correlationData);
    }

    @EventListener
    public void handleLoginEvent(UserLoginEvent userLoginEvent){
        CorrelationData correlationData = new CorrelationData(userLoginEvent.getProfileReq().getUserId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"user.login",userLoginEvent.getProfileReq(),correlationData);
    }
}
