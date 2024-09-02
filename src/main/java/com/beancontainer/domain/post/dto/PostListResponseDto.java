package com.beancontainer.domain.post.dto;

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
    private String profileImageUrl;
    private int commentCount;   // 댓글 수
    private int likeCount;  // 좋아요 수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;  // 조회수
}
