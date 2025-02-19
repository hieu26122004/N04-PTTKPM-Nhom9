package com.nhson.chatservice.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class PresenceEventListener {
    private ParticipantRepository participantRepository;
    private SimpMessagingTemplate messagingTemplate;
    public PresenceEventListener(ParticipantRepository participantRepository, SimpMessagingTemplate messagingTemplate) {
        this.participantRepository = participantRepository;
        this.messagingTemplate = messagingTemplate;
    }
    @EventListener
    private void handleLoginEvent(EventLogin loginEvent) {
        String roomId = loginEvent.getDestination();
        String loginDestination = roomId + ".login";
        messagingTemplate.convertAndSend(loginDestination,loginEvent);
        participantRepository.add(roomId, loginEvent);
    }
    @EventListener
    private void handleLogoutEvent(EventLogout logoutEvent) {
        String roomId = logoutEvent.getDestination();
        String logoutDestination = roomId + ".logout";
        messagingTemplate.convertAndSend(logoutDestination, logoutEvent);
        participantRepository.remove(roomId, logoutEvent);
    }
}
