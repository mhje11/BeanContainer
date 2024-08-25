package com.beancontainer.domain.chatroom.dto;

import com.beancontainer.domain.chatroom.entity.ChatRoom;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoomDto {
    private Long roomId;
    private String name;

    public static ChatRoomDto from(ChatRoom chatRoom) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setRoomId(chatRoom.getRoomId());
        dto.setName(chatRoom.getName());
        return dto;
    }
}