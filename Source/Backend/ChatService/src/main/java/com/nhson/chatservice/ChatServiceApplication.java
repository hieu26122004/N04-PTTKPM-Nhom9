package com.nhson.chatservice;

import com.nhson.chatservice.config.CustomUserDestinationResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.nhson.chatservice.repository")
public class ChatServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
    @Bean
    @Order(9999)
    public UserDestinationResolver userDestinationResolver(SimpUserRegistry userRegistry) {
        return new CustomUserDestinationResolver(userRegistry);
    }
}
