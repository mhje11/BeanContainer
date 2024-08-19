package com.beancontainer.domain.chatroom;

import com.beancontainer.domain.chatroom.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSockChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
//        String payload = message.getPayload();
//        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
//
//        // 메시지를 해당 채팅방의 모든 사용자에게 전송
//        chatService.addMessage(chatMessageDto.getChatRoomId(), chatMessageDto.getContent());
//        broadcastMessage(chatMessageDto);
//    }
//
//    private void broadcastMessage(ChatMessageDto message) throws IOException {
//        for (WebSocketSession session : sessionMap.values()) {
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//        }
//    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessionMap.remove(session.getId());
    }
}
