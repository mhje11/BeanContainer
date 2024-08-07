package com.beancontainer.domain.review.controller;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.review.dto.ReviewCreateDto;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.dto.ReviewUpdateDto;
import com.beancontainer.domain.review.service.ReviewService;
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

    @PostMapping("/api/review/create")
    public ResponseEntity<Long> createReview(@RequestBody ReviewCreateDto reviewCreateDto, @AuthenticationPrincipal UserDetails userDetails) {

        Member member = memberRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        reviewCreateDto.setUsername(member.getNickname());
        Long id = reviewService.createReview(reviewCreateDto);

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/api/reviewlist/{cafeId}")
    public ResponseEntity<List<ReviewResponseDto>> findAllReviewList(@PathVariable("cafeId") Long cafeId) {
         List<ReviewResponseDto> reviews = reviewService.findReviewByCafeId(cafeId);
         return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @PutMapping("/api/review/update/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewUpdateDto reviewUpdateDto) {
        reviewService.updateReview(reviewId, reviewUpdateDto);
        return new ResponseEntity<>("리뷰가 수정되었습니다.", HttpStatus.OK);
    }

    @DeleteMapping("/api/review/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId")Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>("리뷰가 삭제되었습니다.", HttpStatus.OK);
    }

}
