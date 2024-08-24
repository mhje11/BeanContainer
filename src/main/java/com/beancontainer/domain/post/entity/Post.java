package com.beancontainer.domain.post.entity;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.postimg.entity.PostImg;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "comment_count")
    private int commentCount = 0;   // 댓글수

    private int views = 0;  // 조회수

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();  // 작성일

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // 수정일

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImg> images = new ArrayList<>();

    private int likeCount = 0;

    public Post(Member member, String title, String content) {  // 게시글 작성
        this.member = member;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {  // 게시글 수정
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가
    public void incrementViews() {
        this.views++;
    }

    // 댓글수 증가
    public void incrementCommentCount() {
        this.commentCount++;
    }

    // 댓글수 감소
    public void decrementCommentCount() {
        this.commentCount--;
    }

    // 좋아요 증가
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 감소
    public void decrementLikeCount() {
        this.likeCount--;
    }
}
