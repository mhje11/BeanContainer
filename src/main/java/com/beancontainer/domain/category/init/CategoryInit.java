package com.beancontainer.domain.category.init;

import com.beancontainer.domain.cafe.repository.CafeRepository;
import com.beancontainer.domain.category.entity.Category;
import com.beancontainer.domain.category.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryInit {
    private final CategoryRepository categoryRepository;

    @PostConstruct
    @Transactional
    public void init() {
        List<String> categories = Arrays.asList(
                "대형카페", "편한 좌석", "주차가 가능한", "24시간 카페", "룸", "테라스",
                "와이파이", "데이트 하기 좋은", "혼자가기 좋은", "공부하기 좋은", "비즈니스 미팅",
                "애견 동반", "분위기 좋은", "인스타 감성", "풍경이 좋은", "새로 오픈", "조용한",
                "커피가 맛있는", "디저트가 맛있는", "직원이 친절한"
        );

        categories.forEach(name -> {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(new Category(name));
            }
        });
    }
}
