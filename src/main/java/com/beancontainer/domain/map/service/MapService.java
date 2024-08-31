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
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {
    private final MapRepository mapRepository;
    private final MapCafeRepository mapCafeRepository;
    private final CafeRepository cafeRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public Long createMap(MapCreateDto mapCreateDto, Member member) {
        Map map = new Map(mapCreateDto.getMapName(), member, mapCreateDto.getIsPublic());
        mapRepository.save(map);
        Set<String> kakaoIds = mapCreateDto.getKakaoIds();

        for (String kakaoId : kakaoIds) {
            Cafe cafe = cafeRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.CAFE_NOT_FOUND));
            MapCafe mapCafe = new MapCafe(map, cafe);
            mapCafeRepository.save(mapCafe);
        }

        return map.getId();
    }

    @Transactional(readOnly = true)
    public List<MapListResponseDto> getMapList(Member member) {
//        return mapRepository.findAllByMember(member).stream()
//                .map(map -> new MapListResponseDto(map.getMapName(), map.getMember().getNickname(), map.getId()))
//                .collect(Collectors.toList());
        return mapRepository.getMapList(member);
    }

    @Transactional(readOnly = true)
    public MapDetailResponseDto getMapDetail(Long mapId) {
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new CustomException(ExceptionCode.MAP_NOT_FOUND));
        List<CafeResponseDto> cafes = mapCafeRepository.findAllByMapId(map.getId()).stream()
                .map(mapCafe -> {
                    Double averageScore = reviewRepository.calculateAverageScoreByCafeId(mapCafe.getCafe().getId());
                    return new CafeResponseDto(mapCafe.getCafe(), averageScore);
                })
                .collect(Collectors.toList());

        if (map.getMember().getDeletedAt() != null) {
            return new MapDetailResponseDto(map.getMapName(), "탈퇴한 회원", cafes, map.getIsPublic());
        }

        return new MapDetailResponseDto(map.getMapName(), map.getMember().getNickname(), cafes, map.getIsPublic());

    }

    @Transactional
    public Long updateMap(MapUpdateDto mapUpdateDto, UserDetails userDetails, String userId) {
        if (userDetails == null) {
            throw new CustomException(ExceptionCode.NO_LOGIN);
        }
        if (!userDetails.getUsername().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }
        Map map = mapRepository.findById(mapUpdateDto.getMapId()).orElseThrow(() -> new CustomException(ExceptionCode.MAP_NOT_FOUND));
        List<MapCafe> existingMapCafes = mapCafeRepository.findAllByMapId(map.getId());

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
                    .orElseThrow(() -> new CustomException(ExceptionCode.CAFE_NOT_FOUND));
            MapCafe mapCafe = new MapCafe(map, cafe);
            mapCafeRepository.save(mapCafe);
        });

        map.updateMap(mapUpdateDto.getMapName(), mapUpdateDto.getIsPublic());
        mapRepository.save(map);

        return map.getId();
    }

    @Transactional
    public void deleteMap(Long mapId, UserDetails userDetails, String userId) {
        if (userDetails == null) {
            throw new CustomException(ExceptionCode.NO_LOGIN);
        }
        if (!userDetails.getUsername().equals(userId)) {
            throw new CustomException(ExceptionCode.ACCESS_DENIED);
        }
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new CustomException(ExceptionCode.MAP_NOT_FOUND));

        List<MapCafe> mapCafes = mapCafeRepository.findAllByMapId(mapId);
        mapCafeRepository.deleteAll(mapCafes);

        mapRepository.delete(map);
    }

    @Transactional(readOnly = true)
    public Map findById(Long mapId) {
        return mapRepository.findById(mapId).orElseThrow(() -> new CustomException(ExceptionCode.MAP_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<MapListResponseDto> findRandomPublicMap() {
        return mapRepository.findRandomMaps(3);
    }
}
