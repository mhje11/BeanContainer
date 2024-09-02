package com.beancontainer.domain.comment.service;

import com.beancontainer.domain.comment.dto.CommentListResponseDto;
import com.beancontainer.domain.comment.dto.CommentRequestDto;
import com.beancontainer.domain.comment.entity.Comment;
import com.beancontainer.domain.comment.repository.CommentRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    // 댓글 등록
    public Long createComment(Long postId, CommentRequestDto commentRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));
        Member member = memberRepository.findByUserId(commentRequestDto.getMemberLoginId()).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        Comment comment = new Comment(post, member, commentRequestDto.getContent());
        Comment savedComment = commentRepository.save(comment);

        // 댓글수 증가
        post.incrementCommentCount();
        postRepository.save(post);

        return savedComment.getId();
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentListResponseDto> getAllComments(Long postId, Long currentUserId) {
        return commentRepository.getAllComments(postId, currentUserId);
    }

    @Transactional
    // 댓글 삭제
    public void deleteComment(Long postId, Long commentId, String userId, boolean isAdmin) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CustomException(ExceptionCode.HISTORY_NOT_FOUND);
        }

        if (!isAdmin && !comment.getMember().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }

        // 댓글수 감소
        post.decrementCommentCount();
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    @Transactional
    // 댓글 수정
    public void updateComment(Long postId, Long commentId, String content, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        if(!comment.getPost().getId().equals(post.getId())) {
            throw new CustomException(ExceptionCode.HISTORY_NOT_FOUND);
        }

        if(!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }

        comment.updateComment(content);

        commentRepository.save(comment);
    }
}

