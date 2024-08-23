package com.beancontainer.domain.chatroom.dto;

import com.beancontainer.domain.chatroom.model.ChatRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoomDTO {

    private String roomId;
    private String name;
    private long userCount;

    @Builder
    public ChatRoomDTO(String roomId, String name, long userCount) {
        this.roomId = roomId;
        this.name = name;
        this.userCount = userCount;
    }

    // 엔티티로 변환
    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .roomId(roomId != null ? roomId : UUID.randomUUID().toString())  // roomId가 없으면 새로 생성
                .name(name)
                .userCount(userCount)
                .build();
    }
}