package com.beancontainer.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //주석

    @Column(nullable = false)
    private String name; //이름

    @Column(nullable = false, unique = true)
    private String username; //닉네임

    @Column(nullable = false, unique = true)
    private String userId; //로그인 아이디

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    private String uuid;

    private Member(String name, String username, String userId, String password, Role role, String uuid) {
        this.name = name;
        this.username = username;
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.uuid = uuid;
    }

    //setter 를 사용하여 엔티티에 추가하는 방법 대신 별도의 메소드를 만들어서 추가
    public static Member createMember(String name, String username, String userId, String password) {
        return new Member(name, username, userId, password, Role.MEMBER, UUID.randomUUID().toString());
    }
}