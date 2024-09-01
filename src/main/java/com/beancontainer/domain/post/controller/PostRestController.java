package com.beancontainer.domain.post.controller;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.service.PostService;
import com.beancontainer.global.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
    private final PostService postService;
    private final MemberService memberService;

    @PostMapping("/post/create")    // 게시글 작성
    public ResponseEntity<Map<String, String>> createPost(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = memberService.findByUserId(userDetails.getUserId());

        Long postId = postService.createPost(postRequestDto, member);

        Map<String, String> response = new HashMap<>();
        response.put("message", "게시글생성 완료");
        response.put("postId", postId.toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/postList")    // 게시글 전체 조회
    public ResponseEntity<Page<PostListResponseDto>> getAllPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "15") int size,
                                                                 @RequestParam(defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<PostListResponseDto> posts = postService.getAllPosts(pageable, sortBy);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/postList/{postId}")   // 게시글 상세 정보
    public ResponseEntity<PostDetailsResponseDto> postDetails(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = (userDetails != null) ? userDetails.getUserId() : null;
        PostDetailsResponseDto post = postService.postDetails(postId, userId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/post/delete/{postId}") // 게시글 삭제
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        postService.deletePost(postId, userDetails.getUserId(), isAdmin);
        return ResponseEntity.ok("글 삭제가 완료되었습니다");
    }

    @PutMapping("/post/update/{postId}")    // 게시글 수정
    public ResponseEntity<PostDetailsResponseDto> updatePost(@PathVariable Long postId, @RequestPart("postRequestDto") PostRequestDto postRequestDto) throws IOException {
//        postRequestDto.setImages(images);

        PostDetailsResponseDto updatedPost = postService.updatePost(postId, postRequestDto);
        return ResponseEntity.ok(updatedPost);
    }
}