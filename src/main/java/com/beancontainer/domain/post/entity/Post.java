package com.beancontainer.domain.post.entity;

import com.beancontainer.domain.member.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 작성자

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

}
