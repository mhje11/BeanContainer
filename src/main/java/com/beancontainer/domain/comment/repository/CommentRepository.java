package com.beancontainer.domain.comment.repository;

import com.beancontainer.domain.comment.entity.Comment;
import com.beancontainer.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    void deleteByPostId(Long postId);
}