package com.beancontainer.domain.map.service;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MapServiceTest {

    @Autowired
    private MapRepository mapRepository;
    @Autowired
    MapCafeRepository mapCafeRepository;
    @Autowired
    MapService mapService;
    @Autowired
    CafeRepository cafeRepository;

    @Test
    void createMap() {
        Cafe cafe1 = new Cafe("kakaoId1", "Cafe1", "Address1", 37.5665, 126.9780, "Seoul", "District1", "Neighborhood1");
        Cafe cafe2 = new Cafe("kakaoId2", "Cafe2", "Address2", 37.5675, 126.9790, "Seoul", "District2", "Neighborhood2");

        cafeRepository.save(cafe1);
        cafeRepository.save(cafe2);

        MapCreateDto mapCreateDto = new MapCreateDto("map1", "user1", Arrays.asList(cafe1.getId(), cafe2.getId()));
        Long mapId = mapService.createMap(mapCreateDto);

        Map savedMap = mapRepository.findById(mapId).orElse(null);
        assertNotNull(savedMap);
        assertEquals("map1", savedMap.getMapName());
        assertEquals("user1", savedMap.getUsername());

        List<MapCafe> mapCafes = mapCafeRepository.findByMapId(mapId);
        assertEquals(2, mapCafes.size());
        assertEquals("Cafe1", mapCafes.get(0).getCafe().getName());
        assertEquals("Cafe2", mapCafes.get(1).getCafe().getName());
    }

    @Test
    void getMapList() {
        // Given
        Cafe cafe1 = new Cafe("kakaoId1", "Cafe1", "Address1", 37.5665, 126.9780, "Seoul", "District1", "Neighborhood1");
        Cafe cafe2 = new Cafe("kakaoId2", "Cafe2", "Address2", 37.5675, 126.9790, "Seoul", "District2", "Neighborhood2");

        cafeRepository.save(cafe1);
        cafeRepository.save(cafe2);

        mapService.createMap(new MapCreateDto("map1", "user1", Arrays.asList(cafe1.getId(), cafe2.getId())));
        mapService.createMap(new MapCreateDto("map2", "user2", Arrays.asList(cafe1.getId())));

        // When
        List<MapListResponseDto> mapList = mapService.getMapList();

        // Then
        assertNotNull(mapList);
        assertEquals(2, mapList.size());
        assertEquals("map1", mapList.get(0).getMapName());
        assertEquals("user1", mapList.get(0).getUsername());
        assertEquals("map2", mapList.get(1).getMapName());
        assertEquals("user2", mapList.get(1).getUsername());
    }

    @Test
    void getMapDetail() {
        // Given
        Cafe cafe1 = new Cafe("kakaoId1", "Cafe1", "Address1", 37.5665, 126.9780, "Seoul", "District1", "Neighborhood1");
        Cafe cafe2 = new Cafe("kakaoId2", "Cafe2", "Address2", 37.5675, 126.9790, "Seoul", "District2", "Neighborhood2");

        cafeRepository.save(cafe1);
        cafeRepository.save(cafe2);

        Long mapId = mapService.createMap(new MapCreateDto("map1", "user1", Arrays.asList(cafe1.getId(), cafe2.getId())));

        // When
        MapDetailResponseDto mapDetail = mapService.getMapDetail(mapId);

        // Then
        assertNotNull(mapDetail);
        assertEquals("map1", mapDetail.getMapName());
        assertEquals("user1", mapDetail.getUsername());
        assertNotNull(mapDetail.getCafes());
        assertEquals(2, mapDetail.getCafes().size());
        assertEquals("Cafe1", mapDetail.getCafes().get(0).getName());
        assertEquals("Cafe2", mapDetail.getCafes().get(1).getName());
    }
}