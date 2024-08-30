package com.beancontainer.domain.review.service;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.cafe.service.CafeService;
import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.category.repository.CategoryRepository;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.map.service.MapService;
import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.review.dto.ReviewCreateDto;
import com.beancontainer.domain.review.dto.ReviewResponseDto;
import com.beancontainer.domain.review.dto.ReviewUpdateDto;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.domain.reviewcategory.repository.ReviewCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReviewServiceTest {

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
    @Autowired
    private ReviewService reviewService;


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
    void createReview() {
        //given
        String userLoginId = "john123";
        Member member = memberRepository.findByName("John Doe").get();
        Cafe cafe = cafeRepository.findByKakaoId("kakao1").get();
        Set<String> categories = new HashSet<>();
        categories.add("커피가 맛있는");
        categories.add("직원이 친절한");
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto("훌륭한 커피와 친절한 직원들!", 4.0, cafe.getId(), categories);

        //when
//        reviewService.createReview(reviewCreateDto, member.getUserId());

        //then
//        List<Review> reviews = reviewRepository.findAllByCafeId(cafe.getId());
//        Review savedReview = reviews.get(1);

//        assertNotNull(savedReview);
//        assertEquals("훌륭한 커피와 친절한 직원들!", savedReview.getContent());
//        assertEquals(4.0, savedReview.getScore());
//
//        Set<String> savedCategoryNames = savedReview.getReviewCategories().stream()
//                .map(rc -> rc.getCategory().getName())
//                .collect(Collectors.toSet());

//        assertEquals(2, savedCategoryNames.size());
//        assertTrue(savedCategoryNames.contains("커피가 맛있는"));
//        assertTrue(savedCategoryNames.contains("직원이 친절한"));
//
//        Cafe updatedCafe = cafeRepository.findById(cafe.getId()).get();
//        Set<String> topCategories = updatedCafe.getTopCategories();
//        assertTrue(topCategories.contains("커피가 맛있는"));
//        assertTrue(topCategories.contains("직원이 친절한"));

    }

    @Test
    void findReviewByCafeId() {
        //given
        Cafe cafe = cafeRepository.findByKakaoId("kakao1").get();
        Member member = memberRepository.findByName("Jane Smith").get();

        Review review2 = new Review(member, cafe, "넓고 조용한 분위기", 4.0, new HashSet<>());
        Category category = categoryRepository.findByName("커피가 맛있는").get();
        review2.addReviewCategory(category);
        reviewRepository.save(review2);

        long totalTime = 0;


        //when
        for (int i = 0; i < 50; i++) {
            long startTime = System.nanoTime();

            List<ReviewResponseDto> reviewResponseDtos = reviewService.findReviewByCafeId(cafe.getId());

            long endTime = System.nanoTime();

            totalTime += endTime - startTime;
        }


        List<ReviewResponseDto> reviewResponseDtos = reviewService.findReviewByCafeId(cafe.getId());

        //then
        assertNotNull(reviewResponseDtos);
        assertEquals(2, reviewResponseDtos.size());

        assertTrue(reviewResponseDtos.stream().anyMatch(review -> review.getContent().equals("조용하고 커피가 맛있어요")));
        assertTrue(reviewResponseDtos.stream().anyMatch(review -> review.getContent().equals("넓고 조용한 분위기")));

        System.out.println("평균 시간 : " + (totalTime / 50) + "ns");
    }

    @Test
    void updateReview() {
        //given
        Member member = memberRepository.findByName("John Doe").get();
        Cafe cafe = cafeRepository.findByKakaoId("kakao1").get();

        List<Review> reviews = reviewRepository.findAllByCafeId(cafe.getId());
        Review review = reviews.get(0);

        Set<String> updateCategories = new HashSet<>();
        updateCategories.add("직원이 친절한");
        updateCategories.add("조용한");

        ReviewUpdateDto reviewUpdateDto = new ReviewUpdateDto("업데이트된 리뷰 내용", 4.2, updateCategories);

        //when
//        Long updatedReviewId = reviewService.updateReview(review.getId(), reviewUpdateDto);

        //then
//        Review updatedReview = reviewRepository.findById(updatedReviewId).get();
//
//        assertNotNull(updatedReview);
//        assertEquals("업데이트된 리뷰 내용", updatedReview.getContent());
//        assertEquals(4.2, updatedReview.getScore());
//        assertEquals(2, updatedReview.getReviewCategories().size());
//        assertTrue(updatedReview.getReviewCategories().stream()
//                .anyMatch(rc -> rc.getCategory().getName().equals("직원이 친절한")));
//        assertTrue(updatedReview.getReviewCategories().stream()
//                .anyMatch(rc -> rc.getCategory().getName().equals("조용한")));
//
//        Cafe updatedCafe = cafeRepository.findById(cafe.getId()).get();
//        Set<String> topCategories = updatedCafe.getTopCategories();
//        assertTrue(topCategories.contains("직원이 친절한"));
//        assertTrue(topCategories.contains("조용한"));
    }
}