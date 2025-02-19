package com.nhson.userservice.queue;

import com.nhson.userservice.domain.ProfileReq;
import com.nhson.userservice.domain.User;
import com.nhson.userservice.repository.UserProfileRepository;
import com.rabbitmq.client.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Controller
public class RabbitController {
    private final Log log = LogFactory.getLog(LogFactory.class);
    private final UserProfileRepository userProfileRepository;
    public RabbitController(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "user-create", durable = "true" ),
                    exchange = @Exchange(value = RabbitMQConfig.AUTH_EXCHANGE_NAME, type = "topic"),
                    key = "user.create"
            ),
            messageConverter = "customMessageConverter"
    )
    public void createUserProfile(Message message, Channel channel, @Payload ProfileReq req){
        try {
            User user = new User(req);
            userProfileRepository.save(user);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                log.error("Error while creating user profile : " + e.getMessage() , e);
            } catch (IOException nackException) {
                log.error("Failed to negatively acknowledge message: " + nackException.getMessage(), nackException);
            }
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "user-login", durable = "true"),
                    exchange = @Exchange(value = RabbitMQConfig.AUTH_EXCHANGE_NAME, type = "topic"),
                    key = "user.login"
            ),
            messageConverter = "customMessageConverter"
    )
    @Transactional
    public void onLogin(Message message, Channel channel, @Payload ProfileReq req) {
        try {
            User user = new User(req);
            userProfileRepository.updateLastLogin(user.getUserId(),user.getLastLogin());
            log.info("Saved user profile " + user.toString());
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException ackException) {
                log.error("Failed to acknowledge message: " + ackException.getMessage(),ackException);
            }
        } catch (Exception e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException nackException) {
                log.error("Failed to negatively acknowledge message: " + nackException.getMessage(), nackException);
            }
        }
    }


}
