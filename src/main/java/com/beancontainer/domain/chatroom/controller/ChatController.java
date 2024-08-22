package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.model.ChatMessage;
import com.beancontainer.domain.chatroom.repo.ChatRoomRepository;
import com.beancontainer.domain.chatroom.service.ChatService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUserId()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        String nickname = member.getNickname();
        // 로그인 회원 정보로 대화명 설정
        message.setSender(nickname);
        // 채팅방 인원수 세팅
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
    }
}
