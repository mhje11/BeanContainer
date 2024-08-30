package com.beancontainer.domain.review.controller;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.review.dto.ReviewCreateDto;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.dto.ReviewUpdateDto;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.service.ReviewService;
import com.beancontainer.global.auth.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewRestController {

    private final ReviewService reviewService;
    private final MemberService memberService;
    private final com.beancontainer.global.service.AuthorizationService authorizationService;

    @PostMapping("/api/review/create")
    public ResponseEntity<String> createReview(@RequestBody ReviewCreateDto reviewCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        authorizationService.checkLogin(userDetails);
        Member member = memberService.findByUserId(userDetails.getUsername());
        reviewService.createReview(reviewCreateDto, userDetails.getUsername());

        return ResponseEntity.ok("리뷰 등록 완료");
    }

    @GetMapping("/api/reviewlist/{cafeId}")
    public ResponseEntity<Page<ReviewResponseDto>> findAllReviewList(@PathVariable("cafeId") Long cafeId, Pageable pageable) {
        Page<ReviewResponseDto> reviews = reviewService.findReviewByCafeId(cafeId, pageable);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping("/api/review/update/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewUpdateDto reviewUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewService.findById(reviewId);
        authorizationService.checkLogin(userDetails);
        authorizationService.checkReview(userDetails, review.getMember().getUserId());
        reviewService.updateReview(reviewId, reviewUpdateDto);
        return ResponseEntity.ok("리뷰 수정 완료");
    }

    @DeleteMapping("/api/review/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId")Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Review review = reviewService.findById(reviewId);
        authorizationService.checkLogin(userDetails);
        authorizationService.checkReview(userDetails, review.getMember().getUserId());
        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok("리뷰 삭제 완료");
    }

}
