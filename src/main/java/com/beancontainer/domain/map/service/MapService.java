package com.beancontainer.domain.map.service;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.dto.MapUpdateDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapService {
    private final MapRepository mapRepository;
    private final MapCafeRepository mapCafeRepository;
    private final CafeRepository cafeRepository;


    @Transactional
    public Long createMap(MapCreateDto mapCreateDto) {
        Map map = new Map(mapCreateDto.getMapName(), mapCreateDto.getUsername());
        mapRepository.save(map);

        for (Long cafeId : mapCreateDto.getCafeIds()) {
            Cafe cafe = cafeRepository.findById(cafeId)
                    .orElseThrow(() -> new RuntimeException("카페를 찾을 수 없습니다."));
            MapCafe mapCafe = new MapCafe(map, cafe);
            mapCafeRepository.save(mapCafe);
        }

        return map.getId();
    }

    //추후에 user 추가시 findAll -> findAllByUsername
    public List<MapListResponseDto> getMapList() {
        return mapRepository.findAll().stream()
                .map(map -> new MapListResponseDto(map.getMapName(), map.getUsername(), map.getId()))
                .collect(Collectors.toList());
    }

    //추후에 사용자 정의 예외 추가하기
    public MapDetailResponseDto getMapDetail(Long mapId) {
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new RuntimeException("해당 지도가 존재하지 않습니다."));
        List<CafeResponseDto> cafes = mapCafeRepository.findByMapId(map.getId()).stream()
                .map(mapCafe -> new CafeResponseDto(mapCafe.getCafe()))
                .collect(Collectors.toList());

        return new MapDetailResponseDto(map.getMapName(), map.getUsername(), cafes);
    }

    @Transactional
    public Long updateMap(MapUpdateDto mapUpdateDto) {
        Map map = mapRepository.findById(mapUpdateDto.getMapId()).orElseThrow(() -> new RuntimeException("해당 지도가 존재하지 않습니다."));

        List<MapCafe> existingMapCafes = mapCafeRepository.findByMapId(map.getId());
        mapCafeRepository.deleteAll(existingMapCafes);

        List<MapCafe> updateMapCafes = mapUpdateDto.getCafeIds().stream()
                .map(cafeId -> {
                    Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new RuntimeException("카페를 찾을 수 없습니다."));
                    MapCafe mapCafe = new MapCafe(map, cafe);
                    mapCafeRepository.save(mapCafe);
                    return mapCafe;
                })
                .collect(Collectors.toList());

        map.updateMap(mapUpdateDto.getMapName(), updateMapCafes);

        mapRepository.save(map);

        return map.getId();
    }

    @Transactional
    public void deleteMap(Long mapId) {
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new RuntimeException("해당 지도가 존재하지 않습니다."));

        List<MapCafe> mapCafes = mapCafeRepository.findByMapId(mapId);
        mapCafeRepository.deleteAll(mapCafes);

        mapRepository.delete(map);
    }
}
