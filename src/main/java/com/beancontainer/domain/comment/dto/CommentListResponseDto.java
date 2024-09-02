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
    private String profileImageUrl;
    private String content;
    private LocalDateTime createdAt;
    private boolean authorCheck;

}
