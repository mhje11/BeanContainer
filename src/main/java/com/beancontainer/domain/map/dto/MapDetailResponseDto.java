package com.beancontainer.domain.map.dto;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class MapDetailResponseDto {

    private String mapName;
    private String username;
    private List<CafeResponseDto> cafes = new ArrayList<>();
    private Boolean isPublic;

    public MapDetailResponseDto(String mapName, String username, List<CafeResponseDto> cafes, Boolean isPublic) {
        this.mapName = mapName;
        this.username = username;
        this.cafes = cafes;
        this.isPublic = isPublic;
    }

    public MapDetailResponseDto(String mapName, String username, Boolean isPublic) {
        this.mapName = mapName;
        this.username = username;
        this.isPublic = isPublic;
    }
}
