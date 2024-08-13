package com.beancontainer.domain.post.controller;

import com.beancontainer.domain.admin.RequireAdmin;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.service.PostService;
import com.beancontainer.domain.postimg.dto.PostImgSaveDto;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
    private final PostService postService;
    private final MemberRepository memberRepository;

    @PostMapping("/post/create")    // 게시글 작성
    public ResponseEntity<Map<String, String>> createPost(@RequestParam("title") String title, @RequestParam("content") String content,
                                                          @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        PostRequestDto postRequestDto = new PostRequestDto();
        postRequestDto.setTitle(title);
        postRequestDto.setContent(content);

        if (images != null && !images.isEmpty()) {
            List<PostImgSaveDto> postImgSaveDtos = images.stream()
                    .map(image -> new PostImgSaveDto(image))
                    .collect(Collectors.toList());
            postRequestDto.setImages(postImgSaveDtos);
        }

        Member member = memberRepository.findByUserId(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        Long postId = postService.createPost(postRequestDto, member.getNickname());

        Map<String, String> response = new HashMap<>();
        response.put("message", "게시글생성 완료");
        response.put("postId", postId.toString());

        return ResponseEntity.ok(response); // json 형식으로 반환
    }

    @GetMapping("/postList")    // 게시글 전체 조회
    public ResponseEntity<Page<PostListResponseDto>> getAllPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "15") int size,
                                                                 @RequestParam(defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, sortBy));

        Page<PostListResponseDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/postList/{postId}")   // 게시글 상세 정보
    public ResponseEntity<PostDetailsResponseDto> postDetails(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = (userDetails != null) ? userDetails.getUserId() : null;
        PostDetailsResponseDto post = postService.postDetails(postId, userId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/post/delete/{postId}") // 게시글 삭제
    @RequireAdmin
    public ResponseEntity<String> deletePost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!postService.existsById(postId)) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        postService.deletePost(postId,  userDetails.getUserId(), isAdmin);
        return ResponseEntity.ok("글 삭제가 완료되었습니다");
    }

    @PutMapping("/post/update/{postId}")    // 게시글 수정
    public ResponseEntity<PostDetailsResponseDto> updatePost(@PathVariable Long postId, @RequestParam("title") String title, @RequestParam("content") String content,
                                                             @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException{

        PostRequestDto postRequestDto = new PostRequestDto();

        postRequestDto.setTitle(title);
        postRequestDto.setContent(content);

        if (images != null && !images.isEmpty()) {
            List<PostImgSaveDto> postImgSaveDtos = images.stream()
                    .map(image -> new PostImgSaveDto(image))
                    .collect(Collectors.toList());
            postRequestDto.setImages(postImgSaveDtos);
        }

        PostDetailsResponseDto updatedPost = postService.updatePost(postId, postRequestDto);
        return ResponseEntity.ok(updatedPost);
    }
}
