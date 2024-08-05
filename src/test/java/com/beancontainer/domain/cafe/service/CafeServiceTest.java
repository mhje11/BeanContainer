package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    void getCafesByDistrict() {
        Cafe cafe = new Cafe("kakaoId123", "ExampleCafe", "ExAddress", 37.5665, 126.9780, "Seoul", "관악구", "사당동");
        Cafe cafe2 = new Cafe("kakaoId1232", "ExampleCafe2", "ExAddress2", 36.5665, 128.9780, "Seoul", "관악구", "사당동");
        Cafe cafe3 = new Cafe("kakaoId125", "ExampleCafe3", "ExAddress3", 35.5665, 127.9780, "Seoul", "강남구", "삼성동");

        cafeRepository.save(cafe);
        cafeRepository.save(cafe2);
        cafeRepository.save(cafe3);

        List<Cafe> cafes = cafeRepository.findAllByDistrict("관악구");

        assertThat(cafes).hasSize(2);
        assertThat(cafes).extracting("name")
                .containsExactlyInAnyOrder("ExampleCafe", "ExampleCafe2");
    }
}