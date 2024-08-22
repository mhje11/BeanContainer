package com.beancontainer.domain.post.service;

import com.beancontainer.domain.comment.repository.CommentRepository;
import com.beancontainer.domain.like.repository.LikeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.domain.postimg.entity.PostImg;
import com.beancontainer.domain.postimg.service.PostImgService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostImgService postImgService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    // 이미지 처리
    private void createImages(Post post, List<MultipartFile> images) throws IOException {
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (image.isEmpty()) continue;

                // S3에 이미지 저장 및 URL 생성
                String imgUrl = postImgService.saveImage(image);
                String originalName = image.getOriginalFilename();
                String name = postImgService.getFileName(originalName);

                PostImg postImg = new PostImg(originalName, name, post);
                postImg.setPath(imgUrl);  // URL 저장

                postImgService.save(postImg);
                post.getImages().add(postImg);  // Post의 images 리스트에 추가
            }
        }
    }

    // 이미지 삭제
    private void deleteExistImages(Post post) {
        for(PostImg postImg : post.getImages()) {
            postImgService.deleteImage(postImg.getPath());
        }
        post.getImages().clear();
    }

    // 게시글 작성
    @Transactional
    public Long createPost(PostRequestDto postRequestDto, Member member) throws IOException {

        Post post = new Post(
                member,
                postRequestDto.getTitle(),
                postRequestDto.getContent()
        );

        Post savedPost = postRepository.save(post);

        createImages(savedPost, postRequestDto.getImages());

        return savedPost.getId();
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostListResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> postList = postRepository.findAll(pageable);
        return postList.map(PostListResponseDto::new);
    }

    // 게시글 상세 보기
    @Transactional
    public PostDetailsResponseDto postDetails(Long postId, String userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        post.incrementViews();  // 조회수 증가
        postRepository.save(post);

        int likesCount = likeRepository.countByPostId(postId);  // 좋아요수

        boolean authorCheck = (userId != null) && post.getMember().getUserId().equals(userId);
        return new PostDetailsResponseDto(post, likesCount, authorCheck);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String userId, boolean isAdmin) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        if (!isAdmin && !post.getMember().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }

        // S3에서 이미지 삭제
        deleteExistImages(post);

        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    // 게시글 수정
    @Transactional
    public PostDetailsResponseDto updatePost(Long postId, PostRequestDto postRequestDto) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        post.update(postRequestDto.getTitle(), postRequestDto.getContent());

        // 새로운 이미지가 있는 경우
        if (postRequestDto.getImages() != null && !postRequestDto.getImages().isEmpty()) {
            // 기존 이미지 삭제
            deleteExistImages(post);
            // 새 이미지 저장
            createImages(post, postRequestDto.getImages());
        }

        Post updatedPost = postRepository.save(post);
        int likesCount = likeRepository.countByPostId(postId);  // 좋아요수
        boolean authorCheck = post.getMember().getUserId().equals(post.getMember().getUserId());
        return new PostDetailsResponseDto(updatedPost, likesCount, authorCheck);
    }
}