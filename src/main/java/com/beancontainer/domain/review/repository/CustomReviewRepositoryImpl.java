package com.beancontainer.domain.review.repository;

import com.beancontainer.domain.cafe.entity.QCafe;
import com.beancontainer.domain.member.entity.QMember;
import com.beancontainer.domain.review.entity.QReview;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.reviewcategory.entity.QReviewCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.beancontainer.domain.cafe.entity.QCafe.cafe;
import static com.beancontainer.domain.member.entity.QMember.member;
import static com.beancontainer.domain.review.entity.QReview.review;
import static com.beancontainer.domain.reviewcategory.entity.QReviewCategory.reviewCategory;

public class CustomReviewRepositoryImpl implements CustomReviewRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomReviewRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Review> findAllByCafeId(Long cafeId) {
        return queryFactory
                .selectFrom(review)
                .join(review.member, member).fetchJoin()
                .join(review.cafe, cafe).fetchJoin()
                .join(review.reviewCategories, reviewCategory).fetchJoin()
                .where(review.cafe.id.eq(cafeId))
                .fetch();
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
