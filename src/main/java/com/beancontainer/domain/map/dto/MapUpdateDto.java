package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MapUpdateDto {
    private Long mapId;
    private String mapName;
    private Set<Long> cafeIds;

    public MapUpdateDto(Long mapId, String mapName, Set<Long> cafeIds) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.cafeIds = cafeIds;
    }
}
