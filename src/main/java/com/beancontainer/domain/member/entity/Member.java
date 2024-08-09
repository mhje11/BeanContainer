package com.beancontainer.domain.member.entity;

import com.beancontainer.domain.memberprofileimg.entity.ProfileImage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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

    //닉네임 수정
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }


}
