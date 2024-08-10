package com.beancontainer.domain.post.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.domain.postimg.dto.PostImgSaveDto;
import com.beancontainer.domain.postimg.entity.PostImg;
import com.beancontainer.domain.postimg.service.PostImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostImgService postImgService;

    // 게시글 작성
    @Transactional
    public Long createPost(PostRequestDto postRequestDto, String nickname) throws IOException {

        Member member = memberRepository.findByNickname(nickname);

        Post post = new Post(
                member,
                postRequestDto.getTitle(),
                postRequestDto.getContent()
        );

        Post savedPost = postRepository.save(post); // 게시글 먼저 저장해서 id 생성

        // 이미지
        if (postRequestDto.getImages() != null && !postRequestDto.getImages().isEmpty()) {
            for(PostImgSaveDto image : postRequestDto.getImages()) {
                if(image.getImg().isEmpty()) continue;  // 이미지 없음 건너뜀

                image.setPostId(post.getId());  // 게시글 연결

                // S3에 이미지 저장 및 url 생성
                String imgUrl = postImgService.saveImage(image.getImg());
                String originalName = image.getImg().getOriginalFilename();
                String name = postImgService.getFileName(originalName);

                PostImg postImg = new PostImg(originalName, name, post);
                postImg.setPath(imgUrl);    // url 저장

                postImgService.save(postImg);
                post.getImages().add(postImg);  // post의 images 리스트에 추가
            }
        }
        return savedPost.getId();
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostListResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostListResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getMember().getNickname(),
                        // 댓글 수
                        // 좋아요 수
                        post.getCreatedAt(),
                        post.getUpdatedAt(),
                        post.getViews()
                        )).collect(Collectors.toList());
    }

    // 게시글 상세 보기
    @Transactional(readOnly = true)
    public PostDetailsResponseDto postDetails(Long postId, String userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        boolean isAuthor = post.getMember().getUserId().equals(userId);
        return new PostDetailsResponseDto(post, isAuthor);
    }

    // 게시글 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String userId, boolean isAdmin) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!isAdmin && !post.getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // S3에서 이미지 삭제
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            for (PostImg img : post.getImages()) {
                postImgService.deleteImage(img.getPath());
            }
        }
        postRepository.deleteById(postId);
    }

    // 게시글 수정
    @Transactional
    public PostDetailsResponseDto updatePost(Long postId, PostRequestDto postRequestDto) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(postRequestDto.getTitle(), postRequestDto.getContent());

        // 새로운 이미지가 있는 경우
        if (postRequestDto.getImages() != null && !postRequestDto.getImages().isEmpty()) {
            // 기존 이미지 삭제
            for(PostImg postImg : post.getImages()) {
                postImgService.deleteImage(postImg.getPath());
            }
            post.getImages().clear();

            // 새 이미지 저장
            for(PostImgSaveDto image : postRequestDto.getImages()) {
                if(image.getImg().isEmpty()) continue;  // 이미지 없음 건너뜀

                // S3에 이미지 저장 및 url 생성
                String imgUrl = postImgService.saveImage(image.getImg());
                String originalName = image.getImg().getOriginalFilename();
                String name = postImgService.getFileName(originalName);

                PostImg postImg = new PostImg(originalName, name, post);
                postImg.setPath(imgUrl);    // url 저장

                postImgService.save(postImg);
                post.getImages().add(postImg);  // post의 images 리스트에 추가
            }
        }

        Post updatedPost = postRepository.save(post);
        boolean isAuthor = post.getMember().getUserId().equals(post.getMember().getUserId());
        return new PostDetailsResponseDto(updatedPost, isAuthor);
    }
}
