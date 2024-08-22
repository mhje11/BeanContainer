package com.beancontainer.domain.review.dto;

import com.beancontainer.domain.category.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReviewUpdateDto {
    private String content;
    private Double score;
    private Set<String> categoryNames;

    public ReviewUpdateDto(String content, Double score, Set<String> categoryNames) {
        this.content = content;
        this.score = score;
        this.categoryNames = categoryNames;
    }
}
