package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CafeService {
    private final CafeRepository cafeRepository;
    private final ReviewRepository reviewRepository;

    //카페 저장
    @Transactional
    public Long saveCafe(CafeSaveDto cafeSaveDto) {
        Cafe cafe = cafeSaveDto.toEntity();
        cafeRepository.save(cafe);
        return cafe.getId();
    }


    //카페 정보 조회
    @Transactional(readOnly = true)
    public CafeResponseDto getCafeById(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new CustomException(ExceptionCode.CAFE_NOT_FOUND));
        Double average = reviewRepository.calculateAverageScoreByCafeId(cafeId);
        return new CafeResponseDto(cafe, average);
    }

    //카페가 존재하지 않을경우 저장하고 리뷰페이지로 이동하는 로직
    @Transactional
    public CafeResponseDto getCafeByKakaoIdOrSave(String kakaoId, CafeSaveDto cafeSaveDto) {
        return cafeRepository.findByKakaoId(kakaoId)
                .map(cafe -> {
                    Double averageScore = reviewRepository.calculateAverageScoreByCafeId(cafe.getId());
                    return new CafeResponseDto(cafe, averageScore);
                })
                .orElseGet(() -> {
                    Long savedCafeId = saveCafe(cafeSaveDto);
                    Cafe savedCafe = cafeRepository.findById(savedCafeId).orElseThrow(
                            () -> new CustomException(ExceptionCode.CAFE_NOT_FOUND)
                    );
                    return new CafeResponseDto(savedCafe, 0.0);
                });
    }



//    @Transactional
//    public void updatedCafeCategories(Long cafeId) {
//        Cafe cafe = cafeRepository.findById(cafeId)
//                .orElseThrow(() -> new CustomException(ExceptionCode.CAFE_NOT_FOUND));
//
//        Map<String, Long> categoryFrequency = reviewRepository.findAllByCafeId(cafeId).stream()
//                .flatMap(review -> review.getReviewCategories().stream())
//                .map(reviewCategory -> reviewCategory.getCategory().getName())
//                .collect(Collectors.groupingBy(categoryName -> categoryName, Collectors.counting()));
//
//        Set<String> topCategories = categoryFrequency.entrySet().stream()
//                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
//                .limit(3)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toSet());
//
//        cafe.getTopCategories().clear();
//        cafe.getTopCategories().addAll(topCategories);
//
//        cafeRepository.save(cafe);
//    }

    //카페 카테고리 업데이트
    @Transactional
    public void updatedCafeCategories(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CAFE_NOT_FOUND));

        List<Object[]> results = reviewRepository.findCategoryFrequenciesByCafeId(cafeId);
        Map<String, Long> categoryFrequency = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));

        Set<String> topCategories = categoryFrequency.entrySet().stream()
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        cafe.getTopCategories().clear();
        cafe.getTopCategories().addAll(topCategories);

        cafeRepository.save(cafe);
    }

    //동적 쿼리를 통한 카테고리로 카페 검색
    @Transactional(readOnly = true)
    public List<CafeResponseDto> getCafesByCategories(Set<String> categories, Boolean excludeBrands) {
        List<Cafe> cafes = cafeRepository.findByCategories(categories, excludeBrands);
        return cafes.stream()
                .map(cafe -> {
                    Double averageScore = reviewRepository.calculateAverageScoreByCafeId(cafe.getId());
                    return new CafeResponseDto(cafe, averageScore);
                })
                .collect(Collectors.toList());
    }


}
