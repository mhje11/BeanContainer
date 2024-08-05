package com.beancontainer.domain.post.entity;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.postimg.entity.PostImg;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@ToString
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

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<PostImg> images = new ArrayList<>();

    public Post(Member member, String title, String content) {  // 게시글 작성
        this.member = member;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
