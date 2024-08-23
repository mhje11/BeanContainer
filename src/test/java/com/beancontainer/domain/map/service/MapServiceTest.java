package com.beancontainer.domain.map.service;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.cafe.service.CafeService;
import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.category.repository.CategoryRepository;
import com.beancontainer.domain.map.dto.MapCreateDto;
import com.beancontainer.domain.map.dto.MapDetailResponseDto;
import com.beancontainer.domain.map.dto.MapListResponseDto;
import com.beancontainer.domain.map.dto.MapUpdateDto;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.domain.reviewcategory.repository.ReviewCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MapServiceTest {
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
    private MapService mapService;
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
    void createMap() {
        //Given
        Member member = memberRepository.findByName("John Doe").get();
        Set<String> kakaoIds = new HashSet<>();
        kakaoIds.add("kakao1");
        kakaoIds.add("kakao2");

        MapCreateDto mapCreateDto = new MapCreateDto("My Cafe Map", kakaoIds, member.getId(), true);

        //when
        Long mapId = mapService.createMap(mapCreateDto, member);

        //then
        assertNotNull(mapId);
        Map createdMap = mapRepository.findById(mapId).orElse(null);
        assertNotNull(createdMap);
        assertEquals("My Cafe Map", createdMap.getMapName());
        assertTrue(createdMap.getIsPublic());

        List<MapCafe> mapCafes = mapCafeRepository.findAllByMapId(mapId);
        assertEquals(2, mapCafes.size());

        List<String> savedKakaoIds = mapCafes.stream()
                .map(mapCafe -> mapCafe.getCafe().getKakaoId())
                .collect(Collectors.toList());
        assertTrue(savedKakaoIds.contains("kakao1"));
        assertTrue(savedKakaoIds.contains("kakao2"));

    }

    @Test
    void getMapList() {
        //given
        Member member = memberRepository.findByName("John Doe").get();

        long totalTime = 0;

        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();

            //when
            List<MapListResponseDto> mapList = mapService.getMapList(member);

            long endTime = System.nanoTime();

            totalTime += endTime - startTime;
        }



        List<MapListResponseDto> mapList = mapService.getMapList(member);

        //then
        assertNotNull(mapList);
        assertEquals(1, mapList.size());

        assertTrue(mapList.stream().anyMatch(dto -> dto.getMapName().equals("Seoul Cafes")));

        System.out.println("평균시간 : " + (totalTime / 50) + "ns");

    }

    @Test
    void getMapDetail() {
        //given
        Member member = memberRepository.findByName("John Doe").get();
        Map map = new Map("Gangnam Cafes", member, true);
        mapRepository.save(map);

        Cafe cafe1 = cafeRepository.findByKakaoId("kakao1").get();
        Cafe cafe2 = cafeRepository.findByKakaoId("kakao2").get();
        MapCafe mapCafe1 = new MapCafe(map, cafe1);
        MapCafe mapCafe2 = new MapCafe(map, cafe2);
        mapCafeRepository.save(mapCafe1);
        mapCafeRepository.save(mapCafe2);

        long totalTime = 0;
        //when
        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();

            MapDetailResponseDto mapDetailResponseDto = mapService.getMapDetail(map.getId());

            long endTime = System.nanoTime();

            totalTime += endTime - startTime;
        }

        MapDetailResponseDto mapDetailResponseDto = mapService.getMapDetail(map.getId());

        //then
        assertNotNull(mapDetailResponseDto);
        assertEquals("Gangnam Cafes", mapDetailResponseDto.getMapName());
        assertEquals("johndoe", mapDetailResponseDto.getUsername());
        assertTrue(mapDetailResponseDto.getCafes().stream().anyMatch(cafe -> cafe.getName().equals("Starbucks")));
        assertTrue(mapDetailResponseDto.getCafes().stream().anyMatch(cafe -> cafe.getName().equals("Cafe Bene")));
        assertEquals(2, mapDetailResponseDto.getCafes().size());
        assertTrue(mapDetailResponseDto.getIsPublic());
        System.out.println("평균 시간 : " + (totalTime / 50) + "ns");
    }

    @Test
    void updateMap() {
        //given
        Member member = memberRepository.findByName("John Doe").get();
        Map map = new Map("Original Map Name", member, true);
        mapRepository.save(map);

        Cafe cafe1 = cafeRepository.findByKakaoId("kakao1").get();
        Cafe cafe2 = cafeRepository.findByKakaoId("kakao2").get();
        Cafe cafe3 = cafeRepository.findByKakaoId("kakao3").get();

        MapCafe mapCafe1 = new MapCafe(map, cafe1);
        MapCafe mapCafe2 = new MapCafe(map, cafe2);
        mapCafeRepository.save(mapCafe1);
        mapCafeRepository.save(mapCafe2);

        Set<String> updatedCafeIds = new HashSet<>(Arrays.asList("kakao1", "kakao3"));
        MapUpdateDto mapUpdateDto = new MapUpdateDto(map.getId(), "Updated Map Name", updatedCafeIds, false);

        long totalTime = 0;

        for (int i = 0; i < 50; i++) {
            //when
            long startTime = System.nanoTime();

            Long updatedMapId = mapService.updateMap(mapUpdateDto);

            long endTime = System.nanoTime();

            totalTime += endTime - startTime;
        }


        //then
        Map updatedMap = mapRepository.findById(map.getId()).get();
        assertEquals("Updated Map Name", updatedMap.getMapName());

        List<MapCafe> updatedMapCafes = mapCafeRepository.findAllByMapId(updatedMap.getId());
        Set<String> updatedCafeIdsInMap = updatedMapCafes.stream()
                .map(mapCafe -> mapCafe.getCafe().getKakaoId())
                .collect(Collectors.toSet());

        assertTrue(updatedCafeIdsInMap.contains("kakao1"));
        assertTrue(updatedCafeIdsInMap.contains("kakao3"));
        assertFalse(updatedCafeIdsInMap.contains("kakao2"));
        assertEquals(2, updatedCafeIdsInMap.size());

        System.out.println("평균 시간 : " + (totalTime / 50) + "ns");

    }

    @Test
    void deleteMap() {
        //given
        Member member = memberRepository.findByName("John Doe").get();
        Map map = new Map("Map to be Deleted", member, true);
        mapRepository.save(map);

        Cafe cafe1 = cafeRepository.findByKakaoId("kakao1").get();
        Cafe cafe2 = cafeRepository.findByKakaoId("kakao2").get();

        MapCafe mapCafe1 = new MapCafe(map, cafe1);
        MapCafe mapCafe2 = new MapCafe(map, cafe2);
        mapCafeRepository.save(mapCafe1);
        mapCafeRepository.save(mapCafe2);

        assertTrue(mapRepository.existsById(map.getId()));
        assertTrue(mapCafeRepository.existsById(mapCafe1.getId()));
        assertTrue(mapCafeRepository.existsById(mapCafe2.getId()));

        //when
        mapService.deleteMap(map.getId());

        //then
        assertFalse(mapRepository.existsById(map.getId()));
        assertFalse(mapCafeRepository.existsById(mapCafe1.getId()));
        assertFalse(mapCafeRepository.existsById(mapCafe2.getId()));
    }


    @Test
    void findRandomPublicMap() {
        //given
        Member member1 = memberRepository.findByName("John Doe").get();
        Member member2 = memberRepository.findByName("Jane Smith").get();

        Map map1 = new Map("Public Map 1", member1, true);
        Map map2 = new Map("Public Map 2", member2, true);
        Map map3 = new Map("Public Map 3", member1, true);
        Map map4 = new Map("Public Map 4", member2, true);
        Map map5 = new Map("Public Map 5", member1, true);

        mapRepository.save(map1);
        mapRepository.save(map2);
        mapRepository.save(map3);
        mapRepository.save(map4);
        mapRepository.save(map5);

        long totalTime = 0;

        for (int i = 0; i < 50; i++) {
            //when
            long startTime = System.nanoTime();

            List<MapListResponseDto> randomMaps = mapService.findRandomPublicMap();

            long endTime = System.nanoTime();
            totalTime += endTime - startTime;
        }


        //then
        List<MapListResponseDto> randomMaps = mapService.findRandomPublicMap();
        assertNotNull(randomMaps);
        assertEquals(3, randomMaps.size());
        assertTrue(randomMaps.stream().allMatch(map -> map.getMapName().startsWith("Public")));

        System.out.println("평균 시간 : " + (totalTime / 50) + "ns");
    }
}