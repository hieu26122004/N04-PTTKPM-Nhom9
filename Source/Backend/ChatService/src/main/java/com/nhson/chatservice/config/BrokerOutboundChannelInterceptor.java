package com.nhson.chatservice.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class BrokerOutboundChannelInterceptor implements ChannelInterceptor {
    private static final Log log = LogFactory.getLog(BrokerOutboundChannelInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        String destination = accessor.getDestination();

        log.info("BrokerOutboundInterceptor - Destination: " + destination);
        if ("/topic/blockedTopic".equals(destination)) {
            log.warn("Tin nhắn đến " + destination + " đã bị chặn.");
            return null;
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        log.info("BrokerOutboundInterceptor - postSend");
    }
}
