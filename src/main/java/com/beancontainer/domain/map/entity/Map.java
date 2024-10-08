package com.beancontainer.domain.map.entity;

import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maps")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "map_name", nullable = false)
    private String mapName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_public")
    private Boolean isPublic;


    public Map(String mapName, Member member) {
        this.mapName = mapName;
        this.member = member;
    }

    public Map(String mapName, Member member, boolean isPublic) {
        this.mapName = mapName;
        this.member = member;
        this.isPublic = isPublic;
    }

    public void updateMap(String mapName, boolean isPublic) {
        if (mapName != null) {
            this.mapName = mapName;
        }
            this.isPublic = isPublic;

    }
}
