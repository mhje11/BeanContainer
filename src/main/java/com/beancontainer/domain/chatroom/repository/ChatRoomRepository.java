package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}