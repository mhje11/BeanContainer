package com.beancontainer.domain.chatroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
//사용자의 역할의 정의함
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 역할 이름을 저장
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // 역할 이름을 설정
    public Role(String name) {
        this.name = name;
    }
}