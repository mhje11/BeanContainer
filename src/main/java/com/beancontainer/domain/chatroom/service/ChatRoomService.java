package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomDto> findAllRoom() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomDto findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .map(ChatRoomDto::from)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }

    @Transactional
    public ChatRoomDto createChatRoom(String name, int capacity, String userId) {
        Member creator = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        ChatRoom chatRoom = new ChatRoom(name, creator, capacity);
        chatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.from(chatRoom);
    }

    @Transactional
    public void enterChatRoom(Long roomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        if (chatRoom.isFull()) {
            throw new RuntimeException("Chat room is full");
        }

        chatRoom.incrementUserCount();
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void leaveChatRoom(Long roomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        chatRoom.decrementUserCount();
        chatRoomRepository.save(chatRoom);
    }

}