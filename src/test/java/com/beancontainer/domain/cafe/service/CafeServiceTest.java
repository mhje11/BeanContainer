package com.beancontainer.domain.cafe.service;

import com.beancontainer.domain.cafe.dto.CafeResponseDto;
import com.beancontainer.domain.cafe.dto.CafeSaveDto;
import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.category.repository.CategoryRepository;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.domain.reviewcategory.entity.ReviewCategory;
import com.beancontainer.domain.reviewcategory.repository.ReviewCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CafeServiceTest {
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private CafeService cafeService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MapRepository mapRepository;
    @Autowired
    private MapCafeRepository mapCafeRepository;
    @Autowired
    private ReviewCategoryRepository reviewCategoryRepository;

    @BeforeEach
    public void setUp() {
        Member member1 = new Member("John Doe", "johndoe", "john123", "password", "example@example.com", Role.MEMBER);
        Member member2 = new Member("Jane Smith", "janesmith", "jane123", "password", "example2@example.com", Role.MEMBER);
        memberRepository.save(member1);
        memberRepository.save(member2);

        Cafe cafe1 = new Cafe("kakao1", "Starbucks", "Seoul, Gangnam-gu", 37.5665, 126.9780, "Seoul", "Gangnam-gu");
        Cafe cafe2 = new Cafe("kakao2", "Cafe Bene", "Seoul, Mapo-gu", 37.5665, 126.9781, "Seoul", "Mapo-gu");
        Cafe cafe3 = new Cafe("kakao3", "Ediya Coffee", "Seoul, Jongno-gu", 37.5665, 126.9782, "Seoul", "Jongno-gu");
        cafeRepository.save(cafe1);
        cafeRepository.save(cafe2);
        cafeRepository.save(cafe3);

        Category category1 = categoryRepository.findByName("커피가 맛있는").get();
        Category category2 = categoryRepository.findByName("직원이 친절한").get();


        Review review1 = new Review(member1, cafe1, "조용하고 커피가 맛있어요", 4.5, new HashSet<>());

        review1.addReviewCategory(category1);
        review1.addReviewCategory(category2);

        reviewRepository.save(review1);

        cafeService.updatedCafeCategories(cafe1.getId());
        cafeService.updatedCafeCategories(cafe2.getId());
        cafeService.updatedCafeCategories(cafe3.getId());


        Map map1 = new Map("Seoul Cafes", member1, true);
        mapRepository.save(map1);

        MapCafe mapCafe1 = new MapCafe(map1, cafe1);
        mapCafeRepository.save(mapCafe1);


    }

    @Test
    void getCafeById() {
        // Given
        Cafe cafe1 = cafeRepository.findByKakaoId("kakao1").get();
        Long cafeId = cafe1.getId();

        long totalTime = 0;

        // When

        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();

            CafeResponseDto responseDto = cafeService.getCafeById(cafeId);

            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            totalTime += executionTime;

        }
        CafeResponseDto responseDto = cafeService.getCafeById(cafeId);

        // Then
        assertNotNull(responseDto);
        assertEquals("Starbucks", responseDto.getName());
        assertEquals("Seoul, Gangnam-gu", responseDto.getAddress());
        assertEquals(4.5, responseDto.getAverageScore());

        long averageExecutionTime = totalTime / 50;
        System.out.println("평균시간: " + averageExecutionTime + " ns");
    }

    @Test
    void getCafeByKakaoIdOrSave_whenCafeExists() {
        //given
        CafeSaveDto cafeSaveDto = new CafeSaveDto("kakao1", "Starbucks", "Seoul, Gangnam-gu", 37.5665, 126.9780, "Seoul", "Gangnam-gu");

        //when
        CafeResponseDto responseDto = cafeService.getCafeByKakaoIdOrSave("kakao1", cafeSaveDto);

        //then
        assertNotNull(responseDto);
        assertEquals("Starbucks", responseDto.getName());
        assertEquals("Seoul, Gangnam-gu", responseDto.getAddress());
        assertEquals(4.5, responseDto.getAverageScore());
    }

    @Test
    void getCafeByKakaoIdOrSave_whenCafeDoesNotExist() {
        //given
        CafeSaveDto cafeSaveDto = new CafeSaveDto("kakao4", "New Cafe", "Seoul, Jung-gu", 37.5663, 126.9779, "Seoul", "Jung-gu");

        //when
        CafeResponseDto responseDto = cafeService.getCafeByKakaoIdOrSave("kakao4", cafeSaveDto);

        //then
        assertNotNull(responseDto);
        assertEquals("New Cafe", responseDto.getName());
        assertEquals("Seoul, Jung-gu", responseDto.getAddress());
        assertEquals(0.0, responseDto.getAverageScore());
    }


    @Test
    void updatedCafeCategories() {
        //given
        Cafe cafe1 = cafeRepository.findByKakaoId("kakao1").get();
        Long cafeId = cafe1.getId();

        Category category1 = categoryRepository.findByName("조용한").get();
        Category category2 = categoryRepository.findByName("커피가 맛있는").get();
        Category category3 = categoryRepository.findByName("직원이 친절한").get();
        Category category4 = categoryRepository.findByName("와이파이").get();

        Category[] categories = {category1, category2, category3, category4};
        Member member1 = memberRepository.findByName("John Doe").get();  // 기존에 저장된 멤버 사용

        long totalTime = 0;

        //when
        for (int i = 0; i < 50; i++) {
            Review newReview = new Review(member1, cafe1, "리뷰 내용 " + i, 4.0 + (i % 5) * 0.1, new HashSet<>());
            newReview.addReviewCategory(categories[i % 4]);
            reviewRepository.save(newReview);

            long startTime = System.nanoTime();
            cafeService.updatedCafeCategories(cafeId);
            long endTime = System.nanoTime();
            totalTime += (endTime - startTime);

        }

        long averageTime = totalTime / 50;
        System.out.println("평균 시간 : " + averageTime + "ns");

        //then
        Cafe updatedCafe = cafeRepository.findById(cafeId).get();
        assertTrue(updatedCafe.getTopCategories().size() <= 3);  // topCategories는 최대 3개까지만 유지되어야 함
    }


    @Test
    void getCafesByCategories() {
        // Given
        Set<String> categories = new HashSet<>();
        categories.add("커피가 맛있는");
        categories.add("직원이 친절한");

        long totalTime = 0;

        // When
//        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();
        System.out.println("------------QueryStart-------------");

            List<CafeResponseDto> responseDtos = cafeService.getCafesByCategories(categories, false);
        System.out.println("------------QueryEnd-------------");

            long endTime = System.nanoTime();
            totalTime += endTime - startTime;
//        }

        System.out.println("평균 시간 : " + totalTime / 50 + "ns");

//        List<CafeResponseDto> responseDtos = cafeService.getCafesByCategories(categories, false);

        // Then
        assertNotNull(responseDtos);
        assertFalse(responseDtos.isEmpty());
        assertEquals(1, responseDtos.size());
        CafeResponseDto cafeResponseDto = responseDtos.get(0);
        assertEquals("Starbucks", cafeResponseDto.getName());
        assertEquals("Seoul, Gangnam-gu", cafeResponseDto.getAddress());
        assertEquals(4.5, cafeResponseDto.getAverageScore());
    }

}