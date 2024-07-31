package com.beancontainer.domain.post.entity;

import com.beancontainer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 작성자*/

    private String username;    // 멤버 생기기 전 테스트 목적으로 사용

    @Column(nullable = false)
    private String title;   // 제목

    @Column(nullable = false)
    private String content; // 내용

    private int views = 0;  // 조회수

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();  // 작성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // 수정일

    private String uuid;    // 이미지

    public Post(String username, String title, String content, String uuid) {
        this.username = username;
        this.title = title;
        this.content = content;
        this.uuid = uuid;
    }
}
