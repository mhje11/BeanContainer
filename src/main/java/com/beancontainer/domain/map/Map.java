package com.beancontainer.domain.map;

import com.beancontainer.domain.cafe.Cafe;
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

    @OneToMany
    @JoinColumn(name = "map_id", nullable = false)
    private List<Cafe> cafes;
}
