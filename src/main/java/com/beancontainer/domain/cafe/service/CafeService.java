package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeService {
    private final CafeRepository cafeRepository;

    @Transactional
    public Long saveCafe(CafeSaveDto cafeSaveDto) {
        Cafe cafe = cafeSaveDto.toEntity();
        cafeRepository.save(cafe);
        return cafe.getId();
    }

    public Cafe findByKakaoId(String kakaoId) {
        return cafeRepository.findByKakaoId(kakaoId);
    }

    public List<CafeResponseDto> getCafesByDistrict(String district) {
        List<Cafe> cafes = cafeRepository.findAllByDistrict(district);
        return cafes.stream()
                .map(CafeResponseDto::new)
                .collect(Collectors.toList());
    }
}
