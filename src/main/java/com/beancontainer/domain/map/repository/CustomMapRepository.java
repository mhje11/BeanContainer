package com.beancontainer.domain.map.repository;

import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CustomMapRepository {
    List<MapListResponseDto> findRandomMaps(int count);

    List<MapListResponseDto> getMapList(Member member);

}
