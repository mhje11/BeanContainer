package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.post.entity.Post;
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

    public PostDetailsResponseDto(Post updatedPost) {
        this.id = updatedPost.getId();
        this.title = updatedPost.getTitle();
        this.nickname = updatedPost.getMember().getNickname();
        this.createdAt = updatedPost.getCreatedAt();
        this.updatedAt = updatedPost.getUpdatedAt();
        this.views = updatedPost.getViews();
        this.content = updatedPost.getContent();
    }
}
