package com.beancontainer.domain.chatroom.service;


import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    //새로운 채팅방을 생성하고 데이터베이스에 저장
    @Transactional //데이터의 일관성을 보장
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }


    //특정 채팅방 ID로 채팅방을 조회하여 해당 채팅방 정보를 반환
    @Transactional(readOnly = true)
    public ChatRoom getChatRoom(Long chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        return chatRoom.orElseThrow(() -> new RuntimeException("ChatRoom not found"));
    }

    // 모든 채팅방을 조회
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getAllChatRooms() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomDto::new)
                .collect(Collectors.toList());
    }

    //소유한 채팅방을 조회
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getMyChatRooms(String ownerName) {
        return chatRoomRepository.findByOwnerName(ownerName).stream()
                .map(ChatRoomDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteChatRoom(Long chatId) {
        if (chatRoomRepository.existsById(chatId)) {
            chatRoomRepository.deleteById(chatId);
            return true;
        } else {
            return false;
        }
    }
}
