package com.beancontainer.domain.map.Controller;

import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.dto.MapUpdateDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.auth.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MapRestController {
    private final MapService mapService;
    private final MemberService memberService;

    @PostMapping("/api/maps")
    public ResponseEntity<String> createMap(@Valid @RequestBody MapCreateDto mapCreateDto, @AuthenticationPrincipal UserDetails userDetails) {

        Member member = memberService.findByUserId(userDetails.getUsername());
        mapCreateDto.setMemberId(member.getId());

        Long mapId = mapService.createMap(mapCreateDto, member);
        return ResponseEntity.ok("지도 생성 성공 : ID : " + mapId);
    }


    @GetMapping("/api/maps/my")
    public ResponseEntity<List<MapListResponseDto>> myMapList(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByUserId(userDetails.getUsername());
        List<MapListResponseDto> mapList = mapService.getMapList(member);
        return ResponseEntity.ok(mapList);
    }

    @GetMapping("/api/maps/{mapId}")
    public ResponseEntity<MapDetailResponseDto> myMapDetail(@PathVariable("mapId") Long mapId) {
        MapDetailResponseDto mapDetail = mapService.getMapDetail(mapId);
        return ResponseEntity.ok(mapDetail);
    }

    @PutMapping("/api/maps/{mapId}/update")
    public ResponseEntity<String> updateMap(@PathVariable("mapId") Long mapId, @RequestBody MapUpdateDto mapUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        Map map = mapService.findById(mapId);

        mapUpdateDto.setMapId(mapId);
        mapService.updateMap(mapUpdateDto, userDetails, map.getMember().getUserId());
        return ResponseEntity.ok("지도 업데이트 성공 ID : " + mapId);
    }

    @DeleteMapping("/api/maps/{mapId}/delete")
    public ResponseEntity<String> deleteMap(@PathVariable("mapId") Long mapId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map map = mapService.findById(mapId);

        mapService.deleteMap(mapId, userDetails, map.getMember().getUserId());
        return ResponseEntity.ok("지도 삭제 성공 ID : " + mapId);
    }

    @GetMapping("/api/maps/random")
    public ResponseEntity<List<MapListResponseDto>> findRandomPublicMap() {
        List<MapListResponseDto> randomMap = mapService.findRandomPublicMap();
        return ResponseEntity.ok(randomMap);
    }
}
