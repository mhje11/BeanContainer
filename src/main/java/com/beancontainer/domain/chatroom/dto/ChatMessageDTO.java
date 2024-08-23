package com.beancontainer.domain.chatroom.dto;


import com.beancontainer.domain.chatroom.model.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {

    private ChatMessage.MessageType type;
    private String roomId;
    private String sender;
    private String message;
 //   private long userCount;

    @Builder
    public ChatMessageDTO(ChatMessage.MessageType type, String roomId, String sender, String message, long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
  //      this.userCount = userCount;
    }

    // 엔티티로 변환
    public ChatMessage toEntity() {
        return new ChatMessage(type, roomId, sender, message);
    }
}
