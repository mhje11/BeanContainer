package com.beancontainer.domain.chatroom.service;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
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
                .filter(room -> room.getCurrentUserCount() > 0)
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatRoomDto findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .filter(room -> room.getCurrentUserCount() > 0)
                .map(ChatRoomDto::from)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
    }

    @Transactional
    public ChatRoomDto createChatRoom(String name, int capacity, String userId) {
        Member creator = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = new ChatRoom(name, creator, capacity);
        chatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.from(chatRoom);
    }

    @Transactional
    public String enterRoom(Long roomId, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.getCurrentUserCount() >= chatRoom.getCapacity()) {
            throw new  CustomException(ExceptionCode.CHATROOM_FULL);
        }
        chatRoom.incrementUserCount();
        chatRoomRepository.save(chatRoom);
        return username;
    }

    @Transactional
    public void exitRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
        chatRoom.decrementUserCount();
        chatRoomRepository.save(chatRoom);
        if (chatRoom.getCurrentUserCount() == 0) {
            chatRoomRepository.delete(chatRoom);
        }
    }
}