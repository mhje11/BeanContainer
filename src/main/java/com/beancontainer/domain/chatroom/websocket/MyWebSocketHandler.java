package com.beancontainer.domain.chatroom.websocket;


import com.beancontainer.domain.chatroom.service.ChatRoomService;
import com.beancontainer.domain.message.entity.Message;
import com.beancontainer.domain.message.service.MessageService;
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
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;

    public MyWebSocketHandler(MessageService messageService, ChatRoomService chatRoomService) {
        this.messageService = messageService;
        this.chatRoomService = chatRoomService;
    }

    //웹소켓으로 접속한 세션들을 관리하기 위한 저장소를 생성한다.
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("세션 연결 성공 :: "+session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); //실제 메시지 (헤더나 메타정보나 이런것들을 제외한..)
        System.out.println("수신된 메시지 :: "+payload);

        //현재 인증된 사용자 정보 가져옴.
        SecurityContext securityContext = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
        String username = "Unknown user";

        //SecurityContext와 인증 정보를 확인하고, UserDetails를 통해 사용자 이름을 가져옴
        if(securityContext != null && securityContext.getAuthentication() != null &&
                securityContext.getAuthentication().getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
            username = userDetails.getUsername();
        }

        Long chatRoomId = Long.parseLong(payload.split(":")[0]);
        String content = payload.split(":")[1];
        Message chatMessage = new Message(content, chatRoomService.getChatRoom(chatRoomId));
        messageService.sendMessage(chatMessage);

        //모든 활성화된 WebSocket 세션에 대해 메시지를 전송
        for (WebSocketSession webSocketSession : sessions) {
            if(webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(username + ": " + content));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("세션 연결 종료 :: "+ session.getId());
    }
}
