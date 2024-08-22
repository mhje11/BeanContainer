package com.beancontainer.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReviewCreateDto {

    private String content;
    private Double score;
    private Long cafeId;
    private Set<String> categoryNames;

    public ReviewCreateDto(String content, Double score, Long cafeId, Set<String> categoryNames) {
        this.content = content;
        this.score = score;
        this.cafeId = cafeId;
        this.categoryNames = categoryNames;
    }
}
