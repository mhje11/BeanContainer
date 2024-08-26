package com.beancontainer.domain.chatroom.dto;

import com.beancontainer.domain.chatroom.entity.ChatMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatMessageDto {
    private Long id;
    private MessageType type;
    private Long roomId;
    private String senderNickname;
    private String message;
    private LocalDateTime timestamp;

    public enum MessageType {
        ENTER, TALK
    }

    public static ChatMessageDto from(ChatMessage chatMessage) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(chatMessage.getId());
        dto.setType(MessageType.valueOf(chatMessage.getType().name()));
        dto.setRoomId(chatMessage.getChatRoom().getRoomId());
        dto.setSenderNickname(chatMessage.getSender().getNickname());
        dto.setMessage(chatMessage.getMessage());
        dto.setTimestamp(chatMessage.getTimestamp());
        return dto;
    }
}