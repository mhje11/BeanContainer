package com.beancontainer.domain.admin;

import com.beancontainer.domain.comment.service.CommentService;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostListResponseDto>> getAllPosts() {
        List<PostListResponseDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId, null, true);  // null for userId, true for isAdmin
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }
}
