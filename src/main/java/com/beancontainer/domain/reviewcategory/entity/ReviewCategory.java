package com.beancontainer.domain.reviewcategory.entity;

import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

    public ReviewCategory(Review review, Category category) {
        this.review = review;
        this.category = category;
    }
}
