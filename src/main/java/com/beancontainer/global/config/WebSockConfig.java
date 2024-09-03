package com.beancontainer.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker

//WebSocket 활설화, 메세지 브로커와 STOMP 엔드포인트 설정
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    //메시지 브로커를 설정
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");  //메세지 구독
        config.setApplicationDestinationPrefixes("/pub");   //메세지 발행
    }

    @Override
    //STOMP 엔드포인트 설정
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")  //ws-stomp 엔드포인트, 모든 출처를 허용
                .withSockJS();
    }
}