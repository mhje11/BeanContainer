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
}
