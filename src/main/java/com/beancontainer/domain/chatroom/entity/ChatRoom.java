package com.beancontainer.domain.chatroom.entity;

import com.beancontainer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;


    @Column(name = "capacity", nullable = false)
    private int capacity = 0;

    @Column(name = "current_participants", nullable = false)
    private int currentUserCount = 0;

    public ChatRoom(String name, Member creator, int capacity) {
        this.name = name;
        this.creator = creator;
        this.capacity = capacity;
        this.currentUserCount = 0;
    }

    public boolean isFull() {
        return currentUserCount >= capacity;
    }

    public void incrementUserCount() {
        if (isFull()) {
            throw new RuntimeException("Chat room is full");
        }
        currentUserCount++;
    }

    public void decrementUserCount() {
        if (currentUserCount > 0) {
            currentUserCount--;
        }
    }
}
