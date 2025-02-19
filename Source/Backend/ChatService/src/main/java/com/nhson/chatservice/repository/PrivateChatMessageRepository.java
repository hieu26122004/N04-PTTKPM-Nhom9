package com.nhson.chatservice.repository;

import com.nhson.chatservice.domain.PrivateChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivateChatMessageRepository extends MongoRepository<PrivateChatMessage,String>, PrivateChatMessageRepositoryCustom{
}
