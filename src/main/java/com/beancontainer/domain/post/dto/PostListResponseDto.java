package com.beancontainer.domain.post.dto;

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
    // 댓글 수
    // 좋아요 수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;
}
