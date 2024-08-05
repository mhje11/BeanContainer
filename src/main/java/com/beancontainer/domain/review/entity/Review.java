package com.beancontainer.domain.review.entity;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.reviewcategory.entity.ReviewCategory;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "reviews")
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

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewCategory> reviewCategories = new HashSet<>();

}
