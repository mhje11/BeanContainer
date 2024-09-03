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


    // 모든 활성화된 채팅방을 조회하여 DTO 리스트로 반환
    @Transactional(readOnly = true)
    public List<ChatRoomDto> findAllRoom() {
//        return chatRoomRepository.findAll().stream()
//                .filter(ChatRoom::isActive)
//                .map(ChatRoomDto::from)
//                .collect(Collectors.toList());

        return chatRoomRepository.findAllActiveRooms();
    }

    // 특정 ID의 채팅방 조회 후 DTO로 반환
    @Transactional(readOnly = true)
    public ChatRoomDto findRoomById(Long id) {
        return chatRoomRepository.findById(id)
                .filter(ChatRoom::isActive)
                .map(ChatRoomDto::from)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
    }

    // 새로운 채팅방을 생성하고 DTO로 반환
    @Transactional
    public ChatRoomDto createChatRoom(String name, int capacity, String userId) {
        Member creator = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        ChatRoom chatRoom = new ChatRoom(name, creator, capacity);
        chatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.from(chatRoom);
    }

    // 채팅방 입장할 때 필요한 로직을 처리
    @Transactional
    public String enterRoom(Long roomId, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .filter(ChatRoom::isActive)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
        if (chatRoom.isFull()) {
            throw new CustomException(ExceptionCode.CHATROOM_FULL);
        }
        chatRoom.incrementUserCount();
        if (!chatRoom.isActive()) {
            chatRoom.reactivate();
        }
        chatRoomRepository.save(chatRoom);
        return username;
    }

    // 채팅방 나갈 때 필요한 로직을 처리
    @Transactional
    public void exitRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));
        chatRoom.decrementUserCount();
        chatRoomRepository.save(chatRoom);
    }
}