package com.beancontainer.domain.like.repository;

import com.beancontainer.domain.like.entity.Likes;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostAndMember(Post post, Member member);

    int countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
