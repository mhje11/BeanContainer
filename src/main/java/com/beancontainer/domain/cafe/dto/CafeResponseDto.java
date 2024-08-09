package com.beancontainer.domain.cafe.dto;

import com.beancontainer.domain.cafe.entity.Cafe;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CafeResponseDto {
    private Long id;
    private String kakaoId;
    private String name;
    private String address;
    private String district;
    private Double longitude;
    private Double latitude;
    private Set<String> topCategories = new HashSet<>();
    private Double averageScore;


    public CafeResponseDto(Cafe cafe, Double averageScore) {
        this.id = cafe.getId();
        this.name = cafe.getName();
        this.address = cafe.getAddress();
        this.district = cafe.getDistrict();
        this.longitude = cafe.getLongitude();
        this.latitude = cafe.getLatitude();
        this.topCategories = cafe.getTopCategories();
        this.kakaoId = cafe.getKakaoId();
        this.averageScore = averageScore != null ? averageScore : 0.0;
    }
}
