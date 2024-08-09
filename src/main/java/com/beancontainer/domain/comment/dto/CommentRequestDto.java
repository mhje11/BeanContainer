package com.beancontainer.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequestDto {
    private Long postId;
    private String nickname;
    private String content;
}
