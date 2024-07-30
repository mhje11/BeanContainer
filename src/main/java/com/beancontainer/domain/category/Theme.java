package com.beancontainer.domain.category;

public enum Theme implements Category{
    AMBIENT("분위기 좋은"),
    INSTAGRAM("인스타 감성"),
    GREAT_VIEW("풍경이 좋은"),
    GRAND_OPEN("새로 오픈"),
    QUIET("조용한");


    private String categoryName;
    Theme(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }
}