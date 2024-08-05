package com.beancontainer.domain.reviewcategory.entity;

import com.beancontainer.domain.category.Category;
import com.beancontainer.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ReviewCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
