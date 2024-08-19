package com.beancontainer.domain.chatroom.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageCreateDto {
    //생성
    private String content;
    private Long chatRoomId;
}

