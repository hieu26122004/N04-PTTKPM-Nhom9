package com.nhson.chatservice.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.simp.user.UserDestinationResult;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDestinationResolver implements UserDestinationResolver {
    private String prefix = "/user/";
    private SimpUserRegistry simpUserRegistry;
    private final Log log = LogFactory.getLog(CustomUserDestinationResolver.class);

    public CustomUserDestinationResolver(SimpUserRegistry userRegistry) {
        this.simpUserRegistry = userRegistry;
    }

    @Override
    public UserDestinationResult resolveDestination(Message<?> message) {
        ParseResult parseResult = this.parse(message);
        if(parseResult == null) {
            return null;
        }else {
            String user = parseResult.getUser();
            String sourceDestination = parseResult.getSourceDestination();
            String actualDestination = parseResult.getActualDestination();
            Set<String> targetSet = new HashSet();
            String targetDestination = this.getTargetDestination(sourceDestination,actualDestination,user);
            log.debug(String.format("Resolve %s to %s", sourceDestination, targetDestination));
            if(targetSet!=null) {
                targetSet.add(targetDestination);
            }
            String subscribeDestination = parseResult.getSubscribeDestination();
            return new UserDestinationResult(sourceDestination,targetSet,subscribeDestination,user,parseResult.getSessionIds());
        }
    }

    private ParseResult parse(Message<?> message){
        MessageHeaders headers = message.getHeaders();
        String sourceDestination = SimpMessageHeaderAccessor.getDestination(headers);
        if(sourceDestination != null && this.checkDestination(sourceDestination,this.prefix)){
            SimpMessageType messageType = SimpMessageHeaderAccessor.getMessageType(headers);
            if(messageType != null){
                switch (messageType){
                    case SUBSCRIBE:
                    case UNSUBSCRIBE:
                        return this.parseSubscriptionMessage(message, sourceDestination);
                    case MESSAGE:
                        return this.parseMessage(headers, sourceDestination);
                }
            }
        }
        return null;
    }

    protected boolean checkDestination(String destination, String requiredPrefix) {
        return destination.startsWith(requiredPrefix);
    }

    protected String getTargetDestination(String sourceDestination, String actualDestination, @Nullable String user) {
        return actualDestination + "-user" + user;
    }

    @Nullable
    private ParseResult parseSubscriptionMessage(Message<?> message, String sourceDestination) {
        MessageHeaders headers = message.getHeaders();
        int prefixEnd = this.prefix.length() - 1;
        String actualDestination = sourceDestination.substring(prefixEnd); // /queue/messages

        Principal principal = SimpMessageHeaderAccessor.getUser(headers);
        String user = principal != null ? principal.getName() : null;
        Assert.isTrue(user == null || !user.contains("%2F"), () -> {
            return "Invalid sequence \"%2F\" in user name: " + user;
        });
        return new ParseResult(sourceDestination, actualDestination, sourceDestination, Set.of(principal.getName()) ,user);
    }
    private ParseResult parseMessage(MessageHeaders headers, String sourceDest) {
        // sourDest /user/d94cc897-5bf0-440d-97e9-ff823676eb44/queue/messages
        int prefixEnd = this.prefix.length(); // 6 "/user/"
        int userEnd = sourceDest.indexOf(47, prefixEnd);
        Assert.isTrue(userEnd > 0, "Expected destination pattern \"/user/{userId}/**\"");
        String actualDest = sourceDest.substring(userEnd); // /queue/messages
        String var10000 = this.prefix.substring(0, prefixEnd - 1);
        String subscribeDest = var10000 + actualDest;
        String userName = sourceDest.substring(prefixEnd, userEnd);
        userName = StringUtils.replace(userName, "%2F", "/");
        return new ParseResult(sourceDest, actualDest, subscribeDest, Set.of(userName),userName);
    }
    class ParseResult {
        private final String sourceDestination;
        private final String actualDestination;
        private final String subscribeDestination;
        private final Set<String> sessionIds;
        @Nullable
        private final String user;

        ParseResult(String sourceDestination, String actualDestination, String subscribeDestination, Set<String> sessionIds, @Nullable String user) {
            this.sourceDestination = sourceDestination;
            this.actualDestination = actualDestination;
            this.subscribeDestination = subscribeDestination;
            this.sessionIds = sessionIds;
            this.user = user;
        }

        public Set<String> getSessionIds() {
            return sessionIds;
        }

        public String getSourceDestination() {
            return sourceDestination;
        }

        public String getActualDestination() {
            return actualDestination;
        }

        public String getSubscribeDestination() {
            return subscribeDestination;
        }

        @Nullable
        public String getUser() {
            return user;
        }
    }
}
