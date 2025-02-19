package com.nhson.chatservice.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class ParticipantRepository {
    private Map<String, List<EventLogin>> participants = new ConcurrentHashMap<>();
    public void add(String roomId, EventLogin eventLogin) {
        participants.computeIfAbsent(roomId, k -> new ArrayList<>());
        participants.get(roomId).add(eventLogin);
    }
    public List<EventLogin> getParticipants(String roomId) {
        return participants.getOrDefault(roomId, new ArrayList<>());
    }
    public void remove(String roomId, EventLogout eventLogout) {
        List<EventLogin> roomParticipants = participants.get(roomId);
        if (roomParticipants != null) {
            roomParticipants.removeIf(event -> event.getUsername().equals(eventLogout.getUsername()));
        }
    }
    public List<String> getRooms(){
        return new ArrayList<>(participants.keySet());
    }

    public void createNewRoom(String roomId){
        participants.computeIfAbsent(roomId, k -> new ArrayList<>());
    }
}

