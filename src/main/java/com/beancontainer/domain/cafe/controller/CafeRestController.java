package com.beancontainer.domain.cafe.controller;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.cafe.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CafeRestController {
    private final CafeService cafeService;
    private final CafeRepository cafeRepository;

    @PostMapping("/api/save/cafe")
    public ResponseEntity<String> saveCafe(@RequestBody CafeSaveDto cafeSaveDto) {
        Long cafeId = cafeService.saveCafe(cafeSaveDto);
        return new ResponseEntity<>("카페 저장완료 " + cafeId, HttpStatus.CREATED);
    }

    @GetMapping("/api/cafelist")
    public ResponseEntity<List<CafeResponseDto>> savedCafeListByDistrict(@RequestParam String district) {
        List<CafeResponseDto> cafes = cafeService.getCafesByDistrict(district);
        return ResponseEntity.ok(cafes);
    }

    @GetMapping("/api/cafe/{cafeId}")
    public ResponseEntity<CafeResponseDto> getCafe(@PathVariable("cafeId")Long cafeId) {
        CafeResponseDto cafe = cafeService.getCafeById(cafeId);
        return new ResponseEntity<>(cafe, HttpStatus.OK);
    }


}
