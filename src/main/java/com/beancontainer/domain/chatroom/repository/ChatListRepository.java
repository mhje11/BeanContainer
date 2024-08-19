package com.beancontainer.domain.chatroom.repository;


import com.beancontainer.domain.chatroom.entity.ChatList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatListRepository extends JpaRepository<ChatList, Long> {
}
