package com.beancontainer.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; //회원 이름

    @Column(nullable = false)
    private String nickname; //닉네임

    @Column(nullable = false, unique = true)
    private String userId; //로그인 아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email; //이메일 인증을 통한 회원가입 구현

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Member(String name, String nickname, String userId, String password, String email, Role role) {
        this.name = name;
        this.nickname = nickname;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.role = role;
    }


    public static Member createMember(String name, String nickname, String userId, String password, String email) {
        Member member = new Member();
        member.name = name;
        member.nickname = nickname;
        member.userId = userId;
        member.password = password;
        member.email = email;
        member.role = Role.MEMBER; // 기본 역할을 MEMBER로 설정
        return member;
    }

    //닉네임 수정
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 프로필 이미지 URL 업데이트
    public void updateProfileImageUrl(String newProfileImageUrl) {
        this.profileImageUrl = newProfileImageUrl;
    }




}
