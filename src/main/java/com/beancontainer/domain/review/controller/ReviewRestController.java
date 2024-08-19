package com.beancontainer.domain.review.controller;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.review.dto.ReviewCreateDto;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.dto.ReviewUpdateDto;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.domain.review.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewRestController {

    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    @PostMapping("/api/review/create")
    public ResponseEntity<String> createReview(@RequestBody ReviewCreateDto reviewCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다.");
        }
        Member member = memberRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        reviewService.createReview(reviewCreateDto, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("지도 생성 완료");
    }

    @GetMapping("/api/reviewlist/{cafeId}")
    public ResponseEntity<List<ReviewResponseDto>> findAllReviewList(@PathVariable("cafeId") Long cafeId) {
         List<ReviewResponseDto> reviews = reviewService.findReviewByCafeId(cafeId);
         return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping("/api/review/update/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewUpdateDto reviewUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다.");
        } if (userDetails.getUsername().equals(review.getMember().getUserId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
        }
        reviewService.updateReview(reviewId, reviewUpdateDto);
        return new ResponseEntity<>("리뷰가 수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("/api/review/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId")Long reviewId, @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다.");
        } if (userDetails.getUsername().equals(review.getMember().getUserId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
        }
        reviewService.deleteReview(reviewId);

        return new ResponseEntity<>("리뷰가 삭제되었습니다.", HttpStatus.OK);
    }

}
