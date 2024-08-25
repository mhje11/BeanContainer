package com.beancontainer.domain.review.dto;

import com.beancontainer.domain.review.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String nickName;
    private String content;
    private Double score;
    private Set<String> categoryNames;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.nickName = review.getMember().getNickname();
        this.content = review.getContent();
        this.score = review.getScore();
        this.categoryNames = review.getReviewCategories().stream()
                .map(reviewCategory -> reviewCategory.getCategory().getName())
                .collect(Collectors.toSet());
    }

    public ReviewResponseDto(Long id, String nickName, String content, Double score, List<String> categoryNames) {
        this.id = id;
        this.nickName = nickName;
        this.content = content;
        this.score = score;
        this.categoryNames = new HashSet<>(categoryNames);
    }
}
