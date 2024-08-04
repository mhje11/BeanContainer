package com.beancontainer.domain.review.controller;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ReviewController {
    private final CafeService cafeService;



    //카페 저장 안돼있으면 저장하고 이동하게 하는 로직
    @PostMapping("/review/{cafeId}")
    public String getCafeOrElseSaveAndGet(@PathVariable("cafeId") Long cafeId, @RequestBody CafeSaveDto cafeSaveDto) {
        CafeResponseDto cafe = cafeService.getCafeByIdOrSave(cafeId, cafeSaveDto);
        return "redirect:/reviews/" + cafe.getId();
    }
}
