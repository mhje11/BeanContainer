package com.beancontainer.domain.chatroom.entity;

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

    @ManyToMany
    @JoinTable(
            name = "chatroom_users",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<UserEntity> users = new HashSet<>();

    // users 컬렉션을 조작하는 메서드 추가
    public void addUser(UserEntity user) {
        if (this.users == null) {
            this.users = new HashSet<>();
        }
        this.users.add(user);
    }

}