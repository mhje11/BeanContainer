package com.beancontainer.domain.comment.dto;

import com.beancontainer.domain.comment.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private boolean authorCheck;

    public CommentListResponseDto(Comment comment, boolean authorCheck) {
        this.id = comment.getId();
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.authorCheck = authorCheck;
    }
}
