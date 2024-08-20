package com.beancontainer.domain.map.Controller;

import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.dto.MapUpdateDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.service.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MapRestController {
    private final MapService mapService;
    private final MemberRepository memberRepository;
    private final MapRepository mapRepository;

    @PostMapping("/api/mymap")
    public ResponseEntity<String> createMap(@Valid @RequestBody MapCreateDto mapCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다");
        }
        Member member = memberRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        mapCreateDto.setMemberId(member.getId());
        log.info("Received Map Data: {}", mapCreateDto.getKakaoIds()); // 로그 추가

        Long mapId = mapService.createMap(mapCreateDto, member);
        return ResponseEntity.ok("지도 생성 성공 : ID : " + mapId);
    }


    @GetMapping("/api/mymap")
    public ResponseEntity<List<MapListResponseDto>> myMapList(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
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
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new EntityNotFoundException("해당 지도를 찾을 수 없습니다."));

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다.");
        } else if (!userDetails.getUsername().equals(map.getMember().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        mapUpdateDto.setMapId(mapId);
        mapService.updateMap(mapUpdateDto);
        return ResponseEntity.ok("지도 업데이트 성공 ID : " + mapId);
    }

    @DeleteMapping("/api/mymap/delete/{mapId}")
    public ResponseEntity<String> deleteMap(@PathVariable("mapId") Long mapId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map map = mapRepository.findById(mapId).orElseThrow(() -> new EntityNotFoundException("해당 지도를 찾을 수 없습니다."));
        log.info("userDetails {}", userDetails.getUserId());
        log.info("mapUserId {}", map.getMember().getUserId());
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 후 이용 가능합니다.");
        } else if (!userDetails.getUserId().equals(map.getMember().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
        mapService.deleteMap(mapId);
        return ResponseEntity.ok("지도 삭제 성공 ID : " + mapId);
    }
}
