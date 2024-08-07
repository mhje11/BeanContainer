package com.beancontainer.domain.cafecategory;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CafeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
