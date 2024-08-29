package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.cafe.entity.QCafe;
import com.beancontainer.domain.member.entity.QMember;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.entity.QReview;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.reviewcategory.entity.QReviewCategory;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import static com.beancontainer.domain.cafe.entity.QCafe.cafe;
import static com.beancontainer.domain.member.entity.QMember.member;
import static com.beancontainer.domain.review.entity.QReview.review;
import static com.beancontainer.domain.reviewcategory.entity.QReviewCategory.reviewCategory;

public class CustomReviewRepositoryImpl implements CustomReviewRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomReviewRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

//    @Override
//    public List<Review> findAllByCafeId(Long cafeId) {
//        return queryFactory
//                .selectFrom(review)
//                .join(review.member, member).fetchJoin()
//                .join(review.reviewCategories, reviewCategory).fetchJoin()
//                .where(review.cafe.id.eq(cafeId))
//                .fetch();
//    }

    @Override
    public Page<ReviewResponseDto> findAllByCafeId(Long cafeId, Pageable pageable) {
        List<Tuple> result = queryFactory
                .select(
                        review.id,
                        new CaseBuilder()
                                .when(review.member.deletedAt.isNull())
                                .then(review.member.nickname)
                                .otherwise("탈퇴한 회원").as("nickname"),
                        review.content,
                        review.score,
                        reviewCategory.category.name,
                        review.createdAt
                )
                .from(review)
                .join(review.member, member)
                .join(review.reviewCategories, reviewCategory)
                .where(review.cafe.id.eq(cafeId))
                .orderBy(review.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Map<Long, ReviewResponseDto> reviewMap = new LinkedHashMap<>();

        for (Tuple tuple : result) {
            Long reviewId = tuple.get(review.id);
            String nickname = tuple.get(Expressions.stringPath("nickname"));


            ReviewResponseDto dto = reviewMap.computeIfAbsent(reviewId, id -> new ReviewResponseDto(
                    id,
                    nickname,
                    tuple.get(review.content),
                    tuple.get(review.score),
                    new HashSet<>()
            ));
            dto.getCategoryNames().add(tuple.get(reviewCategory.category.name));
        }

        List<ReviewResponseDto> content = new ArrayList<>(reviewMap.values());

        Long total = queryFactory
                .select(review.count())
                .from(review)
                .where(review.cafe.id.eq(cafeId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


//    @Override
//    public Map<String, Long> findCategoryFrequenciesByCafeId(Long cafeId) {
//        return queryFactory
//                .select(reviewCategory.category.name, reviewCategory.category.name.count())
//                .from(reviewCategory)
//                .where(reviewCategory.review.cafe.id.eq(cafeId))
//                .groupBy(reviewCategory.category.name)
//                .orderBy(reviewCategory.category.name.count().desc())
//                .fetch()
//                .stream()
//                .collect(Collectors.toMap(
//                        tuple -> tuple.get(0, String.class),
//                        tuple -> tuple.get(1, Long.class)
//                ));
//    }
}
