package com.beancontainer.domain.map.entity;

import com.beancontainer.domain.mapcafe.entity.MapCafe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maps")
@Getter
@AllArgsConstructor
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_name", nullable = false)
    private String mapName;

    @Column(nullable = false)
    private String username;


    public Map(String mapName, String username) {
        this.mapName = mapName;
        this.username = username;
    }

    public void updateMap(String mapName) {
        if (mapName != null) {
            this.mapName = mapName;
        }
    }

    protected Map() {}
}
