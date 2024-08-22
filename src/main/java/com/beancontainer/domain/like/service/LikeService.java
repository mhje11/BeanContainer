package com.beancontainer.domain.like.service;

import com.beancontainer.domain.like.entity.Likes;
import com.beancontainer.domain.like.repository.LikeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.global.exception.HistoryNotFoundException;
import com.beancontainer.global.exception.LikeExistException;
import com.beancontainer.global.exception.PostNotFoundException;
import com.beancontainer.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 좋아요 추가
    public void addLike(Long postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if (likeRepository.findByPostAndMember(post, member).isPresent()) {
            throw new LikeExistException("이미 좋아요를 누른 상태입니다.");
        }

        Likes like = new Likes(post, member);
        likeRepository.save(like);

        post.incrementLikeCount();
        postRepository.save(post);
    }

    // 좋아요 삭제
    public void removeLike(Long postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException("해당 사용자를 찾을 수 없습니다."));
        Likes like = likeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new HistoryNotFoundException("좋아요를 누르지 않았습니다."));

        likeRepository.delete(like);

        post.decrementLikeCount();
        postRepository.save(post);
    }

    // 좋아요수 조회
    @Transactional(readOnly = true)
    public int countLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        return likeRepository.countByPostId(postId);
    }
}
