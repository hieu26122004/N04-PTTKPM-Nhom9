package com.nhson.chatservice.config;

import com.mongodb.client.MongoClient;
import com.nhson.chatservice.domain.ChatService;
import com.nhson.chatservice.event.ParticipantRepository;
import com.nhson.chatservice.event.PresenceEventListener;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class ChatConfig {
    @Bean
    public PresenceEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate){
        return new PresenceEventListener(participantRepository(), messagingTemplate);
    }
    @Bean
    public ParticipantRepository participantRepository() {
        return new ParticipantRepository();
    }
}
