package com.beancontainer.domain.comment.controller;

import com.beancontainer.domain.admin.RequireAdmin;
import com.beancontainer.domain.comment.dto.CommentListResponseDto;
import com.beancontainer.domain.comment.dto.CommentRequestDto;
import com.beancontainer.domain.comment.service.CommentService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.service.PostService;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postlist")
public class CommentRestController {
    private final CommentService commentService;
    private final MemberRepository memberRepository;

    // 댓글 등록
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

    // 댓글 조회
    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<CommentListResponseDto>> getAllComments(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        List<CommentListResponseDto> comments = commentService.getAllComments(postId, member.getId());
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/delete/{commentId}")
    @RequireAdmin
    public ResponseEntity<String> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        Member member = memberRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        commentService.deleteComment(postId, commentId, userDetails.getUserId(), isAdmin);
        return ResponseEntity.ok("댓글 삭제가 완료되었습니다.");
    }

    @PutMapping("/{postId}/update/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        commentService.updateComment(postId, commentId, commentRequestDto.getContent(), member);
        return ResponseEntity.ok("댓글 수정이 완료되었습니다.");
    }
}
