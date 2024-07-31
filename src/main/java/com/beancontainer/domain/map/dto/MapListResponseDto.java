package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapListResponseDto {
    private String mapName;
    private String username;

    public MapListResponseDto(String mapName, String username) {
        this.mapName = mapName;
        this.username = username;
    }
}
