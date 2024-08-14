package com.beancontainer.domain.chatroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@NoArgsConstructor

public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();



    @OneToMany(mappedBy ="chatRoom")
    private Set<ChatList> chatList;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> chatMessageList;


    public ChatRoom(Set<ChatList> chatList, List<ChatMessage> chatMessageList, String name) {
        this.chatList = chatList;
        this.chatMessageList = chatMessageList;
        this.name = name;
    }
}