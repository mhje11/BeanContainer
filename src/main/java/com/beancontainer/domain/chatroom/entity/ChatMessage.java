package com.beancontainer.domain.chatroom.entity;


import com.beancontainer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public enum MessageType {
        ENTER, TALK
    }

    public ChatMessage(MessageType type, ChatRoom chatRoom, Member sender, String message) {
        this.type = type;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}