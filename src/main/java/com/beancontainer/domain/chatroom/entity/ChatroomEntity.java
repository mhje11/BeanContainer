package com.beancontainer.domain.chatroom.entity;

import com.beancontainer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chatrooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ChatListEntity> users = new HashSet<>();

    // users 컬렉션을 조작하는 메서드 추가
    public void addUser(ChatListEntity chatListEntity) {
        this.users.add(chatListEntity);
        chatListEntity.setChatroom(this);
    }

}