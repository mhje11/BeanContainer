package com.beancontainer.domain.like.service;

import com.beancontainer.domain.like.entity.Likes;
import com.beancontainer.domain.like.repository.LikeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    // 좋아요 추가
    public void addLike(Long postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        if (likeRepository.findByPostAndMember(post, member).isPresent()) {
            throw new CustomException(ExceptionCode.LIKE_ALREADY_EXISTS);
        }

        Likes like = new Likes(post, member);
        likeRepository.save(like);

        post.incrementLikeCount();
        postRepository.save(post);
    }

    @Transactional
    // 좋아요 삭제
    public void removeLike(Long postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        Likes like = likeRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new CustomException(ExceptionCode.HISTORY_NOT_FOUND));

        likeRepository.delete(like);

        post.decrementLikeCount();

        postRepository.save(post);
    }

    // 좋아요수 조회
    @Transactional(readOnly = true)
    public int countLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        return likeRepository.countByPostId(postId);
    }
}
