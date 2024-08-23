package com.beancontainer.domain.chatroom.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long userCount;

    @Builder
    public ChatRoom(String roomId, String name, long userCount) {
        this.roomId = roomId;
        this.name = name;
        this.userCount = userCount;
    }

    public static ChatRoom create(String name) {
        return ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .name(name)
                .userCount(0) // 기본값으로 설정
                .build();
    }
    // userCount를 업데이트하는 메서드 추가
    public void updateUserCount(long userCount) {
        this.userCount = userCount;
    }
}