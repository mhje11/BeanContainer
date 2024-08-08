package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.postimg.entity.PostImg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> imageUrls;

    public PostDetailsResponseDto(Post updatedPost) {
        this.id = updatedPost.getId();
        this.title = updatedPost.getTitle();
        this.nickname = updatedPost.getMember().getNickname();
        this.createdAt = updatedPost.getCreatedAt();
        this.updatedAt = updatedPost.getUpdatedAt();
        this.views = updatedPost.getViews();
        this.content = updatedPost.getContent();
        this.imageUrls = updatedPost.getImages().stream()
                .map(PostImg::getPath)
                .collect(Collectors.toList());
    }
}
