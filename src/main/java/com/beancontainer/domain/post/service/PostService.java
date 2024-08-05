package com.beancontainer.domain.post.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글 작성
    @Transactional
    public Long createPost(PostRequestDto postRequestDto, String nickname) {
        Member member = memberRepository.findByNickname(nickname);
        log.info(member.getNickname());

        Post post = new Post(
                member,
                postRequestDto.getTitle(),
                postRequestDto.getContent()
        );

        // 이미지..

        log.info(post.toString());

        Post savedPost = postRepository.save(post);
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
    public PostDetailsResponseDto postDetails(Long postId) {
        Post post = postRepository.findById(postId).get();
        return new PostDetailsResponseDto(
                post.getId(),
                post.getTitle(),
                post.getMember().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViews(),
                post.getContent()
        );
    }

    // 게시글 존재 여부 확인
    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }


    // 게시글 수정
    @Transactional
    public PostDetailsResponseDto updatePost(Long postId, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(postRequestDto.getTitle(), postRequestDto.getContent());

        Post updatedPost = postRepository.save(post);

        return new PostDetailsResponseDto(updatedPost);
    }

}
