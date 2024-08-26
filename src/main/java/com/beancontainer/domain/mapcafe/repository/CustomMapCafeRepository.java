package com.beancontainer.domain.mapcafe.repository;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;

import java.util.List;

public interface CustomMapCafeRepository {
    List<CafeResponseDto> findALlCafeByAverageScoreByMapId(Long mapId);
}
