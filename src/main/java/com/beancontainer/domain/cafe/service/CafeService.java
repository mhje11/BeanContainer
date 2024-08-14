package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CafeService {
    private final CafeRepository cafeRepository;
    private final ReviewRepository reviewRepository;

    //카페 db에 저장
    @Transactional
    public Long saveCafe(CafeSaveDto cafeSaveDto) {
        log.info("dto의 주소 : {}", cafeSaveDto.getAddress());
        Cafe cafe = cafeSaveDto.toEntity();
        log.info("엔티티의 주소 : {}", cafe.getAddress());
        cafeRepository.save(cafe);
        return cafe.getId();
    }



    //구별 카페 검색
    public List<CafeResponseDto> getCafesByDistrict(String district) {
        List<Cafe> cafes = cafeRepository.findAllByDistrict(district);
        return cafes.stream()
                .map(cafe -> {
                    Double averageScore = reviewRepository.calculateAverageScoreByCafeId(cafe.getId());
                    return new CafeResponseDto(cafe, averageScore);
                })
                .collect(Collectors.toList());
    }

    public CafeResponseDto getCafeById(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("해당 카페를 찾을 수 없습니다."));
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
                            () -> new RuntimeException("카페 저장 후 조회에 실패")
                    );
                    return new CafeResponseDto(savedCafe, 0.0);
                });
    }



    @Transactional
    public void updatedCafeCategories(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카페를 찾을 수 없습니다."));

        // 리뷰에서 카테고리 빈도를 계산
        Map<String, Long> categoryFrequency = reviewRepository.findAllByCafeId(cafeId).stream()
                .flatMap(review -> review.getReviewCategories().stream())
                .map(reviewCategory -> reviewCategory.getCategory().getName())
                .collect(Collectors.groupingBy(categoryName -> categoryName, Collectors.counting()));

        // 상위 3개의 카테고리를 선택
        Set<String> topCategories = categoryFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Cafe의 topCategories 업데이트
        cafe.getTopCategories().clear();
        cafe.getTopCategories().addAll(topCategories);

        cafeRepository.save(cafe);
    }


    public List<CafeResponseDto> getCafesByCategories(Set<String> categories) {
        List<Cafe> cafes = cafeRepository.findByCategories(categories);
        return cafes.stream()
                .map(cafe -> {
                    Double averageScore = reviewRepository.calculateAverageScoreByCafeId(cafe.getId());
                    return new CafeResponseDto(cafe, averageScore);
                })
                .collect(Collectors.toList());
    }


}
