package com.beancontainer.domain.post.repository;

import com.beancontainer.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository{

    void deleteByMemberId(Long memberId);
}
