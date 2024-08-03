package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
public class MapCreateDto {
    private String mapName;
    private String username;
    private Set<Long> cafeIds;

    public MapCreateDto(String mapName, String username, Set<Long> cafeIds) {
        this.mapName = mapName;
        this.username = username;
        this.cafeIds = cafeIds;
    }
}
