package com.nhson.chatservice.repository;

import com.nhson.chatservice.domain.PAGResponse;
import com.nhson.chatservice.domain.PrivateChatMessage;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.List;

@Repository
public class PrivateChatMessageRepositoryCustomImpl implements PrivateChatMessageRepositoryCustom{
    private final MongoTemplate mongoTemplate;

    public PrivateChatMessageRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> findRecentChatParticipantsAndGroups(String username, Date lastTimeLoad, Date endDate) {

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("sender").is(username),
                Criteria.where("recipient").is(username)
        ));

        Criteria.where("timestamp").gte(lastTimeLoad).lte(endDate);
        query.with(Sort.by(Sort.Order.desc("timestamp")));

        List<PrivateChatMessage> messages = mongoTemplate.find(query, PrivateChatMessage.class);
        Set<String> participants = new HashSet<>();
        Set<String> groups = new HashSet<>();

        for (PrivateChatMessage msg : messages) {
            if (!msg.getSender().equals(username)) {
                participants.add(msg.getSender());
            }
            if (!msg.getRecipient().equals(username)) {
                participants.add(msg.getRecipient());
            }
            if (msg.getConversation() != null) {
                groups.add(msg.getConversation());
            }
        }

        List<String> response = new ArrayList<>();
        response.addAll(participants);
        response.addAll(groups);

        return response;
    }

    @Override
    public List<PrivateChatMessage> findRecentMessages(String username, Date lastTimeLoad,Date endDate,String targetUsername) {

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("sender").is(username).and("recipient").is(targetUsername),
                Criteria.where("sender").is(targetUsername).and("recipient").is(username)
        ));

        Criteria.where("timestamp").gte(lastTimeLoad).lte(endDate);
        query.with(Sort.by(Sort.Order.asc("timestamp")));
        List<PrivateChatMessage> messages = mongoTemplate.find(query, PrivateChatMessage.class);

        return messages;
    }
}
