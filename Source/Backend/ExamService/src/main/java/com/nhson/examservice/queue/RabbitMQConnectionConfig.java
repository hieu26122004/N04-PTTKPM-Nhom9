package com.nhson.examservice.queue;

import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConnectionConfig {
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
}
