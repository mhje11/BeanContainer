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

    @Column(nullable = false, unique = true)
    private String nickname; //닉네임

    @Column(nullable = false, unique = true)
    private String userId; //로그인 아이디

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage;

    public Member(String name, String nickname, String userId, String password, Role role) {
        this.name = name;
        this.nickname = nickname;
        this.userId = userId;
        this.password = password;
        this.role = role;
    }

    public static Member createMember(String name, String nickname, String userId, String password) {
        Member member = new Member();
        member.name = name;
        member.nickname = nickname;
        member.userId = userId;
        member.password = password;
        member.role = Role.MEMBER; // 기본 역할을 MEMBER로 설정
        return member;
    }


}
