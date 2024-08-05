package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MapListResponseDto {
    private String mapName;
    private String username;
    private Long mapId;

    public MapListResponseDto(String mapName, String username, Long mapId) {
        this.mapName = mapName;
        this.username = username;
        this.mapId = mapId;
    }
}
