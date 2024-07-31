package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CafeService {
    private final CafeRepository cafeRepository;

    //카페 db에 저장
    @Transactional
    public Long saveCafe(CafeSaveDto cafeSaveDto) {
        log.info("dto의 주소 : {}", cafeSaveDto.getAddress());
        Cafe cafe = cafeSaveDto.toEntity();
        log.info("엔티티의 주소 : {}", cafe.getAddress());
        cafeRepository.save(cafe);
        return cafe.getId();
    }

    public Cafe findByKakaoId(String kakaoId) {
        return cafeRepository.findByKakaoId(kakaoId);
    }

    //구별 카페 검색
    public List<CafeResponseDto> getCafesByDistrict(String district) {
        List<Cafe> cafes = cafeRepository.findAllByDistrict(district);
        return cafes.stream()
                .map(CafeResponseDto::new)
                .collect(Collectors.toList());
    }
}
