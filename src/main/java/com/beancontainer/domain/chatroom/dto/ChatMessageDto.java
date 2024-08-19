package com.beancontainer.domain.chatroom.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private Long id;
    private String content;
    private Long chatRoomId;
    private LocalDateTime createdAt;
}

