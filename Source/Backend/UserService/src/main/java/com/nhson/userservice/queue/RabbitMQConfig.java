package com.nhson.userservice.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String AUTH_EXCHANGE_NAME = "auth-exchange";

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
        factory.setMessageConverter(this.customMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(1);
        return factory;
    }


    @Bean
    CachingConnectionFactory cachingCf() {
        CachingConnectionFactory cf = new CachingConnectionFactory("localhost", 5672);
        cf.setChannelCacheSize(10);
        cf.setChannelCheckoutTimeout(10000);
        cf.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                // do some works
            }

            @Override
            public void onShutDown(ShutdownSignalException signal) {
                System.out.println(signal.getReason());
                ConnectionListener.super.onShutDown(signal);
            }
        });
        cf.setPublisherReturns(true);
        cf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return cf;
    }

    @Bean
    public MessageConverter customMessageConverter(){
        return new CustomMessageConvert(objectMapper());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
