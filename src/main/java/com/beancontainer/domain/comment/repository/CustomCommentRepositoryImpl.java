package com.beancontainer.domain.comment.repository;

import com.beancontainer.domain.comment.dto.CommentListResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.beancontainer.domain.comment.entity.QComment.comment;
import static com.beancontainer.domain.member.entity.QMember.member;

public class CustomCommentRepositoryImpl implements CustomCommentRepository{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomCommentRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CommentListResponseDto> getAllComments(Long postId, Long currentUserId) {
        BooleanExpression authorCheckExpression = (currentUserId != null)
                ? comment.member.id.eq(currentUserId)
                : comment.member.id.isNull();

        return queryFactory
                .select(Projections.constructor(
                        CommentListResponseDto.class,
                        comment.id,
                        new CaseBuilder()
                                .when(comment.member.deletedAt.isNull())
                                .then(comment.member.nickname)
                                .otherwise("탈퇴한 회원"),
                        comment.member.nickname,
                        comment.member.profileImageUrl,
                        comment.content,
                        comment.createdAt,
                        authorCheckExpression.as("authorCheck")
                ))
                .from(comment)
                .join(comment.member, member)
                .where(comment.post.id.eq(postId))
                .fetch();

    }
}
