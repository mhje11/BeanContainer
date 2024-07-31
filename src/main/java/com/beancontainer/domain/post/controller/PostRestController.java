package com.beancontainer.domain.post.controller;

import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostRestController {
    private final PostService postService;

    @PostMapping("/post/create")
    public ResponseEntity createPost(@RequestBody PostRequestDto postRequestDto) {
        PostResponseDto post = postService.createPost(postRequestDto);
        return ResponseEntity.ok(post);
    }
}
