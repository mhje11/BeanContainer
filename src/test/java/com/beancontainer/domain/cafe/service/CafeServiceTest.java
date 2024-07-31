package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CafeServiceTest {

    @Autowired
    CafeRepository cafeRepository;
    @Autowired
    CafeService cafeService;

    //    @BeforeEach
//    void setUp() {
//        Cafe cafe = new Cafe("kakaoId123", "ExampleCafe", "ExAddress", 37.5665, 126.9780, "Seoul", "관악구", "사당동");
//        CafeSaveDto cafeSaveDto = new CafeSaveDto("kakaoId123", "ExampleCafe", 37.5665, 126.9780, "ExAddress", "Seoul", "관악구", "사당동");
//    }
    @Test
    void saveCafe() {
        Cafe cafe = new Cafe("kakaoId123", "ExampleCafe", "ExAddress", 37.5665, 126.9780, "Seoul", "관악구", "사당동");
        cafeRepository.save(cafe);
        Cafe savedCafe = cafeRepository.findByKakaoId("kakaoId123");

        assertEquals("ExampleCafe", savedCafe.getName());
        assertEquals("ExAddress", savedCafe.getAddress());
        assertEquals(37.5665, savedCafe.getLatitude());
        assertEquals(126.9780, savedCafe.getLongitude());
        assertEquals("Seoul", savedCafe.getCity());
        assertEquals("관악구", savedCafe.getDistrict());
        assertEquals("사당동", savedCafe.getNeighborhood());

    }

    @Test
    void findByKakaoId() {
    }

    @Test
    void getCafesByDistrict() {
    }
}