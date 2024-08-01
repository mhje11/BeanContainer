package com.beancontainer.domain.post.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;

    @Test
    public void testCreatePost() {  // 게시글 작성 테스트

        Member member = new Member("test", "test", "1234", "1234", Role.MEMBER, "asdf");
        memberRepository.save(member);
        PostRequestDto postRequestDto = new PostRequestDto("제목", "내용", "asdf");

        Long postId = postService.createPost(postRequestDto, member.getUsername());
        Optional<Post> post = postRepository.findById(postId);

        assertEquals(postId, post.get().getId());

    }
}