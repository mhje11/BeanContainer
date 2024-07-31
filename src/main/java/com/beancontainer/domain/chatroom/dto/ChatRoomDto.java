package com.beancontainer.domain.chatroom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDto {
    private Long id;
    private String title;
    private int maxParticipant;
    private String ownerName;
    private String district;
}