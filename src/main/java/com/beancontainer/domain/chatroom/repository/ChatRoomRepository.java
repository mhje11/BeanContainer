package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 채팅방 이름으로 존재 여부를 확인하는 메서드 정의
    boolean existsByName(String name);
}