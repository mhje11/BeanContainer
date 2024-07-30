package com.beancontainer.domain.chatroom;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ChatRoom {
    @Id
    private Long id;
}
