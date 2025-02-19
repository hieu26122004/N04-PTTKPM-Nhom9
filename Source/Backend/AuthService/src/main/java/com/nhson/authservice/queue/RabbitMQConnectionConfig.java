package com.nhson.authservice.queue;

import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConnectionConfig {
    @Bean
    CachingConnectionFactory cachingCf() {
        CachingConnectionFactory cf = new CachingConnectionFactory("localhost", 5672);
        cf.setChannelCacheSize(10);
        cf.setChannelCheckoutTimeout(10000);
        cf.addConnectionListener(new ConnectionListener() {
            @Override
            public void onCreate(Connection connection) {
                log.info("RabbitMQ connection created");
            }
            @Override
            public void onShutDown(ShutdownSignalException signal) {
                // lắng nghe sự kiện khi channel bị đóng , ví dụ gửi message đến exchange không tồn tại
                log.info("RabbitMQ connection shutdown , " + signal.getReason());
                ConnectionListener.super.onShutDown(signal);
            }
        });
        cf.setPublisherReturns(true); // set mandatory is true
        cf.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return cf;
    }
}
