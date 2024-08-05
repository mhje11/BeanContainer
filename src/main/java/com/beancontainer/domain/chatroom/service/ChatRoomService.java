package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public List<ChatRoomDto> getAllChatRooms() {
        return chatRoomRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ChatRoomDto> getUserChatRooms(String ownerName) {
        return chatRoomRepository.findByOwnerName(ownerName).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom = new ChatRoom();
        // Setting values using DTO setters
        chatRoomRepository.save(chatRoom);
        return toDto(chatRoom);
    }

    public void deleteChatRoom(Long chatId) {
        chatRoomRepository.deleteById(chatId);
    }

    private ChatRoomDto toDto(ChatRoom chatRoom) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setId(chatRoom.getId());
        chatRoomDto.setTitle(chatRoom.getTitle());
        chatRoomDto.setMaxParticipant(chatRoom.getMaxParticipant());
        chatRoomDto.setOwnerName(chatRoom.getOwnerName());
        chatRoomDto.setDistrict(chatRoom.getDistrict());
        return chatRoomDto;
    }
}
