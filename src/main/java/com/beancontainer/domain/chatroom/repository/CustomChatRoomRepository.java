package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;

import java.util.List;

public interface CustomChatRoomRepository {

    List<ChatRoomDto> findAllActiveRooms();
}
