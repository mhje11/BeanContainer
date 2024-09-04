package com.beancontainer.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    // 게시글 전체 조회
    @GetMapping("/list")
    public String postList() {
        return "post/post-list";
    }

    // 게시글 작성 폼
    @GetMapping("/create")
    public String postCreateForm() {
        return "post/post-create";
    }

    // 게시글 상세보기 폼
    @GetMapping("/{postId}")
    public String postDetailsForm(@PathVariable Long postId) {
        return "post/post-details";
    }

    // 게시글 수정 폼
    @GetMapping("/{postId}/update")
    public String postUpdateForm(@PathVariable Long postId) {
        return "post/post-update";
    }
}
