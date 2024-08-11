package com.beancontainer.domain.postimg.repository;

import com.beancontainer.domain.postimg.entity.PostImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImgRepository extends JpaRepository<PostImg, Long> {
}
