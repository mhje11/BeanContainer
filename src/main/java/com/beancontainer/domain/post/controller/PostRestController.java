package com.beancontainer.domain.post.controller;

import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
    private final PostService postService;

    @PostMapping("/post/create")
    public ResponseEntity<String> createPost(@RequestBody PostRequestDto postRequestDto, Principal principal) {
        String nickname = "test";
        log.info(postRequestDto.getTitle());
        log.info(postRequestDto.getContent());
        log.info(postRequestDto.getUuid());
        Long postId = postService.createPost(postRequestDto, nickname);
        log.info("postId {}", postId);

        return ResponseEntity.ok("게시글생성 완료");
    }
}
