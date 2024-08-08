package com.beancontainer.domain.comment.controller;

import com.beancontainer.domain.comment.dto.CommentRequestDto;
import com.beancontainer.domain.comment.service.CommentService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postlist")
public class CommentRestController {
    private final CommentService commentService;
    private final MemberRepository memberRepository;

    @PostMapping("/create/{postId}")
    public ResponseEntity<Map<String, String>> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        commentRequestDto.setNickname(member.getNickname());
        Long id = commentService.createComment(postId, commentRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글 생성 완료");
        response.put("commentId", id.toString());

        return ResponseEntity.ok(response); // json 형식으로 반환
    }
}
