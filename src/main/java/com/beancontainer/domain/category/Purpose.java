package com.beancontainer.domain.category;

public enum Purpose implements Category{
    GOOD_DATE_COURSE("데이트 하기 좋은"),
    ALONE("혼자가기 좋은"),
    STUDY_FRIENDLY("공부하기 좋은"),
    BUSINESS("비즈니스 미팅"),
    PET_TOGETHER("애견 동반");

    private String categoryName;
    Purpose(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }
}
