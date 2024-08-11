package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByCafeId(Long cafeId);

    @Query("select AVG (r.score) from Review r where r.cafe.id = :cafeId")
    Double calculateAverageScoreByCafeId(Long cafeId);
}
