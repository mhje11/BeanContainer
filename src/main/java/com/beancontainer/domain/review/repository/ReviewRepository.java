package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByCafeId(Long cafeId);
}
