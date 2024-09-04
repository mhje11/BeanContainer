package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CustomReviewRepository {
    Page<ReviewResponseDto> findAllByCafeId(Long cafeId, Pageable pageable);
}
