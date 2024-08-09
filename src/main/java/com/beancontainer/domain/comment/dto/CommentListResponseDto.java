package com.beancontainer.domain.comment.dto;

import com.beancontainer.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private boolean isAuthor;

    public CommentListResponseDto(Comment comment, Long currentUserId) {
        this.id = comment.getId();
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.isAuthor = comment.getMember().getId().equals(currentUserId);
    }
}
