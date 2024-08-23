package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatMessageDTO;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
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

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDTO messageDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUserId()).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        String nickname = member.getNickname();
        // 로그인 회원 정보로 대화명 설정
        messageDTO.setSender(nickname);

        // 채팅방 인원수 세팅
 //       messageDTO.setUserCount(chatRoomRepository.getUserCount(messageDTO.getRoomId()));

        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(messageDTO);
    }
}
