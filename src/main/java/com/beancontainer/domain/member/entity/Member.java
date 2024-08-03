package com.beancontainer.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; //회원 이름

    @Column(name = "username", nullable = false, unique = true)
    private String nickname; //닉네임

    @Column(nullable = false, unique = true)
    private String userId; //로그인 아이디

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

    public Member(String name, String nickname, String userId, String password, Role role) {
        this.name = name;
        this.nickname = nickname;
        this.userId = userId;
        this.password = password;
        this.role = role;
    }

    //setter 를 사용하여 엔티티에 추가하는 방법 대신 별도의 메소드를 만들어서 추가
    public static Member createMember(String name, String nickname, String userId, String password) {
        return new Member(name, nickname, userId, password, Role.MEMBER);
    }
}
