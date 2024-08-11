package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
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

    public PostListResponseDto(Post post, int likeCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.nickname = post.getMember().getNickname();
        this.commentCount = post.getCommentCount();
//        if (post.getLikes().isEmpty()) {
//            this.likeCount = 0;
//        } else {
//            this.likeCount = post.getLikes().size();
//        }
        this.likeCount = likeCount;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.views = post.getViews();
    }
}
