package com.beancontainer.domain.map.Controller;

import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.dto.MapUpdateDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import com.beancontainer.global.service.AuthorizationService;
import com.beancontainer.global.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final AuthorizationService authorizationService;

    @PostMapping("/api/mymap")
    public ResponseEntity<String> createMap(@Valid @RequestBody MapCreateDto mapCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED);
        }
        Member member = memberService.findByUserId(userDetails.getUsername());
        mapCreateDto.setMemberId(member.getId());

        Long mapId = mapService.createMap(mapCreateDto, member);
        return ResponseEntity.ok("지도 생성 성공 : ID : " + mapId);
    }


    @GetMapping("/api/mymap")
    public ResponseEntity<List<MapListResponseDto>> myMapList(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.findByUserId(userDetails.getUsername());
        List<MapListResponseDto> mapList = mapService.getMapList(member);
        return ResponseEntity.ok(mapList);
    }

    @GetMapping("/api/mymap/{mapId}")
    public ResponseEntity<MapDetailResponseDto> myMapDetail(@PathVariable("mapId") Long mapId) {
        MapDetailResponseDto mapDetail = mapService.getMapDetail(mapId);
        return ResponseEntity.ok(mapDetail);
    }

    @PutMapping("/api/mymap/update/{mapId}")
    public ResponseEntity<String> updateMap(@PathVariable("mapId") Long mapId, @RequestBody MapUpdateDto mapUpdateDto, @AuthenticationPrincipal UserDetails userDetails) {
        Map map = mapService.findById(mapId);

        authorizationService.checkLogin(userDetails);
        authorizationService.checkMap(userDetails, map.getMember().getUserId());

        mapUpdateDto.setMapId(mapId);
        mapService.updateMap(mapUpdateDto);
        return ResponseEntity.ok("지도 업데이트 성공 ID : " + mapId);
    }

    @DeleteMapping("/api/mymap/delete/{mapId}")
    public ResponseEntity<String> deleteMap(@PathVariable("mapId") Long mapId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map map = mapService.findById(mapId);

        authorizationService.checkLogin(userDetails);
        authorizationService.checkMap(userDetails, map.getMember().getUserId());

        mapService.deleteMap(mapId);
        return ResponseEntity.ok("지도 삭제 성공 ID : " + mapId);
    }

    @GetMapping("/api/randommap")
    public ResponseEntity<List<MapListResponseDto>> findRandomPublicMap() {
        List<MapListResponseDto> randomMap = mapService.findRandomPublicMap();
        return ResponseEntity.ok(randomMap);
    }
}
