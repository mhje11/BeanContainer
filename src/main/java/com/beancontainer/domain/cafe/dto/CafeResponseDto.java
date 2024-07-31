package com.beancontainer.domain.cafe.dto;

import com.beancontainer.domain.cafe.entity.Cafe;
import lombok.Getter;

@Getter
public class CafeResponseDto {
    private String name;
    private String address;
    private String district;

    public CafeResponseDto(Cafe cafe) {
        this.name = cafe.getName();
        this.address = cafe.getAddress();
        this.district = cafe.getDistrict();
    }
}
