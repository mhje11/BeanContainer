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
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
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

    //리뷰 생성
    @Transactional
    public void createReview(ReviewCreateDto reviewCreateDto, UserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(ExceptionCode.NO_LOGIN);
        }
        Member member = memberRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        Cafe cafe = cafeRepository.findById(reviewCreateDto.getCafeId()).orElseThrow(() -> new CustomException(ExceptionCode.CAFE_NOT_FOUND));

        Review review = new Review(member, cafe, reviewCreateDto.getContent(), reviewCreateDto.getScore(), new HashSet<>());

        Set<ReviewCategory> reviewCategories = reviewCreateDto.getCategoryNames().stream()
                .map(categoryName -> {
                    Category category = categoryRepository.findByName(categoryName)
                            .orElseThrow(() -> new CustomException(ExceptionCode.CATEGORY_NOT_FOUND));
                     ReviewCategory reviewCategory = new ReviewCategory(review, category);
                     review.addReviewCategory(category);
                     return reviewCategory;
                })
                .collect(Collectors.toSet());

        reviewRepository.save(review);

        cafeService.updatedCafeCategories(reviewCreateDto.getCafeId());
    }

    //해당 카페의 리뷰 조회
    public Page<ReviewResponseDto> findReviewByCafeId(Long cafeId, Pageable pageable) {
//        List<Review> cafes = reviewRepository.findAllByCafeId(cafeId);
//        return cafes.stream()
//                .map(ReviewResponseDto::new)
//                .collect(Collectors.toList());
        return reviewRepository.findAllByCafeId(cafeId, pageable);
    }

    //리뷰 수정
    @Transactional
    public Long updateReview(Long reviewId, ReviewUpdateDto reviewUpdateDto, UserDetails userDetails, String userId) {
            if (userDetails == null) {
                throw new CustomException(ExceptionCode.NO_LOGIN);
            }

            if (!userDetails.getUsername().equals(userId)) {
                throw new CustomException(ExceptionCode.ACCESS_DENIED);
            }

        Review existingReview = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ExceptionCode.REVIEW_NOT_FOUND));
        Set<ReviewCategory> updateCategories = reviewUpdateDto.getCategoryNames().stream()
                .map(categoryName -> {
                    Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new CustomException(ExceptionCode.CATEGORY_NOT_FOUND));
                    return new ReviewCategory(existingReview, category);
                })
                .collect(Collectors.toSet());
        Review updatedReview = new Review(existingReview.getId(), existingReview.getMember(), existingReview.getCafe(), reviewUpdateDto.getContent(), reviewUpdateDto.getScore(), updateCategories);
        Review review = reviewRepository.save(updatedReview);
        cafeService.updatedCafeCategories(existingReview.getCafe().getId());

        return review.getId();
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, UserDetails userDetails, String userId) {
        if (userDetails == null) {
            throw new CustomException(ExceptionCode.NO_LOGIN);
        }

        if (!userDetails.getUsername().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }
        reviewRepository.deleteById(reviewId);
    }


    public Review findById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
    }
}
