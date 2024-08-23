package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatMessageDTO;
import com.beancontainer.domain.chatroom.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomService chatRoomService;

    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessageDTO chatMessageDTO) {
       // chatMessageDTO.setUserCount(chatRoomRepository.getUserCount(chatMessageDTO.getRoomId()));
        if (ChatMessage.MessageType.ENTER.equals(chatMessageDTO.getType())) {
            chatMessageDTO.setMessage(chatMessageDTO.getSender() + "님이 방에 입장했습니다.");
            chatMessageDTO.setSender("[알림]");
        } else if (ChatMessage.MessageType.QUIT.equals(chatMessageDTO.getType())) {
            chatMessageDTO.setMessage(chatMessageDTO.getSender() + "님이 방에서 나갔습니다.");
            chatMessageDTO.setSender("[알림]");
        }

        // 엔티티로 변환하여 Redis에 발송
        ChatMessage chatMessage = chatMessageDTO.toEntity();
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}