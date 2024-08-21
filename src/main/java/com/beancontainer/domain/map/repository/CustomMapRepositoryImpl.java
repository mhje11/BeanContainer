package com.beancontainer.domain.map.repository;

import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.member.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.beancontainer.domain.map.entity.QMap.map;
import static com.beancontainer.domain.member.entity.QMember.member;

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
                .where(map.isPublic.eq(true))
                .orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
                .limit(count)
                .fetch();
    }
}
