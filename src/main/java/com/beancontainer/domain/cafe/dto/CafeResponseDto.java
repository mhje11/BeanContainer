package com.beancontainer.domain.cafe.dto;

import com.beancontainer.domain.cafe.entity.Cafe;
import lombok.Getter;

@Getter
public class CafeResponseDto {
    private Long id;
    private String name;
    private String address;
    private String district;
    private Double longitude;
    private Double latitude;

    public CafeResponseDto(Cafe cafe) {
        this.id = cafe.getId();
        this.name = cafe.getName();
        this.address = cafe.getAddress();
        this.district = cafe.getDistrict();
        this.longitude = cafe.getLongitude();
        this.latitude = cafe.getLatitude();
    }
}
