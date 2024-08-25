package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomDto> findAllRoom() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomDto findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .map(ChatRoomDto::from)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }

    @Transactional
    public ChatRoomDto createChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.from(chatRoom);
    }
}