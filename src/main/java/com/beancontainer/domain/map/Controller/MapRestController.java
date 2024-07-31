package com.beancontainer.domain.map.Controller;

import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapRestController {
    private final MapService mapService;
    private final MapCafeRepository mapCafeRepository;

//    @PostMapping("/api/mymap")
//    public ResponseEntity<String> createMap(@RequestBody MapCreateDto mapCreateDto, @RequestBody List<CafeSaveDto> cafeSaveDtos) {
//        mapService.createMap(mapCreateDto, cafeSaveDtos);
//    }
//
//    @GetMapping("/api/mymap")
//    public ResponseEntity<MapListResponseDto> myMapList() {
//
//    }
//
//    @GetMapping("/api/mymap/{mapId}")
//    public ResponseEntity<MapDetailResponseDto> myMapDetail() {
//
//    }
}
