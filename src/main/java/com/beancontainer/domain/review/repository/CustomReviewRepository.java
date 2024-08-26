package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.entity.Review;

import java.util.List;
import java.util.Map;

public interface CustomReviewRepository {
    List<ReviewResponseDto> findAllByCafeId(Long cafeId);

//    Map<String, Long> findCategoryFrequenciesByCafeId(Long cafeId);
}
