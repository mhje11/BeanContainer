package com.beancontainer.domain.cafe.controller;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
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

    @PostMapping("/api/save/cafe")
    public ResponseEntity<Long> saveCafe(@RequestBody CafeSaveDto cafeSaveDto) {
        Long cafeId = cafeService.saveCafe(cafeSaveDto);
        return new ResponseEntity<>(cafeId, HttpStatus.CREATED);
    }

    @GetMapping("/api/cafelist")
    public ResponseEntity<List<CafeResponseDto>> savedCafeList(@RequestParam String district) {
        List<CafeResponseDto> cafes = cafeService.getCafesByDistrict(district);
        return ResponseEntity.ok(cafes);
    }
}
