package com.beancontainer.domain.review.controller;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.service.CafeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final CafeService cafeService;


    //카페 저장 안돼있으면 저장하고 이동하게 하는 로직
    @PostMapping("/review/{kakaoId}")
    @ResponseBody
    public ResponseEntity<?> getCafeOrElseSaveAndGet(@PathVariable("kakaoId") String kakaoId, @RequestBody CafeSaveDto cafeSaveDto) {
        try {
            log.info("Received request to check and save cafe with Cafe ID: {}", kakaoId);
            CafeResponseDto cafe = cafeService.getCafeByKakaoIdOrSave(kakaoId, cafeSaveDto);
            log.info("Returning cafe details: {}", cafe);
            return ResponseEntity.ok(cafe);
        } catch (Exception e) {
            log.error("Error while saving cafe: {}", e.getMessage());
            return ResponseEntity.status(500).body("{\"error\":\"" + e.getMessage() + "\"}");

        }

    }

    @GetMapping("review/{cafeId}")
    public String getReview(@PathVariable("cafeId") Long cafeId, Model model) {
        model.addAttribute("cafeId", cafeId);
        return "review/reviews";
    }
}
