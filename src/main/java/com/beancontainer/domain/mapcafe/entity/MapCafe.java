package com.beancontainer.domain.mapcafe.entity;

import com.beancontainer.domain.cafe.entity.Cafe;
import com.beancontainer.domain.map.entity.Map;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "map_cafes")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MapCafe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    public MapCafe(Map map, Cafe cafe) {
        this.map = map;
        this.cafe = cafe;
    }

}
