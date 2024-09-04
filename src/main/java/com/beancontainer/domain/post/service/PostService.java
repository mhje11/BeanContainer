package com.beancontainer.domain.post.service;

import com.beancontainer.domain.comment.repository.CommentRepository;
import com.beancontainer.domain.like.repository.LikeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.domain.postimg.dto.PostImgResponseDto;
import com.beancontainer.domain.postimg.entity.PostImg;
import com.beancontainer.domain.postimg.repository.PostImgRepository;
import com.beancontainer.domain.postimg.service.PostImgService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostImgService postImgService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostImgRepository postImgRepository;

    // 이미지 처리
    private void createImages(Post post, List<PostImgResponseDto> imageInfos) {
        if (imageInfos != null && !imageInfos.isEmpty()) {
            if(imageInfos.size() > 5) {
                throw new CustomException(ExceptionCode.MAX_IMAGES_COUNT);
            }
            for (PostImgResponseDto imageInfo : imageInfos) {
                PostImg postImg = new PostImg(imageInfo.getOriginName(), imageInfo.getName(), imageInfo.getUrl(), post);
                post.getImages().add(postImg);  // Post의 images 리스트에 추가
                postImgService.save(postImg);
            }
        }
    }

    // 이미지 삭제
    private void deleteImagesFromS3(Post post) {
        for(PostImg postImg : post.getImages()) {
            postImgService.deleteImage(postImg.getPath());
        }
        post.getImages().clear();
    }

    // 게시글 작성
    @Transactional
    public Long createPost(PostRequestDto postRequestDto, Member member) {
        Post post = new Post(
                member,
                postRequestDto.getTitle(),
                postRequestDto.getContent()
        );

        Post savedPost = postRepository.save(post);

        createImages(savedPost, postRequestDto.getImageInfos());


        if (postRequestDto.getUnusedImageUrls() != null) {
            for (String unusedImageUrl : postRequestDto.getUnusedImageUrls()) {
                postImgService.deleteImage(unusedImageUrl);
            }
        }

        return savedPost.getId();
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostListResponseDto> getAllPosts(Pageable pageable, String sortBy) {
        return postRepository.getAllPosts(pageable, sortBy);
    }

    // 게시글 상세 보기
    @Transactional
    public PostDetailsResponseDto postDetails(Long postId, String userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        post.incrementViews();  // 조회수 증가
        postRepository.save(post);

        return postRepository.findPostDetailsById(postId, userId);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String userId, boolean isAdmin) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        if (!isAdmin && !post.getMember().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }

        // DB에서 이미지 삭제
        deleteImagesFromS3(post);

        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    // 게시글 수정
    @Transactional
    public PostDetailsResponseDto updatePost(Long postId, PostRequestDto postRequestDto) {
        // 기존 게시글 찾기
        Post existingPost = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 수정 제목, 내용 게시글 update
        existingPost.update(postRequestDto.getTitle(), postRequestDto.getContent());

        // 기존 이미지 조회
        List<PostImg> existingPostImages = postImgRepository.findByPostId(postId);

        // 수정한 이미지 url
        List<String> updatedImageUrls = postRequestDto.getUsedImageUrls();
        List<String> allImageUrls = postRequestDto.getImageInfos() != null
                ? postRequestDto.getImageInfos().stream().map(PostImgResponseDto::getUrl).collect(Collectors.toList())
                : List.of();

        // 사용되지 않는 이미지 처리
        log.info("수정된 사항에서의 이미지 url::::::::" + updatedImageUrls);
        List<PostImg> unusedImages = existingPostImages.stream()
                        .filter(image -> !updatedImageUrls.contains(image.getPath()))
                                .collect(Collectors.toList());

        if(!unusedImages.isEmpty()) {
            log.info("unusedImages:::::::::" + unusedImages);
            for(PostImg image : unusedImages) {
                postImgService.deleteImage(image.getPath());
            }
            postImgRepository.deleteAll(unusedImages);
        }


        // 새로운 이미지 추가
        List<PostImgResponseDto> newImageInfos = postRequestDto.getImageInfos() != null
                ? postRequestDto.getImageInfos().stream()
                .filter(imageInfo -> !existingPostImages.stream()
                        .anyMatch(image -> image.getPath().equals(imageInfo.getUrl())))
                .collect(Collectors.toList())
                : List.of();

        // 이미지 개수 제한
        int totalImageCount = existingPostImages.size() - unusedImages.size() + newImageInfos.size();
        if (totalImageCount > 5) {
            throw new CustomException(ExceptionCode.MAX_IMAGES_COUNT);
        }

        createImages(existingPost, newImageInfos);

        Post updatedPost = postRepository.save(existingPost);

        int likesCount = likeRepository.countByPostId(postId);  // 좋아요수
        boolean authorCheck = existingPost.getMember().getUserId().equals(existingPost.getMember().getUserId());
        return new PostDetailsResponseDto(updatedPost, likesCount, authorCheck);
    }
}