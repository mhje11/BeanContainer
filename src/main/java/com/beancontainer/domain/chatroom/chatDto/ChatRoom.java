package com.beancontainer.domain.chatroom.chatDto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoom {
    private String roomId;
    private String name;

//    private Set<WebSocketSession> sessions = new HashSet<>();
//    @Builder
//    public ChatRoom(String roomId, String name) {
//        this.roomId = roomId;
//        this.name = name;
//    }
//    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService) {
//        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) {
//            sessions.add(session);
//            chatMessage.setMessage((chatMessage.getSender() + "님이 입장했습니다"));
//        }
//        sendMessage(chatMessage, chatService);
//    }
//    public <T> void sendMessage(T message, ChatService chatService) {
//        sessions.parallelStream().forEach(session -> chatService.sendMessage(session, message));
//    }

    /**
     * pub/sub 방식을 이용하면 구독자 관리가 알아서 되므로 웹소켓 세션 관리가 필요 없어집니다.
     * 또한 발송의 구현도 알아서 해결 되므로 일일이 클라이언트에게 메시지 발송하는 구현이 필요없어집니다.
     * 따라서 ChatRoom DTO는 다음과 같이 간소화됩니다.
     **/

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }
}
