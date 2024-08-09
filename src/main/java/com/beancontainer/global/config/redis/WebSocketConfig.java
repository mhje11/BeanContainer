package com.beancontainer.global.config.redis;

import com.beancontainer.domain.chatroom.websocket.CustomHandshakeInterceptor;
import com.beancontainer.domain.chatroom.websocket.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    //security와 함께쓰기위해서 추가

    private final CustomHandshakeInterceptor customHandshakeInterceptor;
    private final MyWebSocketHandler  myWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry  registry) {
        registry.addHandler(myWebSocketHandler, "/ws").setAllowedOrigins("*")
                //security와 함께쓰기위해서 추가
                .addInterceptors(customHandshakeInterceptor);
    }
}
