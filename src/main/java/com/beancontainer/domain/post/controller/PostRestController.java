package com.beancontainer.domain.post.controller;

import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
    private final PostService postService;

    @PostMapping("/post/create")    // 게시글 작성
    public ResponseEntity<String> createPost(@RequestBody PostRequestDto postRequestDto, Principal principal) {
        String nickname = "test";
        log.info(postRequestDto.getTitle());
        log.info(postRequestDto.getContent());
//        log.info(postRequestDto.getUuid());
        Long postId = postService.createPost(postRequestDto, nickname);
        log.info("postId {}", postId);

        return ResponseEntity.ok("게시글생성 완료");
    }

    @GetMapping("/postList")    // 게시글 전체 조회
    public ResponseEntity<List<PostListResponseDto>> getAllPosts(Principal principal) {
        List<PostListResponseDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}
