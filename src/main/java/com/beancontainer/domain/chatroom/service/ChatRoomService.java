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
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    //모든 채팅방 ㅁㄴ조회
    public List<ChatRoomDto> findAllRoom(){
        List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom();
        return chatRooms.stream()
                .map(chatRoom -> new ChatRoomDto(chatRoom.getRoomId(), chatRoom.getName()))
                .collect(Collectors.toList());
    }

    //채팅방 생성
    public ChatRoomDto createChatRoom(String name) {
        ChatRoom chatRoom = chatRoomRepository.createChatRoom(name);
        return new ChatRoomDto(chatRoom.getRoomId(), chatRoom.getName());
    }
}
