package com.beancontainer.domain.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MapCreateDto {
    private String mapName;
    private String username;
    private Set<String> kakaoIds = new HashSet<>();

    public MapCreateDto(String mapName, String username, Set<String> kakaoIds) {
        this.mapName = mapName;
        this.username = username;
        this.kakaoIds = kakaoIds;
    }
}
