package com.beancontainer.domain.category.entity;

import com.beancontainer.domain.cafecategory.CafeCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CafeCategory> cafeCategories = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }
}
