package com.beancontainer.domain.cafe.entity;
import com.beancontainer.domain.cafecategory.CafeCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cafes")
@AllArgsConstructor
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

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CafeCategory> cafeCategories = new HashSet<>();


    public Cafe(String kakaoId, String name, String address, double latitude, double longitude, String city, String district) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.district = district;
    }


    protected Cafe() {}
}
