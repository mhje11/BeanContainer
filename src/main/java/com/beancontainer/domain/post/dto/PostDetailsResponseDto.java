package com.beancontainer.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailsResponseDto {
    private Long id;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int views;
    private String content;

}
