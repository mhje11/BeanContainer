package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅방을 찾는 메서드
    public List<ChatRoomDto> findAllRoom() {
        return chatRoomRepository.findAll().stream()
                .map(chatRoom -> new ChatRoomDto(
                        chatRoom.getId(),
                        chatRoom.getName(),
                        chatRoom.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // ID로 채팅방을 찾는 메서드
    public ChatRoomDto findRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room Id"));
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCreatedAt()
        );
    }

    // 채팅방을 생성하는 메서드
    public ChatRoomDto createRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoomRepository.save(chatRoom);
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCreatedAt()
        );
    }
}

//이슈 쓸때 기능 세세하게..? ㅇㅋ...