package com.beancontainer.domain.cafe.repository;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.entity.QCafe;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomCafeRepositoryImpl implements CustomCafeRepository{

    private final EntityManager em;
    private JPAQueryFactory queryFactory;


    @Override
    public List<Cafe> findByCategories(Set<String> categories) {
        queryFactory = new JPAQueryFactory(em);
        QCafe cafe = QCafe.cafe;
        BooleanBuilder builder = new BooleanBuilder();

        // 각 카테고리에 대해 조건을 추가
        categories.forEach(category -> builder.and(cafe.topCategories.contains(category)));

        return queryFactory.selectFrom(cafe)
                .where(builder)
                .fetch();
    }

}
