package com.nhson.chatservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhson.chatservice.event.EventLogin;
import com.nhson.chatservice.event.EventLogout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class InboundChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(InboundChannelInterceptor.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    public InboundChannelInterceptor(JwtDecoder jwtDecoder, ApplicationEventPublisher applicationEventPublisher) {
        this.jwtDecoder = jwtDecoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();

        logger.debug("Đang chặn tin nhắn: {}", message);

        try {
            if (command == null) {
                return message;
            }

            switch (command) {
                case CONNECT:
                    return handleConnect(headerAccessor, message);
                case SUBSCRIBE:
                    return handleSubscribe(headerAccessor, message);
                case SEND:
                    return handleSend(headerAccessor, message);
                case DISCONNECT:
                    return handleDisconnect(headerAccessor, message);
                default:
                    // Bỏ qua các frame khác như HEARTBEAT
                    return message;
            }
        } catch (SecurityException ex) {
            logger.error("Security exception: {}", ex.getMessage());
            return null; // Kết nối sẽ bị đóng
        } catch (Exception ex) {
            logger.error("Unhandled exception: ", ex);
            return null;
        }
    }

    private Message<?> handleConnect(StompHeaderAccessor headerAccessor, Message<?> message) {
        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("Missing Authorization header for CONNECT command");
            throw new SecurityException("Unauthorized: Missing Authorization header");
        }
        String jwtToken = authHeader; // Không loại bỏ "Bearer " prefix
        logger.debug("JWT Token received (CONNECT): {}", jwtToken);
        Optional<Jwt> jwt = Optional.ofNullable(jwtDecoder.decode(jwtToken));
        if (jwt.isPresent()) {
            logger.debug("JWT decoded successfully for user: {}", jwt.get().getSubject());
        }
        if (jwt.isPresent() && hasRequiredScope(jwt.get())) {
            headerAccessor.setUser(new JwtAuthenticationToken(jwt.get()));
            headerAccessor.setLeaveMutable(false);
            return MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders());
        }
        logger.warn("Invalid JWT token for CONNECT command");
        throw new SecurityException("Unauthorized: Invalid JWT token");
    }

    private Message<?> handleSubscribe(StompHeaderAccessor headerAccessor, Message<?> message) {
        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("Missing Authorization header for SUBSCRIBE command");
            throw new SecurityException("Unauthorized: Missing Authorization header");
        }
        String jwtToken = authHeader; // Không loại bỏ "Bearer " prefix
        logger.debug("JWT Token received (SUBSCRIBE): {}", jwtToken);
        Optional<Jwt> jwt = Optional.ofNullable(jwtDecoder.decode(jwtToken));
        if (jwt.isEmpty() || !hasRequiredScope(jwt.get())) {
            logger.warn("Invalid JWT token or insufficient scope for SUBSCRIBE command");
            throw new SecurityException("Unauthorized: Invalid JWT token or insufficient scope");
        }
        String destination = headerAccessor.getDestination();
        String userId = jwt.get().getSubject();
        String expectedDestination = "/user/" + userId + "/queue/messages";
        if (!destination.equals(expectedDestination) && !hasRequiredScope(jwt.get())) {
            logger.warn("User {} attempting to subscribe to unauthorized destination {}", userId, destination);
            throw new SecurityException("Unauthorized: You cannot subscribe to this topic");
        }

        if (destination.startsWith("/topic") &&
                !destination.endsWith("/login") &&
                !destination.endsWith("/logout") &&
                !destination.endsWith("/participants")) {
            applicationEventPublisher.publishEvent(new EventLogin(this, new Date(), userId, destination));
        }

        headerAccessor.setUser(new JwtAuthenticationToken(jwt.get()));
        headerAccessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders());
    }

    private Message<?> handleSend(StompHeaderAccessor headerAccessor, Message<?> message) {
        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("Missing Authorization header for SEND command");
            throw new SecurityException("Unauthorized: Missing Authorization header");
        }
        String jwtToken = authHeader; // Không loại bỏ "Bearer " prefix
        logger.debug("JWT Token received (SEND): {}", jwtToken);
        Optional<Jwt> jwt = Optional.ofNullable(jwtDecoder.decode(jwtToken));
        if (jwt.isPresent()) {
            logger.debug("JWT decoded successfully for user: {}", jwt.get().getSubject());
        }
        if (jwt.isPresent() && hasRequiredScope(jwt.get())) {
            headerAccessor.setUser(new JwtAuthenticationToken(jwt.get()));
            headerAccessor.setLeaveMutable(true);
            return MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders());
        } else {
            logger.warn("User is not authenticated for SEND command");
            throw new SecurityException("Unauthorized: User is not authenticated");
        }
    }

    private Message<?> handleDisconnect(StompHeaderAccessor headerAccessor, Message<?> message) {
        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            logger.warn("Missing Authorization header for DISCONNECT command");
            return message;
        }

        String jwtToken = authHeader; // Không loại bỏ "Bearer " prefix
        logger.debug("JWT Token received (DISCONNECT): {}", jwtToken);
        Optional<Jwt> jwt = Optional.ofNullable(jwtDecoder.decode(jwtToken));
        if (jwt.isPresent() && hasRequiredScope(jwt.get())) {
            String destination = headerAccessor.getDestination();
            String userId = jwt.get().getSubject();
            if (destination != null && destination.startsWith("/topic") &&
                    !destination.endsWith("/login") &&
                    !destination.endsWith("/logout") &&
                    !destination.endsWith("/participants")) {
                applicationEventPublisher.publishEvent(new EventLogout(userId, new Date(), destination));
            }
            headerAccessor.setUser(new JwtAuthenticationToken(jwt.get()));
            headerAccessor.setLeaveMutable(true);
            return MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders());
        }
        return message;
    }

    private boolean hasRequiredScope(Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        return scopes != null && (scopes.contains("USER") || scopes.contains("ADMIN"));
    }

    private boolean isParticipantOfClass(String classId){
        // TODO
    }
}
