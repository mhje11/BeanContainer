package com.beancontainer.domain.post.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.dto.PostRequestDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        // 초기 데이터 설정
        Member member1 = new Member("name", "nickname", "id", "1234", Role.MEMBER);
        memberRepository.save(member1);
        Member member2 = new Member("test1", "test1", "test1", "1234", Role.MEMBER);
        memberRepository.save(member2);


        Post post1 = new Post(member1, "nick title 1", "test content 1");
        postRepository.save(post1);

        Post post2 = new Post(member1, "nick title 2", "nick content 2");
        postRepository.save(post2);

        Post post3 = new Post(member2, "test title 3", "test content 3");
        postRepository.save(post3);

        Post post4 = new Post(member2, "test title 4", "nick content 4");
        postRepository.save(post4);

    }

    @Test
    public void testCreatePost() {  // 게시글 작성 테스트
        Member member = memberRepository.findById(1L).get();
        PostRequestDto postRequestDto = new PostRequestDto("제목", "내용");


        Long postId = postService.createPost(postRequestDto, member.getNickname());
        Optional<Post> post = postRepository.findById(postId);

        assertEquals(postId, post.get().getId());
    }

    @Test
    public void testGetAllPosts() {
        List<PostListResponseDto> posts = postService.getAllPosts();

        assertEquals("nickname", posts.get(0).getNickname());
        assertEquals("nick title 1", posts.get(0).getTitle());

        assertEquals("nickname", posts.get(1).getNickname());
        assertEquals("nick title 2", posts.get(1).getTitle());

        assertEquals("test1", posts.get(2).getNickname());
        assertEquals("test title 3", posts.get(2).getTitle());

        assertEquals("test1", posts.get(3).getNickname());
        assertEquals("test title 4", posts.get(3).getTitle());
    }

}