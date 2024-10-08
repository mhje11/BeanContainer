package com.beancontainer.domain.post.repository;

import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.beancontainer.domain.like.entity.QLikes.likes;
import static com.beancontainer.domain.member.entity.QMember.member;
import static com.beancontainer.domain.post.entity.QPost.post;
import static com.beancontainer.domain.postimg.entity.QPostImg.postImg;

public class CustomPostRepositoryImpl implements CustomPostRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostListResponseDto> getAllPosts(Pageable pageable, String sortBy) {
        List<PostListResponseDto> results = queryFactory
                .select(Projections.constructor(
                        PostListResponseDto.class,
                        post.id,
                        post.title,
                        new CaseBuilder()
                                .when(post.member.deletedAt.isNull())
                                .then(post.member.nickname)
                                .otherwise("탈퇴한 회원"),
                        post.member.profileImageUrl,
                        post.commentCount,
                        post.likeCount,
                        post.createdAt,
                        post.updatedAt,
                        post.views
                ))
                .from(post)
                .join(post.member, member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(sortBy))
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .fetchOne();
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public PostDetailsResponseDto findPostDetailsById(Long postId, String userId) {
        BooleanExpression authorCheckExpression = (userId != null)
                ? post.member.userId.eq(userId)
                : post.member.userId.isNull();

        Long likesCount = queryFactory
                .select(likes.count())
                .from(likes)
                .where(likes.post.id.eq(postId))
                .fetchOne();

        List<Tuple> results = queryFactory
                .select(
                        post.id,
                        post.title,
                        new CaseBuilder()
                                .when(post.member.deletedAt.isNull())
                                .then(post.member.nickname)
                                .otherwise("탈퇴한 회원").as("nickname"),
                        post.member.profileImageUrl,
                        post.createdAt,
                        post.updatedAt,
                        post.views,
                        post.content,
                        postImg.path,
                        postImg.id,
                        authorCheckExpression
                )
                .from(post)
                .leftJoin(post.member, member)
                .leftJoin(post.images, postImg)
                .where(post.id.eq(postId))
                .groupBy(post.id, postImg.id)
                .fetch();

        if (results.isEmpty()) {
            return null;
        }

        PostDetailsResponseDto dto = null;

        for (Tuple result : results) {
            if (dto == null) {
                String nickname = result.get(Expressions.stringPath("nickname"));
                dto = new PostDetailsResponseDto(
                        result.get(post.id),
                        result.get(post.title),
                        nickname,
                        result.get(post.member.profileImageUrl),
                        result.get(post.createdAt),
                        result.get(post.updatedAt),
                        result.get(post.views).intValue(),
                        likesCount.intValue(),
                        result.get(post.content),
                        result.get(authorCheckExpression)
                );
            }
        }

        return dto;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        if (sortBy.equalsIgnoreCase("createdAt")) {
            return post.createdAt.desc();
        } else if (sortBy.equalsIgnoreCase("title")) {
            return post.title.desc();
        } else if (sortBy.equalsIgnoreCase("views")) {
            return post.views.desc();
        } else if (sortBy.equalsIgnoreCase("likeCount")) {
            return post.likeCount.desc();
        } else if (sortBy.equalsIgnoreCase("commentCount")) {
            return post.commentCount.desc();
        }
        else {
            return post.createdAt.desc();
        }
    }
}
