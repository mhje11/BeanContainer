package com.beancontainer.domain.map.Controller;

import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.dto.MapUpdateDto;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapRestController {
    private final MapService mapService;

    @PostMapping("/api/mymap")
    public ResponseEntity<String> createMap(@RequestBody MapCreateDto mapCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
//        mapCreateDto.setUsername(userDetails.getUsername());
        Long mapId = mapService.createMap(mapCreateDto);
        return ResponseEntity.ok("지도 생성 성공 : ID : " + mapId);
    }


    //추후에 자신의 map만 뜨도록
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

    @PutMapping("/api/mymap/update/{mapId}")
    public ResponseEntity<String> updateMap(@PathVariable("mapId") Long mapId, @RequestBody MapUpdateDto mapUpdateDto) {
        mapUpdateDto.setMapId(mapId);
        mapService.updateMap(mapUpdateDto);
        return ResponseEntity.ok("지도 업데이트 성공 ID : " + mapId);
    }

    @DeleteMapping("/api/mymap/delete/{mapId}")
    public ResponseEntity<String> deleteMap(@PathVariable("mapId") Long mapId) {
        mapService.deleteMap(mapId);
        return ResponseEntity.ok("지도 삭제 성공 ID : " + mapId);
    }
}
