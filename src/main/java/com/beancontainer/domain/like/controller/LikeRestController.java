package com.beancontainer.domain.like.controller;

import com.beancontainer.domain.like.service.LikeService;
import com.beancontainer.global.auth.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeRestController {
    private final LikeService likeService;

    // 좋아요 추가
    @PostMapping("/{postId}")
    public ResponseEntity<String> addLike(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        likeService.addLike(postId, userId);

        return ResponseEntity.ok("좋아요가 추가되었습니다.");
    }

    // 좋아요 삭제
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<String> deleteLike(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        likeService.removeLike(postId, userDetails.getUserId());
        return ResponseEntity.ok("좋아요가 삭제되었습니다.");
    }

    // 게시글 좋아요수 조회
    @GetMapping("/{postId}/count")
    public ResponseEntity<Integer> countLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.countLikes(postId));
    }
}
