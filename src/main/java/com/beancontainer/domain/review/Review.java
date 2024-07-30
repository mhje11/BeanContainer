package com.beancontainer.domain.review;

import com.beancontainer.domain.cafe.Cafe;
import com.beancontainer.domain.category.Category;
import com.beancontainer.domain.member.Member;
import jakarta.persistence.*;

import java.util.List;

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

    private String uuid;

    private Double score;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Category.class)
    private List<Category> categories;


}
