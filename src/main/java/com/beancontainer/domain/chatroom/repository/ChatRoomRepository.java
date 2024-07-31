package com.beancontainer.domain.chatroom.repository;


import com.beancontainer.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByOwnerName(String ownerName);
}