package com.beancontainer.domain.comment.service;

import com.beancontainer.domain.comment.dto.CommentListResponseDto;
import com.beancontainer.domain.comment.dto.CommentRequestDto;
import com.beancontainer.domain.comment.entity.Comment;
import com.beancontainer.domain.comment.repository.CommentRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 작성
    public Long createComment(Long postId, CommentRequestDto commentRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findByNickname(commentRequestDto.getNickname());
        Comment comment = new Comment(post, member, commentRequestDto.getContent());
        Comment savedComment = commentRepository.save(comment);

        return savedComment.getId();
    }

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentListResponseDto> getAllComments(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        List<Comment> comments = commentRepository.findByPost(post);

        return comments.stream()
                .map(comment -> new CommentListResponseDto(
                        comment.getId(),
                        comment.getMember().getNickname(),
                        comment.getContent(),
                        comment.getCreatedAt()
                )).collect(Collectors.toList());
    }
}
