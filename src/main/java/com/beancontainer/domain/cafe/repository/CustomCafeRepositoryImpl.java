package com.beancontainer.domain.cafe.repository;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.review.entity.QReview;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.beancontainer.domain.cafe.entity.QCafe.cafe;
import static com.beancontainer.domain.review.entity.QReview.review;

@Repository
public class CustomCafeRepositoryImpl implements CustomCafeRepository{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomCafeRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Cafe> findByCategories(Set<String> categories, Boolean excludeBrands) {
        queryFactory = new JPAQueryFactory(em);
        BooleanBuilder builder = new BooleanBuilder();

        categories.forEach(category -> builder.and(cafe.topCategories.contains(category)));

        if (excludeBrands) {
            builder.and(cafe.isBrand.eq(false));
        }
        return queryFactory
                .selectFrom(cafe)
                .where(builder)
                .fetch();
    }

}
