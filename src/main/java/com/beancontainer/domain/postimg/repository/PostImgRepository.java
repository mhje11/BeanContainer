package com.beancontainer.domain.postimg.repository;

import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.postimg.entity.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImgRepository extends JpaRepository<PostImg, Long> {

    Optional<PostImg> findByPath(String path);

    List<PostImg> findByPathIn(List<String> paths);

    List<PostImg> findByPostId(Long postId);
}
