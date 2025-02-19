package com.nhson.chatservice.web;

import com.nhson.chatservice.domain.ChatService;
import com.nhson.chatservice.domain.PAGResponse;
import com.nhson.chatservice.domain.PrivateChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final ChatService chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PrivateChatMessage>> getRecentMessages(
            @RequestParam("last-time-load") String lastTimeLoadString,
            @RequestParam("target") String target,
            JwtAuthenticationToken authenticationToken) {
        Date lastTimeLoad;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            lastTimeLoad = dateFormat.parse(lastTimeLoadString);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
        List<PrivateChatMessage> messages = chatService.findRecentMessages(authenticationToken, lastTimeLoad,target);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/pag")
    public ResponseEntity<PAGResponse> getRecentParticipantsAndGroup(
            JwtAuthenticationToken authenticationToken,
            @RequestParam("last-time-load") String lastTimeLoadString){
        Date lastTimeLoad;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            lastTimeLoad = dateFormat.parse(lastTimeLoadString);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
        PAGResponse response = chatService.findRecentChatParticipantsAndGroups(authenticationToken,lastTimeLoad);
        return ResponseEntity.ok(response);
    }

}
