package com.beancontainer.domain.message.entity;

import com.beancontainer.domain.chatroom.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content; // 닉네임 : 내용~~

    private LocalDateTime sentAt  = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id",nullable = false)
    private ChatRoom chatRoom;

    public Message(String content, ChatRoom chatRoom) {
        this.content = content;
        this.chatRoom = chatRoom;
    }


}
