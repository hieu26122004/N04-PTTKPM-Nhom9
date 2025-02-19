package com.nhson.chatservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final InboundChannelInterceptor inboundChannelInterceptor;
    private final BrokerOutboundChannelInterceptor brokerOutboundChannelInterceptor;
    @Autowired
    public WebSocketConfig(InboundChannelInterceptor inboundChannelInterceptor,
                           BrokerOutboundChannelInterceptor brokerOutboundChannelInterceptor) {
        this.inboundChannelInterceptor = inboundChannelInterceptor;
        this.brokerOutboundChannelInterceptor = brokerOutboundChannelInterceptor;
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/queue/", "/topic/", "/exchange/")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest")
                .setSystemLogin("guest")
                .setSystemPasscode("guest");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundChannelInterceptor);
    }
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(brokerOutboundChannelInterceptor);
    }
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(Integer.MAX_VALUE);
        registry.setSendBufferSizeLimit(Integer.MAX_VALUE);
        registry.setSendTimeLimit(2048 * 2048);
    }
    @Bean
    @Order(HIGHEST_PRECEDENCE + 500)
    @Primary
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean factoryBean = new ServletServerContainerFactoryBean();
        factoryBean.setMaxTextMessageBufferSize(Integer.MAX_VALUE);
        factoryBean.setMaxBinaryMessageBufferSize(Integer.MAX_VALUE);
        factoryBean.setMaxSessionIdleTimeout(2048L * 2048L);
        factoryBean.setAsyncSendTimeout(2048L * 2048L);
        return factoryBean;
    }
}
