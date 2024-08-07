package com.beancontainer.domain.reviewcategory.repository;

import com.beancontainer.domain.reviewcategory.entity.ReviewCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCategoryRepository extends JpaRepository<ReviewCategory, Long> {
}
