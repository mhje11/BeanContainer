package com.beancontainer.domain.cafe.repository;

import com.beancontainer.domain.cafe.entity.Cafe;

import java.util.List;
import java.util.Set;

public interface CustomCafeRepository {
    List<Cafe> findByCategories(Set<String> categories, Boolean isBrand);
}
