package com.beancontainer.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentRequestDto {
    private Long postId;
    private String memberLoginId;
    private String content;
}
