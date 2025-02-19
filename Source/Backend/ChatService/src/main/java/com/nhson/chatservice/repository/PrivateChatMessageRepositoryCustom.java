package com.nhson.chatservice.repository;

import com.nhson.chatservice.domain.PrivateChatMessage;

import java.util.Date;
import java.util.List;

public interface PrivateChatMessageRepositoryCustom {
    public List<String> findRecentChatParticipantsAndGroups(String username, Date lastTimeLoad, Date endDate);
    public List<PrivateChatMessage> findRecentMessages(String username,Date lastTimeLoad,Date endDate,String targetUsername);
}
