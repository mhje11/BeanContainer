package com.beancontainer.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PostController {
    // 게시글 작성 폼
    @GetMapping("/post/create")
    public String postCreateForm() {
        return "postCreate";
    }

    // 게시글 수정 폼
    @GetMapping("/postlist/update/{postId}")
    public String postUpdateForm(@PathVariable Long postId, Model model) {
        return "postUpdate";
    }

    // 게시글 상세보기 폼
    @GetMapping("/postlist/{postId}")
    public String postDetailsForm(@PathVariable Long postId, Model model) {
        return "postDetails";
    }
}
