package com.nhson.examservice.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Slf4j
public class RabbitMQConfig {

    public static final String CLASS_EXCHANGE_NAME = "class-exchange";
    public static final String USER_QUEUE_PREFIX = "messages-user";
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cachingCf) {
        RabbitTemplate template = new RabbitTemplate(cachingCf);
        RetryTemplate retryTemplate = new RetryTemplate();
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        template.setReturnsCallback(returnedMessage -> {
            log.info("Message with routing key " + returnedMessage.getRoutingKey() + " has returned with replyCode " + returnedMessage.getReplyCode() + " : " + returnedMessage.getReplyText());
        }); // for returned message
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack){
                    log.info("Message confirmed with correlation id " + correlationData.getId());
                }else{
                    log.info("Message not confirmed. Cause: " + cause);
                }
            }
        }); // for publisher confirm type ConfirmType.CORRELATED, an example is when sending a message to a non-existent exchange. In that case, the broker closes the channel. The reason for the closure is included in the cause
//        template.setRecoveryCallback();
        template.setUsePublisherConnection(true);
        template.setMandatory(true);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(CachingConnectionFactory cf){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setAfterReceivePostProcessors(new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // Xử lý tin nhắn, ví dụ: logging
                return message;
            }
        });
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
//        factory.setMessageConverter(customMessageConverter());
        return factory;
    }
}
