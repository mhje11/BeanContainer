package com.beancontainer.domain.post.service;

import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    @Transactional
    public void testCreatePost() {  // 게시글 작성 테스트
        PostRequestDto postRequestDto = new PostRequestDto("닉네임","제목", "내용", "uuid");

        PostResponseDto postResponseDto = postService.createPost(postRequestDto);

        assertNotNull(postResponseDto);
        assertEquals("닉네임", postResponseDto.getUsername());
        assertEquals("제목", postResponseDto.getTitle());
        assertEquals("내용", postResponseDto.getContent());
        assertEquals("uuid", postResponseDto.getUuid());
    }
}