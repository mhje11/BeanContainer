package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatMessageDto;
import com.beancontainer.domain.chatroom.service.ChatMessageService;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import com.beancontainer.global.auth.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatController {
    // WebSocket을 통해 수신된 채팅 메시지를 처리
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;

    /**
     * websocket 사용으로 principal을 사용해야함
     * 메시지를 데이터베이스에 저장 후, 해당 채팅방의 모든 구독자에게 메시지 전송
     */

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto messageDto, Principal principal) { // Principal: 사용자 인증 정보를 담음
        String userId = principal.getName();
        ChatMessageDto savedMessage = chatMessageService.saveMessage(messageDto, userId);
        messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getRoomId(), savedMessage);
    }
}
