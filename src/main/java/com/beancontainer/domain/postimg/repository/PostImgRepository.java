package com.beancontainer.domain.postimg.repository;

import com.beancontainer.domain.postimg.entity.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImgRepository extends JpaRepository<PostImg, Long> {

    List<PostImg> findByPostId(Long postId);
}
