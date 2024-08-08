package com.beancontainer.domain.cafe.dto;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafecategory.CafeCategory;
import com.beancontainer.domain.category.entity.Category;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CafeResponseDto {
    private Long id;
    private String name;
    private String address;
    private String district;
    private Double longitude;
    private Double latitude;
    private Set<String> topCategories;
    private Double averageScore;


    public CafeResponseDto(Cafe cafe, Double averageScore) {
        this.id = cafe.getId();
        this.name = cafe.getName();
        this.address = cafe.getAddress();
        this.district = cafe.getDistrict();
        this.longitude = cafe.getLongitude();
        this.latitude = cafe.getLatitude();
        this.topCategories = cafe.getCafeCategories().stream()
                .map(CafeCategory::getCategory)
                .map(Category::getName)
                .collect(Collectors.toSet());
        this.averageScore = averageScore != null ? averageScore : 0.0;
    }
}
