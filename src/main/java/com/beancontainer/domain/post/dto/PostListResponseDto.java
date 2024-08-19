package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.post.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private int commentCount;   // 댓글 수
    private int likeCount;  // 좋아요 수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;  // 조회수

    public PostListResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.nickname = post.getMember().getNickname();
        this.commentCount = post.getCommentCount();
        this.likeCount = post.getLikeCount();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.views = post.getViews();
    }
}
