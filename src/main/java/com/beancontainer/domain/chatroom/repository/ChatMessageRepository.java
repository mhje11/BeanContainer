package com.beancontainer.domain.chatroom.repository;

import com.beancontainer.domain.chatroom.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}