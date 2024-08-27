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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

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

        ChatMessage chatMessage = new ChatMessage(
                ChatMessage.MessageType.valueOf(messageDto.getType().name()),
                chatRoom,
                sender,
                message
        );

        chatMessage = chatMessageRepository.save(chatMessage);

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage = new ChatMessage(
                    ChatMessage.MessageType.ENTER,
                    chatRoom,
                    sender,
                    sender.getNickname() + "님이 입장하셨습니다."
            );
            chatMessage = chatMessageRepository.save(chatMessage);
        }

        return ChatMessageDto.from(chatMessage);
    }

    @Transactional(readOnly = true)
    public ChatMessageDto findMessageById(Long id) {
        return chatMessageRepository.findById(id)
                .map(ChatMessageDto::from)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    // 필요에 따라 다른 메서드들을 추가할 수 있습니다.
}