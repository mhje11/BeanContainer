package com.beancontainer.domain.review.entity;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.reviewcategory.entity.ReviewCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    private Double score;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewCategory> reviewCategories = new HashSet<>();

    public Review(Member member, Cafe cafe, String content, Double score, Set<ReviewCategory> reviewCategories) {
        this.member = member;
        this.cafe = cafe;
        this.content = content;
        this.score = score;
        this.reviewCategories = reviewCategories;
        this.createdAt = LocalDateTime.now();
    }

    public void addReviewCategory(Category category) {
        ReviewCategory reviewCategory = new ReviewCategory(this, category);
        this.reviewCategories.add(reviewCategory);
    }

    public Review(Long id, Member member, Cafe cafe, String content, Double score, Set<ReviewCategory> reviewCategories) {
        this.id = id;
        this.member = member;
        this.cafe = cafe;
        this.content = content;
        this.score = score;
        this.reviewCategories = reviewCategories;
    }
}
