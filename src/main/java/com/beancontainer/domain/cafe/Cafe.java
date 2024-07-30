package com.beancontainer.domain.cafe;

import com.beancontainer.domain.review.Review;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    //위도
    private double latitude;
    //경도
    private double longitude;
    private String city;
    private String district;
    private String neighborhood;

}
