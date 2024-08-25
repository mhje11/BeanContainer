package com.beancontainer.domain.mapcafe.repository;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.mapcafe.entity.QMapCafe;
import com.beancontainer.domain.review.entity.QReview;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.beancontainer.domain.cafe.entity.QCafe.cafe;
import static com.beancontainer.domain.mapcafe.entity.QMapCafe.mapCafe;
import static com.beancontainer.domain.review.entity.QReview.review;

public class CustomMapCafeRepositoryImpl implements CustomMapCafeRepository{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomMapCafeRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CafeResponseDto> findALlCafeByAverageScoreByMapId(Long mapId) {
        return queryFactory
                .select(Projections.constructor(
                        CafeResponseDto.class,
                        cafe.id,
                        cafe.kakaoId,
                        cafe.name,
                        cafe.address,
                        cafe.district,
                        cafe.longitude,
                        cafe.latitude,
                        cafe.topCategories,
                        review.score.avg().coalesce(0.0)
                ))
                .from(mapCafe)
                .join(mapCafe.cafe, cafe)
                .leftJoin(review).on(review.cafe.eq(cafe))
                .where(mapCafe.map.id.eq(mapId))
                .groupBy(cafe.id)
                .fetch();
    }
}
