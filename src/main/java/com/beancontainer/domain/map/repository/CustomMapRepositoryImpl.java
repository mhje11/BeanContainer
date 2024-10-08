package com.beancontainer.domain.map.repository;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.entity.QCafe;
import com.beancontainer.domain.category.entity.QCategory;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.entity.QMap;
import com.beancontainer.domain.mapcafe.entity.QMapCafe;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.QMember;
import com.beancontainer.domain.review.entity.QReview;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.beancontainer.domain.cafe.entity.QCafe.cafe;
import static com.beancontainer.domain.category.entity.QCategory.category;
import static com.beancontainer.domain.map.entity.QMap.map;
import static com.beancontainer.domain.mapcafe.entity.QMapCafe.mapCafe;
import static com.beancontainer.domain.member.entity.QMember.member;
import static com.beancontainer.domain.review.entity.QReview.review;

@Repository

public class CustomMapRepositoryImpl implements CustomMapRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public CustomMapRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<MapListResponseDto> findRandomMaps(int count) {
        return queryFactory
                .select(Projections.constructor(
                        MapListResponseDto.class,
                        map.mapName,
                        map.member.nickname,
                        map.id
                )).from(map)
                .join(map.member, member)
                .where(map.isPublic.eq(true)
                        .and(map.member.deletedAt.isNull()))
                .orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
                .limit(count)
                .fetch();
    }

    @Override
    public List<MapListResponseDto> getMapList(Member member) {
        QMember qMember = QMember.member;
        return queryFactory
                .select(Projections.constructor(MapListResponseDto.class,
                        map.mapName,
                        qMember.nickname,
                        map.id))
                .from(map)
                .join(map.member, qMember)
                .where(map.member.eq(member))
                .fetch();
    }



}
