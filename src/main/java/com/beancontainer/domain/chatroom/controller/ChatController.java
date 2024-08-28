package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatMessageDto;
import com.beancontainer.domain.chatroom.service.ChatMessageService;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
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

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto messageDto, Principal principal) {
        String userId = principal.getName();
        ChatMessageDto savedMessage = chatMessageService.saveMessage(messageDto, userId);
        messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getRoomId(), savedMessage);
    }
    @MessageMapping("/chat/enter")
    public void enter(ChatMessageDto messageDto, Principal principal) {
        String userId = principal.getName();
        try {
            chatRoomService.enterChatRoom(messageDto.getRoomId(), userId);
            ChatMessageDto savedMessage = chatMessageService.saveMessage(messageDto, userId);
            messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getRoomId(), savedMessage);
        } catch (RuntimeException e) {
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", "Chat room is full");
        }
    }

    @MessageMapping("/chat/leave")
    public void leave(ChatMessageDto messageDto, Principal principal) {
        String userId = principal.getName();
        chatRoomService.leaveChatRoom(messageDto.getRoomId(), userId);
        ChatMessageDto savedMessage = chatMessageService.saveMessage(messageDto, userId);
        messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getRoomId(), savedMessage);
    }
}