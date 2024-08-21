package com.beancontainer.domain.review.controller;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.review.dto.ReviewCreateDto;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.dto.ReviewUpdateDto;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.domain.review.service.ReviewService;
import com.beancontainer.global.exception.AccessDeniedException;
import com.beancontainer.global.exception.UnAuthorizedException;
import com.beancontainer.global.exception.UserNotFoundException;
import com.beancontainer.global.service.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewRestController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    @PostMapping("/api/review/create")
    public ResponseEntity<String> createReview(@RequestBody ReviewCreateDto reviewCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnAuthorizedException("로그인 후 이용 가능합니다.");
        }
        Member member = memberService.findByUserId(userDetails.getUsername());
        reviewService.createReview(reviewCreateDto, userDetails.getUsername());

        return ResponseEntity.ok("리뷰 등록 완료");
    }

    @GetMapping("/api/reviewlist/{cafeId}")
    public ResponseEntity<List<ReviewResponseDto>> findAllReviewList(@PathVariable("cafeId") Long cafeId) {
         List<ReviewResponseDto> reviews = reviewService.findReviewByCafeId(cafeId);
         return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping("/api/review/update/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewUpdateDto reviewUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewService.findById(reviewId);
        if (userDetails == null) {
            throw new UnAuthorizedException("로그인 후 이용 가능합니다.");
        } if (!userDetails.getUsername().equals(review.getMember().getUserId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
        reviewService.updateReview(reviewId, reviewUpdateDto);
        return ResponseEntity.ok("리뷰 수정 완료");
    }

    @DeleteMapping("/api/review/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId")Long reviewId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Review review = reviewService.findById(reviewId);
        if (userDetails == null) {
            throw new UnAuthorizedException("로그인 후 이용 가능합니다.");
        } if (!userDetails.getUsername().equals(review.getMember().getUserId())) {
            log.info("userDetails {}", userDetails.getUsername());
            log.info("review {}", review.getMember().getUserId());
            throw new AccessDeniedException("권한이 없습니다.");
        }
        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok("리뷰 삭제 완료");
    }

}
