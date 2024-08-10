package com.beancontainer.domain.cafe.repository;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.entity.QCafe;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
        QCafe cafe = QCafe.cafe;
        return queryFactory.selectFrom(cafe)
                .where(cafe.topCategories.any().in(categories))
                .fetch();
    }
}
