package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MapUpdateDto {
    private Long mapId;
    private String mapName;
    private List<Long> cafeIds;

    public MapUpdateDto(Long mapId, String mapName, List<Long> cafeIds) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.cafeIds = cafeIds;
    }
}
