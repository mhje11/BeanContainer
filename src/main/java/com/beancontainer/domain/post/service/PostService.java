package com.beancontainer.domain.post.service;

import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
//        Member testMember = new Member(1L, "test", "nick", "test", "1234", null, null);

        Post post = new Post(
                postRequestDto.getUsername(),
                postRequestDto.getTitle(),
                postRequestDto.getContent(),
                postRequestDto.getUuid()
        );

        Post savedPost = postRepository.save(post);

        return new PostResponseDto(
                savedPost.getId(),
                savedPost.getUsername(),
                savedPost.getTitle(),
                savedPost.getContent(),
//                savedPost.getViews(),
//                savedPost.getCreatedAt(),
//                savedPost.getUpdatedAt(),
                savedPost.getUuid()
        );
    }
}
