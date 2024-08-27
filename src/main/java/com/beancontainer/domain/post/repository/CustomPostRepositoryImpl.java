package com.beancontainer.domain.post.repository;

import com.beancontainer.domain.like.entity.QLikes;
import com.beancontainer.domain.member.entity.QMember;
import com.beancontainer.domain.post.dto.PostDetailsResponseDto;
import com.beancontainer.domain.post.dto.PostListResponseDto;
import com.beancontainer.domain.post.entity.Post;
import com.beancontainer.domain.post.entity.QPost;
import com.beancontainer.domain.postimg.entity.QPostImg;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
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
                        post.member.nickname,
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

        List<String> imageUrls = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();

        for (Tuple result : results) {
            if (dto == null) {
                dto = new PostDetailsResponseDto(
                        result.get(post.id),
                        result.get(post.title),
                        result.get(post.member.nickname),
                        result.get(post.member.profileImageUrl),
                        result.get(post.createdAt),
                        result.get(post.updatedAt),
                        result.get(post.views).intValue(),
                        likesCount.intValue(),
                        result.get(post.content),
                        imageUrls,
                        imageIds,
                        result.get(authorCheckExpression)
                );
            }
            imageUrls.add(result.get(postImg.path));
            imageIds.add(result.get(postImg.id));
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
