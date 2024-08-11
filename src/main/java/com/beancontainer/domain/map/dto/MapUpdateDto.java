package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MapUpdateDto {
    private Long mapId;
    private String mapName;
    private Set<String> kakaoIds;

    public MapUpdateDto(Long mapId, String mapName, Set<String> kakaoIds) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.kakaoIds = kakaoIds;
    }
}
