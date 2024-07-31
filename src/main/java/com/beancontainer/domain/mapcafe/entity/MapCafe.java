package com.beancontainer.domain.mapcafe.entity;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.map.entity.Map;
import jakarta.persistence.*;

@Table(name = "map_cafes")
@Entity
public class MapCafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "map_id")
    private Map map;

    @ManyToOne
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;
}
