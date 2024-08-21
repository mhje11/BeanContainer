package com.beancontainer.domain.map.repository;

import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.entity.Map;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CustomMapRepository {
    List<MapListResponseDto> findRandomMaps(int count);
}
