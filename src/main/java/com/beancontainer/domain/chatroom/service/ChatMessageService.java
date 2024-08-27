package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatMessageDto;
import com.beancontainer.domain.chatroom.entity.ChatMessage;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatMessageRepository;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto messageDto, String userId) {
        log.info("메세지: {}",messageDto.getMessage());
        log.info("아디: {}",messageDto.getRoomId());
        log.info("타입: {}",messageDto.getType());
        log.info("보낸닉넴: {}",messageDto.getSenderNickname());

        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        Member sender = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        ChatMessage chatMessage = new ChatMessage(
                ChatMessage.MessageType.valueOf(messageDto.getType().name()),
                chatRoom,
                sender,
                messageDto.getMessage()
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