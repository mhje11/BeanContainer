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
    private String creatorNickname;
    private int capacity;
    private int currentUserCount;

    public static ChatRoomDto from(ChatRoom chatRoom) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setRoomId(chatRoom.getRoomId());
        dto.setName(chatRoom.getName());
        dto.setCapacity(chatRoom.getCapacity());
        dto.setCurrentUserCount(chatRoom.getCurrentUserCount());
        // Null 체크 추가
        if (chatRoom.getCreator() != null) {
            dto.setCreatorNickname(chatRoom.getCreator().getNickname());
        } else {
            dto.setCreatorNickname("Unknown Creator"); // 기본 값 설정
            // 또는
            // throw new IllegalStateException("ChatRoom의 Creator가 설정되지 않았습니다.");
        }
        return dto;
    }
}