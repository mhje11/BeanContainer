package com.beancontainer.domain.chatroom.dto;

import com.beancontainer.domain.chatroom.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
        }
        return dto;
    }


}