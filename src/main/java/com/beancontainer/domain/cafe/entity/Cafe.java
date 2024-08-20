package com.beancontainer.domain.cafe.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cafes")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    //위도
    @Column(nullable = false)
    private double latitude;
    //경도
    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(name = "is_brand", nullable = false)
    private Boolean isBrand;

    @ElementCollection
    @Column(name = "categories")
    private Set<String> topCategories = new HashSet<>();

    private static final Set<String> BRANDS = Set.of(
            "스타벅스", "메가커피", "투썸플레이스", "빽다방", "컴포즈커피",
            "이디야", "할리스", "파스쿠찌", "더벤티", "폴바셋",
            "커피빈", "엔제리너스", "카페베네", "하삼동커피", "감성커피",
            "매머드커피", "달콤커피", "탐앤탐스", "커피나무", "커피베이",
            "커피명가", "드롭탑", "커피에반하다", "커피스미스", "커피마마",
            "더카페", "만랩커피", "셀렉토커피", "토프레소", "빈스빈스",
            "그라찌에", "전광수커피", "카페보니또", "더착한커피"
    );



    public Cafe(String kakaoId, String name, String address, double latitude, double longitude, String city, String district, Boolean isBrand) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.district = district;
        this.isBrand = BRANDS.contains(name);
    }

    public Cafe(String kakaoId, String name, String address, double latitude, double longitude, String city, String district) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.district = district;
        this.isBrand = BRANDS.contains(name);
    }

}
