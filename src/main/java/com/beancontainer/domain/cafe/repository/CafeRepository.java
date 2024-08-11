package com.beancontainer.domain.cafe.repository;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long>, CustomCafeRepository {
    Optional<Cafe> findByKakaoId(String kakaoId);
    List<Cafe> findAllByDistrict(String district);


}
