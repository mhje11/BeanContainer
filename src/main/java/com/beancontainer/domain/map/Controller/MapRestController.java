package com.beancontainer.domain.map.Controller;

import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapRestController {
    private final MapService mapService;
    private final MapCafeRepository mapCafeRepository;

    @PostMapping("/api/mymap")
    public ResponseEntity<String> createMap(@RequestBody MapCreateDto mapCreateDto) {
        Long mapId = mapService.createMap(mapCreateDto);
        return ResponseEntity.ok("지도 생성 성공 : ID : " + mapId);
    }

    @GetMapping("/api/mymap")
    public ResponseEntity<List<MapListResponseDto>> myMapList() {
        List<MapListResponseDto> mapList = mapService.getMapList();
        return ResponseEntity.ok(mapList);
    }

    @GetMapping("/api/mymap/{mapId}")
    public ResponseEntity<MapDetailResponseDto> myMapDetail(@PathVariable("mapId") Long mapId) {
        MapDetailResponseDto mapDetail = mapService.getMapDetail(mapId);
        return ResponseEntity.ok(mapDetail);
    }
}
