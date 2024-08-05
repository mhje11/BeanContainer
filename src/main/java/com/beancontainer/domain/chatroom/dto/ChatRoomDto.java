package com.beancontainer.domain.chatroom.dto;


import com.beancontainer.domain.chatroom.entity.ChatRoom;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomDto {
    private Long id;
    private String title;
    private String ownerName;
    private LocalDateTime createdAt;
    private int maxParticipant;

    public ChatRoomDto(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.ownerName = chatRoom.getOwnerName();
        this.createdAt = chatRoom.getCreatedAt();
        this.maxParticipant = chatRoom.getMaxParticipant();
    }
}
