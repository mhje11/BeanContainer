package com.beancontainer.domain.post.repository;

import com.beancontainer.domain.member.entity.QMember;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.entity.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.beancontainer.domain.member.entity.QMember.member;
import static com.beancontainer.domain.post.entity.QPost.post;

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
                        member.nickname,
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
