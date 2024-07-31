package com.beancontainer.domain.map.entity;

import com.beancontainer.domain.cafe.entity.Cafe;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "maps")
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_name", nullable = false)
    private String mapName;

    @Column(nullable = false)
    private String username;

}
