package com.beancontainer.domain.member;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
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
}
