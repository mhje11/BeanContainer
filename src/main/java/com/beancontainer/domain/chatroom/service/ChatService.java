package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatMessage;
import com.beancontainer.domain.chatroom.entity.ChatListEntity;
import com.beancontainer.domain.chatroom.entity.ChatroomEntity;
import com.beancontainer.domain.chatroom.entity.MessageEntity;
import com.beancontainer.domain.chatroom.repository.ChatListRepository;
import com.beancontainer.domain.chatroom.repository.ChatroomRepository;
import com.beancontainer.domain.chatroom.repository.MessageRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    public List<ChatListEntity> getChatList(String userId) {
        Member user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatListRepository.findByMember(user);
    }

    public ChatroomEntity getChatroom(Long id) {
        return chatroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));
    }

    public boolean hasAccess(String userId, ChatroomEntity chatroom) {
        Member user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatroom.getUsers().stream()
                .anyMatch(chatListEntity -> chatListEntity.getMember().equals(user));
    }

    // 새로 추가한 메서드
    public void addUserToChatroom(ChatroomEntity chatroom, Member member) {
        ChatListEntity chatListEntity = ChatListEntity.builder()
                .chatroom(chatroom)
                .member(member)
                .build();

        chatroom.addUser(chatListEntity);
    }

    // Chatroom 생성 및 사용자 추가 메서드
    @Transactional
    public ChatroomEntity createChatroom(String userId1, String userId2) {
        Member member1 = memberRepository.findByUserId(userId1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Member member2 = memberRepository.findByUserId(userId2)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatroomEntity chatroom = ChatroomEntity.builder()
                .name(member1.getName() + ", " + member2.getName())
                .build();
        chatroomRepository.save(chatroom);

        addUserToChatroom(chatroom, member1);
        addUserToChatroom(chatroom, member2);

        return chatroom;
    }

    @Transactional
    public MessageEntity saveMessage(ChatMessage chatMessage) {
        ChatroomEntity chatroom = chatroomRepository.findById(chatMessage.getChatroomId())
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));
        Member sender = memberRepository.findByName(chatMessage.getSender())
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
        Member user = memberRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatListRepository.findByMemberOrderByIdDesc(user);
    }

    @Transactional
    public Long getOrCreateChatroom(String name1, String name2) {
        Member user1 = memberRepository.findByName(name1)
                .orElseThrow(() -> new RuntimeException("User not found with name: " + name1));
        Member user2 = memberRepository.findByName(name2)
                .orElseThrow(() -> new RuntimeException("User not found with name: " + name2));

        return chatroomRepository.findByUsersContainingAndUsersContaining(user1, user2)
                .map(ChatroomEntity::getId)
                .orElseGet(() -> {
                    ChatroomEntity newChatroom = ChatroomEntity.builder()
                            .name(name1 + ", " + name2)
                            .build();
                    chatroomRepository.save(newChatroom);

                    addUserToChatroom(newChatroom, user1);
                    addUserToChatroom(newChatroom, user2);

                    return newChatroom.getId();
                });
    }

}
