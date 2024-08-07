package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatMessage;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
//@RequestMapping("/chat")
public class ChatController {
    private final MemberRepository memberRepository;
//    private final ChatService chatService;
//
//    @PostMapping
//    public ChatRoom createRoom(@RequestParam String name) {
//        return chatService.createRoom(name);
//    }
//
//    @GetMapping
//    public List<ChatRoom> findAllRoom() {
//        return chatService.findAllRoom();
//    }
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("해당 멤버를 찾을 수 없습니다"));
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(member.getNickname()+"님이 입장하셨습니다");
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
}
