package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatMessageDto;
import com.beancontainer.domain.chatroom.entity.ChatMessage;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatMessageRepository;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    // 메시지를 저장하고 이를 DTO로 반환
    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto messageDto, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        Member sender = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        String message = messageDto.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "No content"; // 기본 메시지 설정
        }

        ChatMessage.MessageType messageType = ChatMessage.MessageType.valueOf(messageDto.getType().name());

        ChatMessage chatMessage;
        if (ChatMessage.MessageType.ENTER.equals(messageType)) {
            message = sender.getNickname() + "님이 입장하셨습니다.";
        }

        chatMessage = new ChatMessage(messageType, chatRoom, sender, message);
        chatMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageDto.from(chatMessage);
    }

    // 메시지 ID로 메시지 조회
    @Transactional(readOnly = true)
    public ChatMessageDto findMessageById(Long id) {
        return chatMessageRepository.findById(id)
                .map(ChatMessageDto::from)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }
}