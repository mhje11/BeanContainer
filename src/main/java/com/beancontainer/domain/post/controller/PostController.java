package com.beancontainer.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PostController {
    // 게시글 전체 조회
    @GetMapping("/post/post-list")
    public String postList() {
        return "/post/post-list";
    }

    // 게시글 작성 폼
    @GetMapping("/post/create")
    public String postCreateForm() {
        return "post/post-create";
    }

    // 게시글 상세보기 폼
    @GetMapping("/postList/{postId}")
    public String postDetailsForm(@PathVariable Long postId) {
        return "post/post-details";
    }

    // 게시글 수정 폼
    @GetMapping("/postList/update/{postId}")
    public String postUpdateForm(@PathVariable Long postId) {
        return "post/post-update";
    }
}
