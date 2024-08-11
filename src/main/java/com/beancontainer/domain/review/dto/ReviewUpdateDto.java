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
}
