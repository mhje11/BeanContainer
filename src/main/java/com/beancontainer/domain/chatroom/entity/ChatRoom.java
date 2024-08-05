package com.beancontainer.domain.chatroom.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "max_participant", nullable = false)
    private int maxParticipant;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChatRoom(String title, int maxParticipant, String ownerName, String district) {
        this.title = title;
        this.maxParticipant = maxParticipant;
        this.ownerName = ownerName;
        this.district = district;
    }

    public ChatRoom(Long id) {
        this.id = id;
    }

}
