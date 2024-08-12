package com.beancontainer.domain.chatroom.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoom {
    private String roomId; // 채팅방의 고유 ID
    private String name;   // 채팅방 이름

    // 새로운 채팅방을 생성하는 정적 메서드
    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString(); // 고유한 ID 생성
        chatRoom.name = name;
        return chatRoom;
    }
}