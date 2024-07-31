package com.beancontainer.domain.chatroom.websocket;


import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WebSocketHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("세션 연결 성공 :: " + session.getId());
        sessions.add(session);
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        System.out.println("수신된 메시지 :: " + payload);
//
//        SecurityContext securityContext = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
//        String username = "Unknown user";
//
//        if (securityContext != null && securityContext.getAuthentication() != null &&
//                securityContext.getAuthentication().getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
//            username = userDetails.getUsername();
//        }
//
//        for (WebSocketSession webSocketSession : sessions) {
//            if (webSocketSession.isOpen()) {
//                webSocketSession.sendMessage(new TextMessage(username + ": " + payload));
//            }
//        }
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("세션 연결 종료 :: " + session.getId());
    }
}
