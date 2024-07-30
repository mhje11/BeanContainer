package com.beancontainer.domain.cafe;
import jakarta.persistence.*;

@Entity
@Table(name = "cafes")
public class Cafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
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

    @Column(nullable = false)
    private String neighborhood;

}
