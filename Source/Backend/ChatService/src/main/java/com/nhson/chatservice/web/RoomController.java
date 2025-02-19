package com.nhson.chatservice.web;

import com.nhson.chatservice.event.ParticipantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {
    private ParticipantRepository participantRepository;
    public RoomController(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }
    @GetMapping("/rooms/{subject}")
    public ResponseEntity<List<String>> getRooms(@PathVariable String subject){
        List<String> allRoom = participantRepository.getRooms();
        List<String> roomRes = allRoom.stream().filter(s -> s.startsWith("/topic/" + subject)).toList();
        return ResponseEntity.ok(roomRes);
    }
    @PostMapping("/rooms/{subject}")
    public ResponseEntity<String> createRoom(@PathVariable String subject){
        int size = participantRepository.getRooms().stream().filter(s -> s.startsWith("/" + subject)).toList().size();
        String roomName = "/topic/" + subject + "-" + size;
        participantRepository.createNewRoom(roomName);
        return ResponseEntity.ok(roomName);
    }
}
