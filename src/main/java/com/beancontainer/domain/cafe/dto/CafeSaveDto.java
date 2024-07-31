package com.beancontainer.domain.cafe.dto;

import com.beancontainer.domain.cafe.entity.Cafe;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CafeSaveDto {
    private String kakaoId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String city;
    private String district;
    private String neighborhood;

    public CafeSaveDto(String kakaoId, String name, String address, double latitude, double longitude, String city, String district, String neighborhood) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.district = district;
        this.neighborhood = neighborhood;
    }

    public Cafe toEntity() {
        return new Cafe(kakaoId, name, address, latitude, longitude, city, district, neighborhood);
    }
}
