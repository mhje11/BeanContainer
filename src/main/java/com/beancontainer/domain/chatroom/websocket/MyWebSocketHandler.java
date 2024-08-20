package com.beancontainer.domain.chatroom.websocket;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MyWebSocketHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Connection established with session: " + session.getId());
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
// 현재 인증된 사용자 정보를 가져옴
        SecurityContext securityContext = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
        String username = "Unknown User";
        if (securityContext != null && securityContext.getAuthentication() != null &&
                securityContext.getAuthentication().getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
            username = userDetails.getUsername();
        }
        String formattedMessage = username + ": " + payload;
        System.out.println("Received message: " + formattedMessage);
        synchronized (sessions) {
            for (WebSocketSession webSocketSession : sessions) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(formattedMessage));
                }
            }
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Connection closed with session: " + session.getId());
    }
}