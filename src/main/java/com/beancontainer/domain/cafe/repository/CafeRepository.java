package com.beancontainer.domain.cafe.repository;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    Cafe findByKakaoId(String kakaoId);
    List<Cafe> findAllByDistrict(String district);
}
