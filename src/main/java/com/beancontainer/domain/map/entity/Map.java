package com.beancontainer.domain.map.entity;

import com.beancontainer.domain.mapcafe.entity.MapCafe;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Table(name = "maps")
@Getter
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_name", nullable = false)
    private String mapName;

    @Column(nullable = false)
    private String username;

    @OneToMany
    @JoinColumn(name = "map_id")
    private List<MapCafe> mapCafes;

    public Map(String mapName, String username) {
        this.mapName = mapName;
        this.username = username;
    }

    protected Map() {}
}
