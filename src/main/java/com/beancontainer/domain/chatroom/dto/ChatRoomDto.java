package com.beancontainer.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDto {
    private String roomId;
    private String name;

}
