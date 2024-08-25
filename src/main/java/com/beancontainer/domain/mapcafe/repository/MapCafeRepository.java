package com.beancontainer.domain.mapcafe.repository;

import com.beancontainer.domain.mapcafe.entity.MapCafe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapCafeRepository extends JpaRepository<MapCafe, Long>, CustomMapCafeRepository {
    List<MapCafe> findAllByMapId(Long mapId);
    List<MapCafe> findAllByCafeId(Long cafeId);
}
