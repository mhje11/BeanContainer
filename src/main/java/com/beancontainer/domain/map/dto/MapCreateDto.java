package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MapCreateDto {
    private String mapName;
    private String username;
    private List<Long> cafeIds;

    public MapCreateDto(String mapName, String username, List<Long> cafeIds) {
        this.mapName = mapName;
        this.username = username;
        this.cafeIds = cafeIds;
    }
}
