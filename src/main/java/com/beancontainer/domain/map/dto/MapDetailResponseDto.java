package com.beancontainer.domain.map.dto;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MapDetailResponseDto {

    private String mapName;
    private String username;
    private List<CafeResponseDto> cafes;

    public MapDetailResponseDto(String mapName, String username, List<CafeResponseDto> cafes) {
        this.mapName = mapName;
        this.username = username;
        this.cafes = cafes;
    }
}
