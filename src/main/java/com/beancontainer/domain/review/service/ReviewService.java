package com.beancontainer.domain.review.service;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.cafe.service.CafeService;
import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.category.repository.CategoryRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.review.dto.ReviewCreateDto;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.dto.ReviewUpdateDto;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.domain.reviewcategory.entity.ReviewCategory;
import com.beancontainer.domain.reviewcategory.repository.ReviewCategoryRepository;
import com.beancontainer.global.exception.CafeNotFoundException;
import com.beancontainer.global.exception.CategoryNotFoundException;
import com.beancontainer.global.exception.ReviewNotFoundException;
import com.beancontainer.global.exception.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CafeRepository cafeRepository;
    private final CategoryRepository categoryRepository;
    private final CafeService cafeService;

    @Transactional
    public void createReview(ReviewCreateDto reviewCreateDto, String userLoginId) {
        Member member = memberRepository.findByUserId(userLoginId).orElseThrow(() -> new UserNotFoundException("해당 유저를 찾을 수 없습니다."));
        Cafe cafe = cafeRepository.findById(reviewCreateDto.getCafeId()).orElseThrow(() -> new CafeNotFoundException("카페를 찾을 수 없습니다."));

        Review review = new Review(member, cafe, reviewCreateDto.getContent(), reviewCreateDto.getScore(), new HashSet<>());

        Set<ReviewCategory> reviewCategories = reviewCreateDto.getCategoryNames().stream()
                .map(categoryName -> {
                    Category category = categoryRepository.findByName(categoryName)
                            .orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다: " + categoryName));
                     ReviewCategory reviewCategory = new ReviewCategory(review, category);
                     review.addReviewCategory(category);
                     return reviewCategory;
                })
                .collect(Collectors.toSet());

        reviewRepository.save(review);

        cafeService.updatedCafeCategories(reviewCreateDto.getCafeId());
    }


    public List<ReviewResponseDto> findReviewByCafeId(Long cafeId) {
        List<Review> cafes = reviewRepository.findAllByCafeId(cafeId);
        return cafes.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updateReview(Long reviewId, ReviewUpdateDto reviewUpdateDto) {
        Review existingReview = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        Set<ReviewCategory> updateCategories = reviewUpdateDto.getCategoryNames().stream()
                .map(categoryName -> {
                    Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryNotFoundException("카테고리를 찾을 수 없습니다: " + categoryName));
                    return new ReviewCategory(existingReview, category);
                })
                .collect(Collectors.toSet());
        Review updatedReview = new Review(existingReview.getId(), existingReview.getMember(), existingReview.getCafe(), reviewUpdateDto.getContent(), reviewUpdateDto.getScore(), updateCategories);
        Review review = reviewRepository.save(updatedReview);
        cafeService.updatedCafeCategories(existingReview.getCafe().getId());

        return review.getId();
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }


}
