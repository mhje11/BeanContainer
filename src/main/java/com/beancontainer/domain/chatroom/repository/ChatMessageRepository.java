package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}