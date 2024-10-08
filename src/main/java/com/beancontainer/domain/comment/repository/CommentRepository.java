package com.beancontainer.domain.comment.repository;

import com.beancontainer.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository{
    void deleteByPostId(Long postId);
    void deleteByMemberId(Long memberId);
}