package com.nhson.chatservice.web;

import com.nhson.chatservice.domain.PrivateChatMessage;
import com.nhson.chatservice.repository.PrivateChatMessageRepository;
import com.nhson.chatservice.domain.PublicChatMessage;
import com.nhson.chatservice.user.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final PrivateChatMessageRepository chatRepository;
    private final UserServiceClient userServiceClient;

    public ChatController(SimpMessagingTemplate messagingTemplate, PrivateChatMessageRepository chatRepository, UserServiceClient userServiceClient) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
        this.userServiceClient = userServiceClient;
    }


    // user to user
    @MessageMapping("/chat.private")
    public void privateMessage(@Payload PrivateChatMessage message, JwtAuthenticationToken authenticationToken) {
        String recipientUsername = message.getRecipient();
        String recipientId = userServiceClient.getUserIdByUsername(recipientUsername,authenticationToken);

        message.setTimestamp(new Date());
        String sender = (String) authenticationToken.getToken().getClaims().get("username");
        message.setSender(sender);
        message.setRecipient(recipientUsername);

        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/messages",
                message
        );
        chatRepository.save(message);
    }

    // user to room
    @MessageMapping("/chat/{roomName}")
    public void sendMessageToRoom(@Payload PublicChatMessage message, @DestinationVariable("roomName") String roomName, JwtAuthenticationToken authenticationToken) {
        message.setTimestamp(new java.util.Date());
        message.setSender((String) authenticationToken.getToken().getClaims().get("username"));
        logger.debug("Đang gửi tin nhắn đến nhóm {}: {}", roomName, message.getContent());
        messagingTemplate.convertAndSend("/topic/" + roomName, message);
    }

    // user to class room
    @MessageMapping("/chat/class/{classId}")
    public void sendMessageToClassRoom(@Payload PrivateChatMessage message, @DestinationVariable("classId") String classId, JwtAuthenticationToken authenticationToken) {
        logger.debug("Đang gửi tin nhắn đến lớp {}: {}", classId, message.getContent());
        messagingTemplate.convertAndSend("/topic/class/" + classId, message);
    }
}
