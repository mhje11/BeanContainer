package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatMessage;
import com.beancontainer.domain.chatroom.entity.ChatListEntity;
import com.beancontainer.domain.chatroom.entity.ChatroomEntity;
import com.beancontainer.domain.chatroom.entity.MessageEntity;
import com.beancontainer.domain.chatroom.entity.UserEntity;
import com.beancontainer.domain.chatroom.repository.ChatListRepository;
import com.beancontainer.domain.chatroom.repository.ChatroomRepository;
import com.beancontainer.domain.chatroom.repository.MessageRepository;
import com.beancontainer.domain.chatroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatListRepository chatListRepository;
    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<ChatListEntity> getChatList(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatListRepository.findByUser(user);
    }

    public ChatroomEntity getChatroom(Long id) {
        return chatroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));
    }

    public boolean hasAccess(String username, ChatroomEntity chatroom) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatroom.getUsers().contains(user);
    }

    @Transactional
    public ChatroomEntity createChatroom(String email1, String email2) {
        UserEntity user1 = userRepository.findByEmail(email1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity user2 = userRepository.findByEmail(email2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatroomEntity chatroom = ChatroomEntity.builder()
                .name(user1.getName() + ", " + user2.getName())
                .build();
        chatroomRepository.save(chatroom);

        ChatListEntity chatList1 = ChatListEntity.builder()
                .user(user1)
                .chatroom(chatroom)
                .build();
        ChatListEntity chatList2 = ChatListEntity.builder()
                .user(user2)
                .chatroom(chatroom)
                .build();
        chatListRepository.saveAll(Arrays.asList(chatList1, chatList2));

        return chatroom;
    }

    @Transactional
    public MessageEntity saveMessage(ChatMessage chatMessage) {
        ChatroomEntity chatroom = chatroomRepository.findById(chatMessage.getChatroomId())
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));
        UserEntity sender = userRepository.findByName(chatMessage.getSender())
                .orElseThrow(() -> new RuntimeException("User not found"));

        MessageEntity messageEntity = MessageEntity.builder()
                .chatroom(chatroom)
                .sender(sender)
                .message(chatMessage.getMessage())
                .build();

        return messageRepository.save(messageEntity);
    }

    public List<MessageEntity> getChatroomMessages(Long chatroomId) {
        ChatroomEntity chatroom = getChatroom(chatroomId);
        return messageRepository.findByChatroomOrderByIdAsc(chatroom);
    }

    public List<ChatListEntity> getRecentChats(String username) {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatListRepository.findByUserOrderByIdDesc(user);
    }

    @Transactional
    public Long getOrCreateChatroom(String name1, String name2) {
        UserEntity user1 = userRepository.findByName(name1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity user2 = userRepository.findByName(name2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return chatroomRepository.findByUsersContainingAndUsersContaining(user1, user2)
                .map(ChatroomEntity::getId)
                .orElseGet(() -> {
                    ChatroomEntity newChatroom = ChatroomEntity.builder()
                            .name(name1 + ", " + name2)
                            .build();
                    newChatroom.addUser(user1);
                    newChatroom.addUser(user2);
                    chatroomRepository.save(newChatroom);

                    ChatListEntity chatList1 = ChatListEntity.builder()
                            .user(user1)
                            .chatroom(newChatroom)
                            .build();
                    ChatListEntity chatList2 = ChatListEntity.builder()
                            .user(user2)
                            .chatroom(newChatroom)
                            .build();
                    chatListRepository.saveAll(Arrays.asList(chatList1, chatList2));

                    return newChatroom.getId();
                });
    }
}