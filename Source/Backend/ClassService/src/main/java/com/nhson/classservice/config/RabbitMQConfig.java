package com.nhson.classservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@Slf4j
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "class-exchange";
    public static final String QUEUE_NAME = "class-queue";
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    @Bean
    @Qualifier("rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory cachingCf) {
        RabbitTemplate template = new RabbitTemplate(cachingCf);
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500); // Thời gian chờ ban đầu (ms)
        backOffPolicy.setMultiplier(2.0); // Hệ số nhân tăng thời gian chờ
        backOffPolicy.setMaxInterval(10000); // Thời gian chờ tối đa (ms)
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);

        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setReturnsCallback(returnedMessage -> {
            log.warn("Message with routing key {} was returned with replyCode {}: {}",
                    returnedMessage.getRoutingKey(),
                    returnedMessage.getReplyCode(),
                    returnedMessage.getReplyText());
            try {
                retryTemplate.execute(context -> {
                    String exchangeName = returnedMessage.getExchange();
                    template.convertAndSend(exchangeName, returnedMessage.getRoutingKey(),
                            returnedMessage.getMessage());
                    return null;
                }, recoveryContext -> {
                    log.error("Retry failed after max attempts. Discarding message: " +
                                    "Exchange: {}, RoutingKey: {}, Message: {}",
                            returnedMessage.getExchange(),
                            returnedMessage.getRoutingKey(),
                            returnedMessage.getMessage());
                    return null;
                });
            } catch (Exception e) {
                log.error("Unexpected error during retry: ", e);
            }
        });
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("Message confirmed with correlation id: {}", correlationData != null ? correlationData.getId() : "null");
            } else {
                log.error("Message not confirmed. Cause: {}", cause);
            }
        });
        template.setUsePublisherConnection(true);
        template.setMandatory(true);
        return template;
    }
}
