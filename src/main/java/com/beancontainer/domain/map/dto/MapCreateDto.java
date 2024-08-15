package com.beancontainer.domain.map.dto;

import com.beancontainer.domain.member.entity.Member;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "최소 하나의 카페가 추가 돼야 합니다.")
    private Set<String> kakaoIds = new HashSet<>();
    private Long memberId;

    public MapCreateDto(String mapName, Set<String> kakaoIds, Long memberId) {
        this.mapName = mapName;
        this.kakaoIds = kakaoIds;
        this.memberId = memberId;
    }
}
