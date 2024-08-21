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
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.global.exception.CafeNotFoundException;
import com.beancontainer.global.exception.MapNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MapService {
    private final MapRepository mapRepository;
    private final MapCafeRepository mapCafeRepository;
    private final CafeRepository cafeRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public Long createMap(MapCreateDto mapCreateDto, Member member) {
        Map map = new Map(mapCreateDto.getMapName(), member, mapCreateDto.getIsPublic());
        log.info("isPublic {}", mapCreateDto.getIsPublic());
        mapRepository.save(map);
        Set<String> kakaoIds = mapCreateDto.getKakaoIds();

        for (String kakaoId : kakaoIds) {
            Cafe cafe = cafeRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new CafeNotFoundException("카페를 찾을 수 없습니다."));
            MapCafe mapCafe = new MapCafe(map, cafe);
            mapCafeRepository.save(mapCafe);
        }

        return map.getId();
    }

    public List<MapListResponseDto> getMapList(Member member) {
        return mapRepository.findAllByMember(member).stream()
                .map(map -> new MapListResponseDto(map.getMapName(), map.getMember().getNickname(), map.getId()))
                .collect(Collectors.toList());
    }

    public MapDetailResponseDto getMapDetail(Long mapId) {
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new MapNotFoundException("해당 지도가 존재하지 않습니다."));
        List<CafeResponseDto> cafes = mapCafeRepository.findByMapId(map.getId()).stream()
                .map(mapCafe -> {
                    Double averageScore = reviewRepository.calculateAverageScoreByCafeId(mapCafe.getCafe().getId());
                    return new CafeResponseDto(mapCafe.getCafe(), averageScore);
                })
                .collect(Collectors.toList());
        return new MapDetailResponseDto(map.getMapName(), map.getMember().getNickname(), cafes, map.getIsPublic());
    }

    @Transactional
    public Long updateMap(MapUpdateDto mapUpdateDto) {
        Map map = mapRepository.findById(mapUpdateDto.getMapId()).orElseThrow(() -> new MapNotFoundException("해당 지도가 존재하지 않습니다."));

        List<MapCafe> existingMapCafes = mapCafeRepository.findByMapId(map.getId());

        //업데이트 할 카페목록
        Set<String> newCafeIds = mapUpdateDto.getKakaoIds();

        //기존 카페 목록
        Set<String> existingCafeIds = existingMapCafes.stream()
                .map(mapCafe -> mapCafe.getCafe().getKakaoId())
                .collect(Collectors.toSet());


        //새로추가할 카페목록
        Set<String> addCafe = new HashSet<>(newCafeIds);
        addCafe.removeAll(existingCafeIds);

        //삭제할 카페 목록
        Set<String> removeCafe = new HashSet<>(existingCafeIds);
        removeCafe.removeAll(newCafeIds);

        existingMapCafes.stream()
                .filter(mapCafe -> removeCafe.contains(mapCafe.getCafe().getKakaoId()))
                .forEach(mapCafeRepository::delete);

        addCafe.forEach(kakaoId -> {
            Cafe cafe = cafeRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new CafeNotFoundException("카페를 찾을 수 없습니다."));
            MapCafe mapCafe = new MapCafe(map, cafe);
            mapCafeRepository.save(mapCafe);
        });

        map.updateMap(mapUpdateDto.getMapName(), mapUpdateDto.getIsPublic());
        mapRepository.save(map);

        return map.getId();
    }

    @Transactional
    public void deleteMap(Long mapId) {
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new MapNotFoundException("해당 지도가 존재하지 않습니다."));

        List<MapCafe> mapCafes = mapCafeRepository.findByMapId(mapId);
        mapCafeRepository.deleteAll(mapCafes);

        mapRepository.delete(map);
    }

    public Map findById(Long mapId) {
        return mapRepository.findById(mapId).orElseThrow(() -> new MapNotFoundException("해당 지도를 찾을 수 없습니다."));
    }
}
