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

}
