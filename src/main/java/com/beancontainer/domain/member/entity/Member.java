package com.beancontainer.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private static final String DEFAULT_PROFILE_IMAGE = "/images/BeanContainer.png";

    private String provider;; //OAuth2 제공자(ex : naver, kakao)
    private String providerId; //OAuth2 로그인 Id


    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder //필요한 필드만 사용하기 위해 빌더 어노테이션 사용
    public Member(String name, String nickname, String userId, String password, String email, Role role, String provider, String providerId) {
        this.name = name;
        this.nickname = nickname;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
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

    //OAuth2 로그인 멤버 생성
//    public static Member createOAuth2Member(String userId, String name, String email, String provider, String providerId) {
//        return Member.builder()
//                .userId(userId)
//                .password("{noop}oauth2")
//                .name(name)
//                .nickname(name)  // 초기 닉네임을 이름으로 설정
//                .email(email)
//                .role(Role.MEMBER)
//                .provider(provider)
//                .providerId(providerId)
//                .build();
//    }

    public Member updateOAuth2Info(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }

    //닉네임 수정
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 프로필 이미지 URL 업데이트
    public void updateProfileImageUrl(String newProfileImageUrl) {
        this.profileImageUrl = newProfileImageUrl;
    }

    //null 이면 기본 경로 불러옴
    public String getProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl : DEFAULT_PROFILE_IMAGE;
    }

    public void cancelAccount() {
        this.deletedAt = LocalDateTime.now();
    }


}
