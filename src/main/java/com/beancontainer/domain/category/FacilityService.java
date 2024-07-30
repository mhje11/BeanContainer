package com.beancontainer.domain.category;

public enum FacilityService implements Category{
    BIG("대형카페"),
    COMFORTABLE_TABLE("편한 좌석"),
    PARKING("주차가 가능한"),
    ALL_DAY("24시간 카페"),
    PRIVATE_ROOM("룸"),
    TERRACE("테라스"),
    WIFI("와이파이");

    private String categoryName;
    FacilityService(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String getCategoryName() {
        return categoryName;
    }
}
