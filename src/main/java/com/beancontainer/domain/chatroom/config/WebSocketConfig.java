package com.beancontainer.domain.chatroom.config;

import com.beancontainer.domain.chatroom.service.ChatRoomService;
import com.beancontainer.domain.chatroom.websocket.MyWebSocketHandler;
import com.beancontainer.domain.message.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration //Spring 설정 클래스
@EnableWebSocket // WebSocket 설정을 활성화
//Spring의 WebSocket 설정을 담당
//websocket 핸들러 등록 websocket 연결을 설정
public class WebSocketConfig implements WebSocketConfigurer { //인터페이스를 구현하여 WebSocket 설정을 정의

    private final MessageService messageService;
    private final ChatRoomService chatRoomService;

    public WebSocketConfig(MessageService messageService, ChatRoomService chatRoomService) {
        this.messageService = messageService;
        this.chatRoomService = chatRoomService;
    }

    //강사님 코드
    //registerWebSocketHandlers: WebSocket 핸들러를 등록
    // /ws 경로에 대한 WebSocket 핸들러로 MyWebSocketHandler를 등록하고, 모든 출처에서의 요청을 허용
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler(), "/ws").setAllowedOrigins("*");
    }

    @Bean
    public MyWebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler(messageService, chatRoomService);
    }
}