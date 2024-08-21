package com.beancontainer.domain.comment.service;

import com.beancontainer.domain.comment.dto.CommentListResponseDto;
import com.beancontainer.domain.comment.dto.CommentRequestDto;
import com.beancontainer.domain.comment.entity.Comment;
import com.beancontainer.domain.comment.repository.CommentRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.global.exception.AccessDeniedException;
import com.beancontainer.global.exception.CommentNotFoundException;
import com.beancontainer.global.exception.HistoryNotFoundException;
import com.beancontainer.global.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 등록
    public Long createComment(Long postId, CommentRequestDto commentRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        List<Comment> comments = commentRepository.findByPost(post);

        return comments.stream()
                .map(comment -> new CommentListResponseDto(comment, (currentUserId != null) && comment.getMember().getId().equals(currentUserId)))
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    public void deleteComment(Long postId, Long commentId, String userId, boolean isAdmin) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new HistoryNotFoundException("해당 게시글에 속하지 않는 댓글입니다.");
        }

        if (!isAdmin && !comment.getMember().getUserId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // 댓글수 감소
        post.decrementCommentCount();
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    // 댓글 수정
    public void updateComment(Long postId, Long commentId, String content, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        if(!comment.getPost().getId().equals(post.getId())) {
            throw new HistoryNotFoundException("해당 게시글에 속하지 않는 댓글입니다.");
        }
        if(!comment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        comment.updateComment(content);
        commentRepository.save(comment);
    }
}

