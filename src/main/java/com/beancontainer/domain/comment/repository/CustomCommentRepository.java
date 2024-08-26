package com.beancontainer.domain.comment.repository;

import com.beancontainer.domain.comment.dto.CommentListResponseDto;

import java.util.List;

public interface CustomCommentRepository {

    List<CommentListResponseDto> getAllComments(Long postId, Long currentUserId);
}
